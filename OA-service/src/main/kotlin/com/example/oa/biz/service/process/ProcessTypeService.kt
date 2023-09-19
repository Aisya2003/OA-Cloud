package com.example.oa.biz.service.process

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.process.ProcessType

interface ProcessTypeService : IService<ProcessType> {
    /**
     * 分页查询流程
     */
    fun pageQueryProcessType(page: Long, limit: Long): PageResult<ProcessType>
}