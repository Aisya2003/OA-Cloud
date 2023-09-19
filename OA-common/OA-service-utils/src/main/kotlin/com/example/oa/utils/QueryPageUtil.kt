package com.example.oa.utils

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.vo.system.SysRoleQueryVo

class QueryPageUtil {
    companion object {
        @JvmStatic
        fun <T> getPage(
            page: Long,
            limit: Long,
            service: IService<T>,
            queryCondition: LambdaQueryWrapper<T>?
        ): IPage<T> {
            return service.page(Page<T>(page, limit), queryCondition)
                ?: service.page(Page<T>(page, limit))
        }
    }
}