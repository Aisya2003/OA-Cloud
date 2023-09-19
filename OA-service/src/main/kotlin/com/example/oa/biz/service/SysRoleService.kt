package com.example.oa.biz.service

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.system.SysRole
import com.example.oa.model.vo.system.AssignRoleVo
import com.example.oa.model.vo.system.SysRoleQueryVo

interface SysRoleService:IService<SysRole> {
    /**
     * 分页待条件查询系统角色
     */
    fun queryRolePageByParams(page: Long, limit: Long, sysRoleQueryVo: SysRoleQueryVo): PageResult<SysRole>

    /**
     * 查询用户的角色信息
     */
    fun getRoleMapByUserId(userId: Long): Map<String, List<Any>?>

    /**
     * 设置用户的角色信息
     */
    fun setUserRole(assignRoleVo: AssignRoleVo): Boolean
}