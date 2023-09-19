package com.example.oa.biz.controller

import com.example.oa.biz.service.SysMenuService
import com.example.oa.biz.service.SysUserService
import com.example.oa.exception.BizException
import com.example.oa.model.constant.ResultEnum
import com.example.oa.model.constant.StatusConstant
import com.example.oa.model.dto.Result
import com.example.oa.model.system.SysUser
import com.example.oa.model.vo.UserPermissionVo
import com.example.oa.model.vo.system.LoginVo
import com.example.oa.model.vo.system.RouterVo
import com.example.oa.util.jwt.JWTUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/system/index")
class IndexController(
    private val sysMenuService: SysMenuService
) {

    @GetMapping("/info")
    fun info(request: HttpServletRequest): Result<UserPermissionVo> {
        //获取请求头携带的用户信息
        val jwtToken = request.getHeader("token") ?: throw BizException(msg = "携带令牌非法！")
        val userId = JWTUtil.getUserIdFromJWT(jwtToken) ?: throw BizException(msg = "用户不存在！")
        val username = JWTUtil.getUsernameFromJWT(jwtToken)
        //获取用户可操作的菜单
        val accessibleRouter: List<RouterVo> = sysMenuService.getRouterVoByUserId(userId);
        //获取用户可操作的行为
        val accessibleBtn: List<String> = sysMenuService.getPermitActByUserId(userId);
        //封装返回的数据
        val userPermissionVo = UserPermissionVo().apply {
            this.avatar = "avatar"
            this.name = username
            this.roles = listOf("[admin]")
            this.buttons = accessibleBtn
            this.routers = accessibleRouter
        }
        return Result.ok(userPermissionVo)
    }

    @PostMapping("/logout")
    fun logout(): Result<Unit> {
        return Result.ok()
    }
}