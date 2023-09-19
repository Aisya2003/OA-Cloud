package com.example.oa.biz.service.impl.process

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.process.ProcessRecordMapper
import com.example.oa.biz.service.SysUserService
import com.example.oa.biz.service.process.ProcessRecordService
import com.example.oa.model.process.ProcessRecord
import com.example.oa.model.thread.CurrentUser
import org.springframework.stereotype.Service

@Service
class ProcessRecordServiceImpl(private val sysUserService: SysUserService) :
    ServiceImpl<ProcessRecordMapper, ProcessRecord>(), ProcessRecordService {
    override fun saveProcessRecord(processId: Long, status: Int, desc: String) {
        val processRecord = ProcessRecord().apply {
            this.processId = processId
            this.status = status
            this.description = desc
            this.operateUser = sysUserService.getById(CurrentUser.userId.get()).name
            this.operateUserId = CurrentUser.userId.get()
        }
        this.save(processRecord)
    }
}