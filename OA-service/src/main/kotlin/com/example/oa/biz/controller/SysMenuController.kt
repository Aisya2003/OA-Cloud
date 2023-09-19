package com.example.oa.biz.controller

import com.example.oa.biz.service.SysMenuService
import com.example.oa.model.system.SysMenu
import org.springframework.web.bind.annotation.*
import com.example.oa.model.dto.Result
import com.example.oa.model.vo.system.AssignMenuVo
import org.springframework.security.access.prepost.PreAuthorize


@RestController
@RequestMapping("/admin/system/sysMenu")
class SysMenuController(
    private val sysMenuService: SysMenuService
) {
    /**
     * 查询所有菜单的树形节点
     */
    @GetMapping("/findNodes")
    @PreAuthorize("hasAuthority('btn.sysMenu.list')")
    fun findNodes(): Result<List<SysMenu>>? {
        val list: List<SysMenu> = sysMenuService.findNodes()
        return Result.ok(list)
    }

    /**
     * 查询角色拥有的菜单
     */
    @GetMapping("/toAssign/{roleId}")
    @PreAuthorize("hasAuthority('btn.sysMenu.list')")
    fun getMenuByRoleId(@PathVariable("roleId") roleId: Long): Result<List<SysMenu>> {
        return Result.ok(sysMenuService.findMenuListByRoleId(roleId))
    }

    /**
     * 为角色分配菜单
     */
    @PostMapping("/doAssign")
    @PreAuthorize("hasAuthority('btn.sysMenu.update')")
    fun assignRoleMenu(@RequestBody assignMenuVo: AssignMenuVo): Result<Unit> {
        return if (sysMenuService.assignRoleMenu(assignMenuVo)) Result.ok()
        else Result.fail("分配失败，请稍后重新尝试")
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('btn.sysRole.assignAuth')")
    fun save(@RequestBody sysMenu: SysMenu?): Result<Unit> {
        return if (sysMenuService.save(sysMenu)) Result.ok()
        else Result.fail()
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('btn.sysMenu.update')")
    fun updateById(@RequestBody sysMenu: SysMenu?): Result<Unit> {
        return if (sysMenuService.updateById(sysMenu)) Result.ok()
        else Result.fail()
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('btn.sysMenu.remove')")
    fun remove(@PathVariable id: Long): Result<Unit> {
        return if (sysMenuService.removePreventCascadeById(id)) Result.ok()
        else Result.fail("删除失败，被删除的菜单不能含有子菜单")
    }

}