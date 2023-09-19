package com.example.oa.biz.service

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.system.SysMenu
import com.example.oa.model.vo.system.AssignMenuVo
import com.example.oa.model.vo.system.RouterVo

interface SysMenuService : IService<SysMenu> {
    /**
     * 查询多级菜单
     */
    fun findNodes(): List<SysMenu>

    /**
     * 删除菜单项，但是不会删除包含子菜单的菜单
     */
    fun removePreventCascadeById(id: Long): Boolean

    /**
     * 为角色分配菜单
     */
    fun assignRoleMenu(assignMenuVo: AssignMenuVo?): Boolean

    /**
     * 查询角色的所有菜单
     */
    fun findMenuListByRoleId(roleId: Long): List<SysMenu>

    /**
     * 给用户构建权限路由项
     */
    fun getRouterVoByUserId(userId: Long): List<RouterVo>

    /**
     * 获取用户的可操作项
     */
    fun getPermitActByUserId(userId: Long): List<String>
}