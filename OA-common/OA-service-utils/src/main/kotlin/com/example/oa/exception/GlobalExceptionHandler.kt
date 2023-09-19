package com.example.oa.exception

import com.example.oa.model.constant.ResultEnum
import com.example.oa.model.dto.Result
import io.jsonwebtoken.ExpiredJwtException
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice(basePackages = ["com.example.oa"])
class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(BizException::class)
    fun handleBizException(exception: BizException): Result<Unit> {
        return Result.fail(exception.code, exception.msg)
    }

    @ResponseBody
    @ExceptionHandler(BizException::class)
    fun handleBizException(exception: AccessDeniedException): Result<Unit> {
        return Result.fail(ResultEnum.NO_PERMISSION.code, ResultEnum.NO_PERMISSION.msg)
    }

    @ResponseBody
    @ExceptionHandler(ExpiredJwtException::class)
    fun handleJWTExpireException(exception: AccessDeniedException): Result<Unit> {
        return Result.fail(ResultEnum.NO_PERMISSION.code, "认证时间已到期，请重新认证")
    }

    @ResponseBody
    @ExceptionHandler(Exception::class)
    fun handleUnknownException(): Result<Unit> {
        return Result.fail("服务器异常")
    }
}