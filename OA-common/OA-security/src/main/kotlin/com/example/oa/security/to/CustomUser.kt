package com.example.oa.security.to

import com.example.oa.model.system.SysUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUser(val sysUser: SysUser, authority: Collection<GrantedAuthority>) : User(
    sysUser.username,
    sysUser.password,
    authority
)