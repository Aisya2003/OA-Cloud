package com.example.oa.biz.service.impl.auth

import com.alibaba.fastjson2.JSON
import com.example.oa.biz.service.SysMenuService
import com.example.oa.exception.BizException
import com.example.oa.model.constant.ResultEnum
import com.example.oa.model.dto.Result
import com.example.oa.model.thread.CurrentUser
import com.example.oa.util.jwt.JWTUtil
import com.github.benmanes.caffeine.cache.Cache
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import kotlin.jvm.Throws

class CustomTokenAuthPerRequest(
    private val caffeine: Cache<Long, List<GrantedAuthority>>,
    private val sysMenuService: SysMenuService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        //登录请求放行
        if (AntPathMatcher().match("/admin/system/index/login", request.requestURI) ||
            AntPathMatcher().match("/admin/wechat/authorize", request.requestURI) ||
            AntPathMatcher().match("/admin/wechat/userInfo", request.requestURI) ||
            AntPathMatcher().match("/admin/wechat/bindPhone", request.requestURI)
        ) {
            filterChain.doFilter(request, response)
            return
        }
        //判断是否登录
        val authentication: UsernamePasswordAuthenticationToken? = this.getAuthenticationInfo(request)
        if (authentication == null) {
            response.status = HttpStatus.OK.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.writer.write(JSON.toJSONString(Result.fail(ResultEnum.NO_AUTH.code, ResultEnum.NO_AUTH.msg)))
            return
        } else {
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
        }
    }

    @Throws(Exception::class)
    private fun getAuthenticationInfo(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        return request.getHeader("token")?.let {
            if (it.isBlank() || it == "null") null
            else {
                val userId = try {
                    JWTUtil.getUserIdFromJWT(it)
                } catch (e: Exception) {
                    throw BizException(msg = "认证已失效，请重新登录")
                }
                CurrentUser.userId.set(userId)
                CurrentUser.username.set(JWTUtil.getUsernameFromJWT(it))
                var authorities = caffeine.getIfPresent(userId)
                //再次查询权限
                if (authorities.isNullOrEmpty() && userId != null) {
                    authorities = sysMenuService.getPermitActByUserId(userId)
                        .map { permission -> SimpleGrantedAuthority(permission.trim()) }
                    //存入缓存
                    caffeine.put(userId, authorities)
                }
                UsernamePasswordAuthenticationToken(JWTUtil.getUsernameFromJWT(it), null, authorities)
            }
        }
    }
}