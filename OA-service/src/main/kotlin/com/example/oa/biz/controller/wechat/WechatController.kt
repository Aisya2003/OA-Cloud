package com.example.oa.biz.controller.wechat

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.example.oa.biz.config.properties.WechatProperties
import com.example.oa.biz.service.SysUserService
import com.example.oa.model.dto.Result
import com.example.oa.model.system.SysUser
import com.example.oa.model.vo.wechat.BindPhoneVo
import com.example.oa.util.jwt.JWTUtil
import jakarta.servlet.http.HttpServletRequest
import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.mp.api.WxMpService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URLEncoder
import java.nio.charset.Charset

@Controller
@CrossOrigin
@RequestMapping("/admin/wechat")
class WechatController(
    private val sysUserService: SysUserService,
    private val wxMpService: WxMpService,
    private val wechatProperties: WechatProperties
) {
    /**
     * 微信授权
     */
    @GetMapping("/authorize")
    fun authorize(@RequestParam("returnUrl") returnUrl: String): String? {
//        println("authorized->")
//        println("returnUrl -> $returnUrl")
        val redirectUrl = wxMpService.oAuth2Service.buildAuthorizationUrl(
            wechatProperties.callbackUrl,
            WxConsts.OAuth2Scope.SNSAPI_USERINFO,
            URLEncoder.encode(
                returnUrl.replace("$", "#"),
                Charset.defaultCharset()
            )
        )
//        println("redirectUrl->${redirectUrl}")
        return "redirect:${redirectUrl}"
    }

    /**
     * 微信授权成功回调，获取用户信息
     */
    @RequestMapping("/userInfo")
    fun getUserInfo(@RequestParam("code") code: String, @RequestParam("state") returnUrl: String): String {
//        println("调用了userInfo redirectUrl->${returnUrl}")
        val token = wxMpService.oAuth2Service.getAccessToken(code)
        //获取openId对应的用户
        val sysUser = token.openId?.let {
            sysUserService.getOne(LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, it))
        }
        val jwtToken = sysUser?.let {
            JWTUtil.generateJWT(it.id, it.username)
        }
//        println("openId -> ${token.openId}")
//        println("redirect:${returnUrl}${if (returnUrl.contains("?")) "&" else "?"}token=${jwtToken ?: "null"}&openId=${token.openId}")
        return "redirect:${returnUrl}${if (returnUrl.contains("?")) "&" else "?"}token=${jwtToken ?: "null"}&openId=${token.openId}"
    }

    /**
     * 绑定手机号
     */
    @ResponseBody
    @PostMapping("/bindPhone")
    fun bindSysUserPhoneToOpenId(@RequestBody bindPhoneVo: BindPhoneVo): Result<String> {
        val token: String? = sysUserService.bindPhoneAndOpenId(bindPhoneVo)
        return token?.let {
            Result.ok(token)
        } ?: Result.fail("无法获取员工信息，请联系管理员")
    }
}