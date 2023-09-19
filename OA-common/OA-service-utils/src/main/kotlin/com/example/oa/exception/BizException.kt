package com.example.oa.exception

class BizException constructor(
    var code: Int? = null,
    var msg: String? = null
) : RuntimeException(msg) {
    override fun toString(): String {
        return "BizException(code=$code, msg=$msg)"
    }
}