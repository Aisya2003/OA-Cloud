package com.example.oa.biz.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.SysRoleMenuMapper
import com.example.oa.biz.service.SysRoleMenuService
import com.example.oa.model.system.SysRoleMenu
import org.springframework.stereotype.Service

@Service
class SysRoleMenuServiceImpl : SysRoleMenuService, ServiceImpl<SysRoleMenuMapper, SysRoleMenu>() {
}