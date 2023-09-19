package com.example.oa.biz.service.impl.auth

import com.alibaba.fastjson2.JSON
import com.example.oa.model.constant.ResultEnum
import com.example.oa.model.dto.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import java.nio.charset.StandardCharsets

class CustomAuthDenyHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        response.writer.write(JSON.toJSONString(Result.fail(ResultEnum.NO_PERMISSION.code, "用户没有权限执行")))
    }
}