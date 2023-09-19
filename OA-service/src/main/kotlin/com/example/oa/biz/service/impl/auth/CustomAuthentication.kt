package com.example.oa.biz.service.impl.auth

import com.alibaba.fastjson2.JSON
import com.example.oa.model.constant.ResultEnum
import com.example.oa.model.dto.Result
import com.example.oa.model.thread.CurrentUser
import com.example.oa.model.vo.system.LoginVo
import com.example.oa.security.to.CustomUser
import com.example.oa.util.jwt.JWTUtil
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

class CustomAuthentication constructor(
    authenticationManager: AuthenticationManager,
) : UsernamePasswordAuthenticationFilter(authenticationManager) {
    init {
        this.authenticationManager = authenticationManager
        this.setPostOnly(true)
        //指定提交的方式和路径
        this.setRequiresAuthenticationRequestMatcher(
            AntPathRequestMatcher(
                "/admin/system/index/login",
                HttpMethod.POST.name()
            )
        )
    }

    /**
     * 尝试认证
     */
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication? {
        return request?.let {
            //获取认证信息
            val loginVo: LoginVo = JSON.parseObject(it.inputStream, LoginVo::class.java)
            val authentication: Authentication =
                UsernamePasswordAuthenticationToken(loginVo.username, loginVo.password)
            //认证
            this.authenticationManager.authenticate(authentication)
        }
    }

    /**
     * 认证成功
     */
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val user: CustomUser = authResult?.principal as CustomUser
        val token = user.let {
            CurrentUser.userId.set(user.sysUser.id)
            CurrentUser.username.set(user.sysUser.username)
            JWTUtil.generateJWT(user.sysUser.id, user.sysUser.username)
        }
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        JSON.writeTo(response.outputStream, Result.ok(mapOf("token" to token)))
    }

    /**
     * 认证失败
     */
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        failed: AuthenticationException?
    ) {
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        response.writer.write(JSON.toJSONString(Result.fail(ResultEnum.NO_AUTH.code, "登录失败")))
    }
}