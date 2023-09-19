package com.example.oa.biz.controller.process.employee

import com.example.oa.biz.service.SysUserService
import com.example.oa.biz.service.process.ProcessService
import com.example.oa.biz.service.process.ProcessTemplateService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.process.ProcessTemplate
import com.example.oa.model.process.ProcessType
import com.example.oa.model.system.SysUser
import com.example.oa.model.thread.CurrentUser
import com.example.oa.model.vo.ProcessDetailVo
import com.example.oa.model.vo.process.ApprovalVo
import com.example.oa.model.vo.process.ProcessFormVo
import com.example.oa.model.vo.process.ProcessVo
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
@RequestMapping("/admin/process")
class EmployeeProcessController(
    private val processService: ProcessService,
    private val processTemplateService: ProcessTemplateService,
    private val sysUserService: SysUserService
) {
    /**
     * 查询审批分类和其下的模板
     */
    @GetMapping("/findProcessType")
    fun getProcessTypeAndTemplate(): Result<List<ProcessType>> {
        return Result.ok(processService.getProcessTypeAndTemplate())
    }

    /**
     * 获取模板信息
     */
    @GetMapping("/getProcessTemplate/{processTemplateId}")
    fun getProcessTemplateInfo(@PathVariable("processTemplateId") processTemplateId: Long): Result<ProcessTemplate> {
        return Result.ok(processTemplateService.getById(processTemplateId))
    }

    /**
     * 发起审批请求
     */
    @PostMapping("/startUp")
    fun startProcess(@RequestBody processFormVo: ProcessFormVo): Result<Unit> {
        return if (processService.startProcess(processFormVo)) Result.ok()
        else Result.fail()
    }

    /**
     * 获取待处理的流程信息
     */
    @GetMapping("/findPending/{page}/{limit}")
    fun getPendingProcess(@PathVariable("page") page: Long, @PathVariable("limit") limit: Long): PageResult<ProcessVo> {
        return processService.getPendingProcess(page, limit)
    }

    /**
     * 获取待处理的审批信息详情
     */
    @GetMapping("/show/{processId}")
    fun getProcessDetail(@PathVariable("processId") id: Long): Result<ProcessDetailVo> {
        return processService.getProcessDetail(id)
    }

    /**
     *审批
     */
    @PostMapping("/approve")
    fun doProcessApprove(@RequestBody approvalVo: ApprovalVo): Result<Unit> {
        return if (processService.doProcessApprove(approvalVo)) Result.ok()
        else Result.fail()
    }

    /**
     * 获取已处理过的审批信息
     *
     */
    @GetMapping("/findProcessed/{page}/{limit}")
    fun getProcessedProcess(
        @PathVariable("page") page: Long,
        @PathVariable("limit") limit: Long
    ): PageResult<ProcessVo> {
        return processService.getProcessedProcessPage(page, limit)
    }

    /**
     * 获取已发起的审批信息
     */
    @GetMapping("/findStarted/{page}/{limit}")
    fun getLanchedProcess(
        @PathVariable("page") page: Long,
        @PathVariable("limit") limit: Long
    ): PageResult<ProcessVo> {
        return processService.getLaunchedProcess(page, limit)
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/getCurrentUser")
    fun getCurrentUserInfo(): Result<SysUser> {
        return Result.ok(sysUserService.getById(CurrentUser.userId.get()))
    }
}