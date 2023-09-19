package com.example.oa.model.dto

import com.example.oa.model.constant.ResultEnum

class PageResult<T>(page: Long?, limit: Long?, total: Long?, data: List<T>?, code: Int?) {
    constructor(page: Long?, limit: Long?, total: Long?, data: List<T>?) : this(
        page,
        limit,
        total,
        data,
        ResultEnum.SUCCESS.code
    )

    var page: Long? = null
    var limit: Long? = null
    var total: Long? = null
    var data: List<T>? = null
    var code: Int? = null

    init {
        this.total = total
        this.limit = limit
        this.page = page
        this.data = data
        this.code = code
    }
}