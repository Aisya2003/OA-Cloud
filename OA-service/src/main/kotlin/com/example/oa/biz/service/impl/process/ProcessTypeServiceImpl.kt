package com.example.oa.biz.service.impl.process

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.process.ProcessTypeMapper
import com.example.oa.biz.service.process.ProcessTypeService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.process.ProcessType
import com.example.oa.utils.QueryPageUtil
import org.springframework.stereotype.Service

@Service
class ProcessTypeServiceImpl : ServiceImpl<ProcessTypeMapper, ProcessType>(), ProcessTypeService {
    override fun pageQueryProcessType(page: Long, limit: Long): PageResult<ProcessType> {
        val processTypeIPage = QueryPageUtil.getPage(page, limit, this, null)
        return PageResult(page, limit, processTypeIPage.total, processTypeIPage.records)
    }
}