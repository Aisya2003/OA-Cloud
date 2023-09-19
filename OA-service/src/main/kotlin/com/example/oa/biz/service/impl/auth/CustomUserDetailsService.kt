package com.example.oa.biz.service.impl.auth

import com.example.oa.biz.service.SysMenuService
import com.example.oa.biz.service.SysUserService
import com.example.oa.exception.BizException
import com.example.oa.model.constant.StatusConstant
import com.example.oa.security.to.CustomUser
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val sysUserService: SysUserService,
    private val sysMenuService: SysMenuService,
    private val caffeine: Cache<Long, List<GrantedAuthority>>
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        val sysUser = sysUserService.getUserByUsername(username)
        return sysUser?.let {
            if (it.status == StatusConstant.DISABLED) throw BizException(msg = "账号被禁止使用")
            //获取权限
            val grantedAuthorityList = sysMenuService.getPermitActByUserId(it.id)
                .map { permission -> SimpleGrantedAuthority(permission.trim()) }
            caffeine.put(it.id, grantedAuthorityList)
            CustomUser(it, grantedAuthorityList)
        } ?: throw BizException(msg = "账号或密码错误")
    }
}