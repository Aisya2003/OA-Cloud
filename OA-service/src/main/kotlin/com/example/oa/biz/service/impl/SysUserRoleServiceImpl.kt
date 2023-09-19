package com.example.oa.biz.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.SysUserRoleMapper
import com.example.oa.biz.service.SysUserRoleService
import com.example.oa.model.system.SysUserRole
import org.springframework.stereotype.Service

@Service
class SysUserRoleServiceImpl : SysUserRoleService, ServiceImpl<SysUserRoleMapper, SysUserRole>() {
}