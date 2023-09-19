package com.example.oa.biz.service.impl.wechat

import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.config.properties.WechatProperties
import com.example.oa.biz.mapper.wechat.MenuMapper
import com.example.oa.biz.service.wechat.MenuService
import com.example.oa.model.vo.wechat.MenuVo
import com.example.oa.model.wechat.Menu
import me.chanjar.weixin.mp.api.WxMpService
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service

@Service
class MenuServiceImpl(
    private val wxMpService: WxMpService,
    private val wechatProperties: WechatProperties
) : ServiceImpl<MenuMapper, Menu>(), MenuService {
    override fun getMenuTreeNodes(): List<MenuVo> {
        val allMenus = this.list()
        val parentNode = allMenus.filter { it.parentId == 0L }
        //只有两级菜单
        return parentNode.map {
            MenuVo().apply {
                BeanUtils.copyProperties(it, this)
                this.children = allMenus
                    .filter { it.parentId == this.id }
                    .map { menu -> MenuVo().apply { BeanUtils.copyProperties(menu, this) } }
            }
        }
    }

    override fun synchronize(): Boolean {
        //构建微信菜单请求体
        val treeNodes = this.getMenuTreeNodes()
        val buttonList = treeNodes.map {
            JSONObject().apply {
                this["name"] = it.name
                if (it.children.isNullOrEmpty().not()) {
                    this["sub_button"] = JSONArray().apply {
                        it.children.map { vo ->
                            this.add(JSONObject().apply {
                                this["type"] = vo.type
                                if (vo.type.equals("view")) {
                                    this["name"] = vo.name
                                    this["url"] = "${wechatProperties.employeeHost}${vo.url}"
                                } else {
                                    this["name"] = vo.name
                                    this["key"] = vo.menuKey
                                }
                            })
                        }
                    }
                } else {
                    this["type"] = it.type
                    this["url"] = "${wechatProperties.employeeHost}${it.url}"
                }
            }
        }
        val wxStruct = JSONObject().apply { this["button"] = buttonList }
        //发起同步请求
        wxMpService.menuService.menuCreate(wxStruct.toJSONString())
        return true
    }

    override fun removeWxMenu(): Boolean {
        wxMpService.menuService.menuDelete()
        return true
    }
}