package com.example.oa.biz.service

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.system.SysUser
import com.example.oa.model.vo.system.LoginVo
import com.example.oa.model.vo.system.SysUserQueryVo
import com.example.oa.model.vo.wechat.BindPhoneVo

interface SysUserService : IService<SysUser> {
    /**
     * 分页参数查询
     */
    fun queryUserPageByParams(page: Long, limit: Long, sysUseQueryVo: SysUserQueryVo): PageResult<SysUser>

    /**
     * 更新用户状态
     */
    fun upStatusByUserId(userId: Long, status: Int): Boolean

    /**
     * 获取用户ByVo
     */
    fun getUserByLoginVo(loginVo: LoginVo): SysUser?

    /**
     * 获取spring-security封装好的用户
     */
    fun getUserByUsername(username: String?): SysUser?

    /**
     * 绑定手机号和openId的关系并返回jwtToken
     */
    fun bindPhoneAndOpenId(bindPhoneVo: BindPhoneVo): String?
}