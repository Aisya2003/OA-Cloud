package com.example.oa.biz.service.impl

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.SysRoleMapper
import com.example.oa.biz.service.SysRoleService
import com.example.oa.biz.service.SysUserRoleService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.system.SysRole
import com.example.oa.model.system.SysUserRole
import com.example.oa.model.vo.system.AssignRoleVo
import com.example.oa.model.vo.system.SysRoleQueryVo
import com.example.oa.utils.QueryPageUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SysRoleServiceImpl(
    private val sysUserRoleService: SysUserRoleService
) : SysRoleService, ServiceImpl<SysRoleMapper, SysRole>() {
    override fun queryRolePageByParams(page: Long, limit: Long, sysRoleQueryVo: SysRoleQueryVo): PageResult<SysRole> {
        val pageRecords = QueryPageUtil.getPage(
            page, limit, this,
            sysRoleQueryVo.roleName?.let {
                LambdaQueryWrapper<SysRole>()
                    .like(SysRole::getRoleName, it)
            }
        )
        return PageResult(page, limit, pageRecords.total, pageRecords.records);
    }

    override fun getRoleMapByUserId(userId: Long): Map<String, List<Any>?> {
        //所有角色
        val sysRoleList = this.list()
        //用户所拥有的角色
        val userRoleList = sysUserRoleService
            .list(LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
            ?.filter {
                sysRoleList.map { role -> role.id }.contains(it.roleId)
            }
        return mapOf("assignRoleList" to userRoleList, "allRolesList" to sysRoleList)
    }

    @Transactional
    override fun setUserRole(assignRoleVo: AssignRoleVo): Boolean {
        val sysUserRole = SysUserRole()
        //删除用户所有的角色
        assignRoleVo.let {
            it.userId?.let { userId ->
                sysUserRole.userId = userId
                sysUserRoleService.remove(LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
            } ?: return false
            //添加角色
            it.roleIdList?.let { roleIds ->
                roleIds.filterNotNull().forEach { roleId ->
                    sysUserRole.roleId = roleId
                    sysUserRole.createTime = LocalDateTime.now()
                    sysUserRoleService.save(sysUserRole)
                }
            } ?: return false
        }
        return true
    }
}