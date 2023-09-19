package com.example.oa.model.constant

enum class ResultEnum(val code: Int, val msg: String) {
    SUCCESS(200, "操作成功"),
    FAIL(201, "操作失败"),
    SERVICE_ERROR(202, "服务异常"),
    DATA_ERROR(203, "数据异常"),

    NO_AUTH(301, "登录异常"),
    NO_PERMISSION(302, "未授权")
}