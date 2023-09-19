package com.example.oa.biz.service.wechat

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.vo.wechat.MenuVo
import com.example.oa.model.wechat.Menu

interface MenuService : IService<Menu> {
    /**
     * 获取微信菜单的树形结构
     */
    fun getMenuTreeNodes(): List<MenuVo>

    /**
     * 将菜单同步到微信
     */
    fun synchronize(): Boolean

    /**
     * 删除所有的微信菜单
     */
    fun removeWxMenu(): Boolean
}