package com.example.oa.biz.service.impl.process

import com.alibaba.fastjson2.JSON
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.toolkit.IdWorker
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.process.ProcessMapper
import com.example.oa.biz.service.SysUserService
import com.example.oa.biz.service.process.ProcessRecordService
import com.example.oa.biz.service.process.ProcessService
import com.example.oa.biz.service.process.ProcessTemplateService
import com.example.oa.biz.service.process.ProcessTypeService
import com.example.oa.biz.service.wechat.MessageService
import com.example.oa.model.constant.ProcessStatusConstant
import com.example.oa.model.constant.ProcessStatusEnum
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.process.Process
import com.example.oa.model.process.ProcessRecord
import com.example.oa.model.process.ProcessTemplate
import com.example.oa.model.process.ProcessType
import com.example.oa.model.thread.CurrentUser
import com.example.oa.model.vo.ProcessDetailVo
import com.example.oa.model.vo.process.ApprovalVo
import com.example.oa.model.vo.process.ProcessFormVo
import com.example.oa.model.vo.process.ProcessQueryVo
import com.example.oa.model.vo.process.ProcessVo
import com.example.oa.utils.QueryPageUtil
import org.activiti.bpmn.model.EndEvent
import org.activiti.bpmn.model.FlowNode
import org.activiti.bpmn.model.SequenceFlow
import org.activiti.engine.HistoryService
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ProcessServiceImpl(
    private val processTemplateService: ProcessTemplateService,
    private val processTypeService: ProcessTypeService,
    private val sysUserService: SysUserService,
    private val runtimeService: RuntimeService,
    private val taskService: TaskService,
    private val processRecordService: ProcessRecordService,
    private val repositoryService: RepositoryService,
    private val historyService: HistoryService,
    private val messageService: MessageService
) : ServiceImpl<ProcessMapper, Process>(), ProcessService {
    override fun pageQueryProcess(page: Long, limit: Long, processQueryVo: ProcessQueryVo?): PageResult<ProcessVo> {
        val processPage = QueryPageUtil.getPage(page, limit, this, kotlin.run {
            if (processQueryVo == null) return@run null
            var queryWrapper: LambdaQueryWrapper<Process>? = null
            //状态查询
            processQueryVo.status?.let {
                queryWrapper = LambdaQueryWrapper<Process>()
                queryWrapper!!.eq(Process::getStatus, it)
            }
            //流程创建时间查询
            if (processQueryVo.createTimeBegin != null && processQueryVo.createTimeEnd != null) {
                if (queryWrapper == null) {
                    queryWrapper = LambdaQueryWrapper<Process>()
                }
                queryWrapper!!.between(
                    processQueryVo.createTimeBegin.isNotBlank() && processQueryVo.createTimeEnd.isNotBlank(),
                    Process::getCreateTime,
                    processQueryVo.createTimeBegin,
                    processQueryVo.createTimeEnd
                )
            }
            queryWrapper
        })
        //封装返回数据
        val processVoList = processPage.records.map {
            val processVo = ProcessVo()
            BeanUtils.copyProperties(it, processVo)
            //模板名称
            processVo.processTemplateName = processTemplateService.getById(it.processTemplateId).name
            //类型名称
            processVo.processTypeName = processTypeService.getById(it.processTypeId).name
            //申请人
            val user = sysUserService.getById(it.userId)
            processVo.name = user.name
            //手机号（用于模糊查询)
            processVo.phone = user.phone
            //返回
            processVo
        }.filter {
            if (processQueryVo == null || processQueryVo.keyword == null || processQueryVo.keyword.isBlank()) true
            else {
                //关键字模糊查询
                it.processCode.contains(processQueryVo.keyword, true) ||
                        it.title.contains(processQueryVo.keyword, true) ||
                        it.phone.contains(processQueryVo.keyword, true) ||
                        it.name.contains(processQueryVo.keyword, true)
            }
        }.sortedBy { it.id }
        return PageResult(page, limit, processVoList.size.toLong(), processVoList)
    }

    override fun getProcessTypeAndTemplate(): List<ProcessType> {
        return processTypeService.list().map {
            it.processTemplateList = processTemplateService.list(
                LambdaQueryWrapper<ProcessTemplate>().eq(
                    ProcessTemplate::getProcessTypeId,
                    it.id
                )
            )
            it
        }
    }

    override fun startProcess(processFormVo: ProcessFormVo): Boolean {
        //获取当前用户
        val sysUser = sysUserService.getById(CurrentUser.userId.get())
        //获取模板信息
        val processTemplate = processTemplateService.getById(processFormVo.processTemplateId)
        //审批信息
        val process = Process().apply {
            BeanUtils.copyProperties(processFormVo, this)
            this.status = ProcessStatusConstant.PROCESSING
            this.processCode = IdWorker.getTimeId()
            this.userId = sysUser.id
            this.formValues = processFormVo.formValues
            this.title = "【${sysUser.username}】于【${
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }】发起了【${processTemplate.name}】申请"
        }
        //保存审批消息，获取processId
        this.save(process)
        //获取表单数据
        val formData = JSON.parseObject(process.formValues).getJSONObject("formData")
        val paramsMap = mutableMapOf<String, Any>()
        formData.forEach {
            paramsMap[it.key] = it.value
        }
        val variables = mapOf<String, Any>("data" to paramsMap)
        //启动流程Instance
        val processInstance = runtimeService.startProcessInstanceByKey(
            processTemplate.processDefinitionKey,
            process.id.toString(),
            variables
        )
        //获取下一个负责人
        val taskList = taskService.createTaskQuery().processInstanceId(processInstance.id).list()
        val assignNameList = taskList.map {
            //获取审批人姓名
            val nextUser = sysUserService.getUserByUsername(it.assignee)
            //推送消息
            if (nextUser != null) {
                messageService.sendPendingMessage(process.id,nextUser.id,it.id)
            }
            nextUser?.name
        }
        //保存详情
        process.processInstanceId = processInstance.id
        process.description = "等待下一流程【${assignNameList.joinToString(",")}】审批中"

        //记录审批进程
        processRecordService.saveProcessRecord(
            process.id,
            ProcessStatusEnum.CREATED.code,
            ProcessStatusEnum.CREATED.desc
        )
        //更新审批信息
        return this.updateById(process)
    }

    override fun getPendingProcess(page: Long, limit: Long): PageResult<ProcessVo> {
        //获取当前用户的task信息
        val query = taskService.createTaskQuery()
            .taskAssignee(CurrentUser.username.get())
            .orderByTaskCreateTime()
            .desc()
        val processVoList =
            query.listPage(((page - 1) * limit).toInt(), limit.toInt()).map {
                val processInstance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(it.processInstanceId)
                    .singleResult()
                val process = this.getById(processInstance.businessKey)
                ProcessVo().apply {
                    BeanUtils.copyProperties(process, this)
                    this.taskId = it.id
                }
            }
        return PageResult(page, limit, query.count(), processVoList)
    }

    override fun getProcessDetail(processId: Long): Result<ProcessDetailVo> {
        //通过流程id获取Process
        val process = this.getById(processId)
        //获取ProcessRecord信息
        val processRecordList =
            processRecordService.list(LambdaQueryWrapper<ProcessRecord>().eq(ProcessRecord::getProcessId, processId))
        //获取processTemplate
        val processTemplate = processTemplateService.getById(process.processTemplateId)
        //判读用户是否可以审批
        val taskList = taskService.createTaskQuery().processInstanceId(process.processInstanceId).list()
        var isApprove = false
        taskList.forEach {
            if (it.assignee.equals(CurrentUser.username.get())) {
                isApprove = true
                return@forEach
            }
        }
        //封装数据
        return Result.ok(ProcessDetailVo().apply {
            this.process = process
            this.isApprove = isApprove
            this.processRecordList = processRecordList
            this.processTemplate = processTemplate
        })
    }

    override fun doProcessApprove(approvalVo: ApprovalVo): Boolean {
        taskService.getVariables(approvalVo.taskId).forEach {
            println("${it.key}->${it.value}")
        }
        var description: String = "通过"
        if (approvalVo.status == ProcessStatusConstant.PROCESSING) {
            //完成当前流程
            taskService.complete(approvalVo.taskId)
        } else {
            //被拒绝，直接结束流程
            this.rejectTask(approvalVo.taskId)
            description = "驳回"
        }
        //记录审批信息
        processRecordService.saveProcessRecord(
            approvalVo.processId,
            approvalVo.status,
            "处理了请求：【${description}】"
        )
        //通知下一个审批人
        val process = this.getById(approvalVo.processId)
        val taskList =
            taskService.createTaskQuery().processInstanceId(this.getById(approvalVo.processId).processInstanceId).list()
        if (taskList.isNullOrEmpty()) {
            //不存在下一个审批人，要么结束要么驳回
            process.status =
                if (approvalVo.status == ProcessStatusConstant.PROCESSING) ProcessStatusConstant.SUCCESS else ProcessStatusConstant.REJECTED
            process.description = "审批结束，审批结果：【${description}】"
            //消息推送给发起人
            messageService.sendFinishedMessage(process.id, process.userId, approvalVo.status);
        } else {
            //存在下一个审批人
            val nextAssignList = taskList.map {
                //获取下一个审批人集合
                val username = sysUserService.getUserByUsername(it.assignee)
                //微信消息推送
                if (username != null) {
                    messageService.sendPendingMessage(process.id,username.id,it.id)
                }
                username?.name
            }
            //更新流程信息
            process.status = ProcessStatusConstant.PROCESSING
            process.description = "等待下一流程【${nextAssignList.joinToString(",")}】审批中"
        }
        return this.updateById(process)
    }

    override fun getProcessedProcessPage(page: Long, limit: Long): PageResult<ProcessVo> {

        val query = historyService.createHistoricTaskInstanceQuery()
            .taskAssignee(CurrentUser.username.get())
            .finished()
            .orderByTaskCreateTime().desc()
        val processVoList =
            query.listPage(((page - 1) * limit).toInt(), limit.toInt())
                .map {
                    val process = this
                        .getOne(
                            LambdaQueryWrapper<Process>().eq(Process::getProcessInstanceId, it.processInstanceId)
                        )
                    ProcessVo().apply {
                        BeanUtils.copyProperties(process, this)
                    }
                }
        return PageResult(page, limit, query.count(), processVoList)
    }

    override fun getLaunchedProcess(page: Long, limit: Long): PageResult<ProcessVo> {
        return this.pageQueryProcess(page, limit, ProcessQueryVo().apply { this.userId = CurrentUser.userId.get() })
    }

    /**
     * 结束流程
     */
    private fun rejectTask(taskId: String?) {
        taskId?.let {
            //获取任务
            val task = taskService.createTaskQuery().taskId(it).singleResult()
            //获取流程模型
            val bpmnModel = repositoryService.getBpmnModel(task.processDefinitionId)
            //获取模型的结束节点
            val endEvents = bpmnModel.mainProcess.findFlowElementsOfType(EndEvent::class.java)
            if (endEvents.isNullOrEmpty()) return
            val endFlowNode = endEvents[0] as FlowNode
            //获取当前的流向节点
            val currentFlowNode = bpmnModel.mainProcess.getFlowElement(task.taskDefinitionKey) as FlowNode
            //清理当前流动方向
            currentFlowNode.outgoingFlows.clear()
            //指向结束节点
            val endSequenceFlow = SequenceFlow().apply {
                this.id = "endTaskId:[${taskId}]Flow"
                this.sourceFlowElement = currentFlowNode
                this.targetFlowElement = endFlowNode
            }
            currentFlowNode.outgoingFlows = listOf(endSequenceFlow)
            //完成当前任务
            taskService.complete(it)
        }
    }
}