package com.example.oa.biz.service.process

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.process.Process
import com.example.oa.model.process.ProcessType
import com.example.oa.model.vo.ProcessDetailVo
import com.example.oa.model.vo.process.ApprovalVo
import com.example.oa.model.vo.process.ProcessFormVo
import com.example.oa.model.vo.process.ProcessQueryVo
import com.example.oa.model.vo.process.ProcessVo

interface ProcessService : IService<Process> {
    /**
     * 带条件的分页查询
     */
    fun pageQueryProcess(page: Long, limit: Long, processQueryVo: ProcessQueryVo?): PageResult<ProcessVo>

    /**
     * 查询审批分类和模板
     */
    fun getProcessTypeAndTemplate(): List<ProcessType>

    /**
     * 启动指定流程实例
     */
    fun startProcess(processFormVo: ProcessFormVo): Boolean

    /**
     * 查询待处理的流程任务
     */
    fun getPendingProcess(page: Long, limit: Long): PageResult<ProcessVo>

    /**
     * 获取审批的详情信息
     */
    fun getProcessDetail(processId: Long): Result<ProcessDetailVo>

    /**
     * 进行审批
     */
    fun doProcessApprove(approvalVo: ApprovalVo): Boolean

    /**
     * 获取已被处理的流程
     */
    fun getProcessedProcessPage(page: Long, limit: Long): PageResult<ProcessVo>

    /**
     * 获取用户发起的流程
     */
    fun getLaunchedProcess(page: Long, limit: Long): PageResult<ProcessVo>
}