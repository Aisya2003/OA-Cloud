package com.example.oa.biz.service.process

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.process.ProcessRecord

interface ProcessRecordService : IService<ProcessRecord> {
    /**
     * 保存审批的进度
     */
    fun saveProcessRecord(processId: Long, status: Int, desc: String)
}