package com.example.oa.biz.controller

import com.example.oa.biz.service.SysUserService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.system.SysUser
import com.example.oa.model.vo.system.SysUserQueryVo
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/system/sysUser")
class SysUserController(
    private val sysUserService: SysUserService,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * 更新用户的状态
     */
    @GetMapping("/updateStatus/{userId}/{status}")
    @PreAuthorize("hasAuthority('btn.sysUser.update')")
    fun updateUserStatus(
        @PathVariable("userId") userId: Long,
        @PathVariable("status") status: Int
    ): Result<Unit> {
        return if (sysUserService.upStatusByUserId(userId, status)) Result.ok()
        else Result.fail()
    }

    @GetMapping("/{page}/{limit}")
    @PreAuthorize("hasAuthority('btn.sysUser.list')")
    fun pageQueryUser(
        @PathVariable("page") page: Long,
        @PathVariable("limit") limit: Long,
        sysUserQueryVo: SysUserQueryVo
    ): PageResult<SysUser> {
        return sysUserService.queryUserPageByParams(page, limit, sysUserQueryVo);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('btn.sysUser.list')")
    operator fun get(@PathVariable id: Long?): Result<SysUser> {
        val user: SysUser = sysUserService.getById(id)
        return Result.ok(user)
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('btn.sysUser.add')")
    fun save(@RequestBody user: SysUser?): Result<Unit> {
        user?.let {
            it.password = passwordEncoder.encode(it.password)
        } ?: return Result.fail("信息不完整")
        return if (sysUserService.save(user)) Result.ok()
        else Result.fail()
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('btn.sysUser.update')")
    fun updateById(@RequestBody user: SysUser?): Result<Unit> {
        return if (sysUserService.updateById(user)) Result.ok()
        else Result.fail()
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('btn.sysUser.remove')")
    fun remove(@PathVariable id: Long?): Result<Unit> {
        return if (sysUserService.removeById(id)) Result.ok()
        else Result.fail()
    }
}
