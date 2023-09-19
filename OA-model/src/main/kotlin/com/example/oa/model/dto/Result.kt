package com.example.oa.model.dto

import com.example.oa.model.constant.ResultEnum


class Result<T> private constructor(success: Boolean, code: Int, msg: String?, data: T?) {

    var success: Boolean? = null
    var code: Int? = null
    var msg: String? = null

    var data: T? = null

    init {
        this.let {
            it.success = success
            it.code = code
            it.msg = msg
            it.data = data
        }
    }


    companion object {
        @JvmStatic
        fun <T> ok(): Result<T> {
            return Result<T>(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, null)
        }

        @JvmStatic
        fun <T> ok(data: T): Result<T> {
            return Result<T>(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, data)
        }

        @JvmStatic
        fun <T> ok(code: Int, data: T): Result<T> {
            return Result<T>(true, code, ResultEnum.SUCCESS.msg, data)
        }

        @JvmStatic
        fun <T> ok(msg: String, data: T): Result<T> {
            return Result<T>(true, ResultEnum.SUCCESS.code, msg, data)
        }

        @JvmStatic
        fun <T> fail(): Result<T> {
            return Result<T>(true, ResultEnum.FAIL.code, ResultEnum.FAIL.msg, null)
        }

        @JvmStatic
        fun fail(code: Int?, msg: String?): Result<Unit> {
            return Result(true, code ?: ResultEnum.FAIL.code, msg, null)
        }

        @JvmStatic
        fun <T> fail(msg: String?): Result<T> {
            return Result<T>(true, ResultEnum.FAIL.code, msg, null)
        }
    }
}