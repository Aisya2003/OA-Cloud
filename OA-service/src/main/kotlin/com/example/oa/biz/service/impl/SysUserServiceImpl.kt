package com.example.oa.biz.service.impl

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.SysUserMapper
import com.example.oa.biz.service.SysUserService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.system.SysUser
import com.example.oa.model.vo.system.LoginVo
import com.example.oa.model.vo.system.SysUserQueryVo
import com.example.oa.model.vo.wechat.BindPhoneVo
import com.example.oa.security.to.CustomUser
import com.example.oa.util.jwt.JWTUtil
import com.example.oa.utils.QueryPageUtil
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class SysUserServiceImpl : SysUserService, ServiceImpl<SysUserMapper, SysUser>() {
    override fun queryUserPageByParams(page: Long, limit: Long, sysUseQueryVo: SysUserQueryVo): PageResult<SysUser> {
        val pageRecord = QueryPageUtil.getPage(
            page, limit, this,
            sysUseQueryVo.let {
                val queryWrapper = LambdaQueryWrapper<SysUser>()
                if (it.keyword != null) {
                    queryWrapper
                        .like(it.keyword.isNotBlank(), SysUser::getName, it.keyword)
                        .or()
                        .like(it.keyword.isNotBlank(), SysUser::getUsername, it.keyword)
                        .or()
                        .like(it.keyword.isNotBlank(), SysUser::getPhone, it.keyword)
                }
                if (it.createTimeBegin != null && it.createTimeEnd != null) {
                    queryWrapper.and { wrapper ->
                        wrapper.between(
                            it.createTimeBegin.isNotBlank() && it.createTimeEnd.isNotBlank(),
                            SysUser::getCreateTime,
                            it.createTimeBegin, it.createTimeEnd
                        )
                    }
                } else null
            }
        )
        return PageResult(page, limit, pageRecord.total, pageRecord.records)
    }

    override fun upStatusByUserId(userId: Long, status: Int): Boolean {
        if (status != 0 && status != 1) return false
        this.getById(userId)?.let {
            it.status = status
            this@SysUserServiceImpl.updateById(it)
            return true
        } ?: return false
    }

    override fun getUserByLoginVo(loginVo: LoginVo): SysUser? {
        val sysUser = loginVo.username?.let {
            this.getOne(LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, it))
        } ?: return null
        return loginVo.password?.let {
            if (it == sysUser.password) sysUser
            else null
        }
    }

    override fun getUserByUsername(username: String?): SysUser? {
        return this.getOne(LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username))
    }

    override fun bindPhoneAndOpenId(bindPhoneVo: BindPhoneVo): String? {
        val sysUser = this.getOne(LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, bindPhoneVo.phone))
        return sysUser?.let {
            it.openId = bindPhoneVo.openId
            this.updateById(sysUser)
            JWTUtil.generateJWT(it.id, it.username)
        }
    }
}