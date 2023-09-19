package com.example.oa.biz.controller


import com.example.oa.biz.service.SysRoleService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.system.SysRole
import com.example.oa.model.vo.system.AssignRoleVo
import com.example.oa.model.vo.system.SysRoleQueryVo
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/system/sysRole")
class SysRoleController(
    private val sysRoleService: SysRoleService
) {
    /**
     * 获取用户的角色
     */
    @GetMapping("/toAssign/{userId}")
    @PreAuthorize("hasAuthority('btn.sysRole.list')")
    fun getUserRole(@PathVariable("userId") userId: Long): Result<Map<String, List<Any>?>> {
        return Result.ok(sysRoleService.getRoleMapByUserId(userId))
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/doAssign")
    @PreAuthorize("hasAuthority('btn.sysUser.assignRole')")
    fun setUserRole(@RequestBody assignRoleVo: AssignRoleVo): Result<Unit> {
        return if (sysRoleService.setUserRole(assignRoleVo)) Result.ok()
        else Result.fail()
    }

    /**
     * 所有角色
     */
    @GetMapping("/findAll")
    @PreAuthorize("hasAuthority('btn.sysRole.list')")
    fun getAllRole(): Result<List<SysRole>> {
        return Result.ok(sysRoleService.list())
    }


    /**
     * 分页查询
     */
    @GetMapping("/{page}/{limit}")
    @PreAuthorize("hasAuthority('btn.sysRole.list')")
    fun pageQueryRole(
        @PathVariable("page") page: Long,
        @PathVariable("limit") limit: Long,
        sysRoleQueryVo: SysRoleQueryVo
    ): PageResult<SysRole> {
        return sysRoleService.queryRolePageByParams(page, limit, sysRoleQueryVo);
    }

    /**
     * 添加角色
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('btn.sysRole.add')")
    fun saveRole(@RequestBody sysRole: SysRole): Result<Unit> {
        return if (sysRoleService.save(sysRole)) Result.ok()
        else Result.fail("添加角色成功")
    }

    /**
     * 通过角色id查询
     */
    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('btn.sysRole.list')")
    fun getRoleById(@PathVariable("id") id: Long): Result<SysRole> {
        return sysRoleService.getById(id)?.let {
            Result.ok(it)
        } ?: Result.fail("角色不存在")
    }

    /**
     * 修改角色
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('btn.sysRole.update')")
    fun updateRole(@RequestBody sysRole: SysRole): Result<Unit> {
        return if (sysRoleService.updateById(sysRole)) Result.ok()
        else Result.fail("修改角色失败")
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('btn.sysRole.remove')")
    fun deleteRole(@PathVariable("id") id: Long): Result<Unit> {
        return if (sysRoleService.removeById(id)) Result.ok()
        else Result.fail("删除角色失败")
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batchRemove")
    @PreAuthorize("hasAuthority('btn.sysRole.remove')")
    fun deleteBatchRole(@RequestBody ids: List<Long>): Result<Unit> {
        return if (sysRoleService.removeBatchByIds(ids)) Result.ok()
        else Result.fail("删除角色失败")
    }
}