package com.example.oa.biz.config

import com.example.oa.biz.config.properties.WechatProperties
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import me.chanjar.weixin.mp.config.WxMpConfigStorage
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(WechatProperties::class)
class WechatConfiguration(private val wechatProperties: WechatProperties) {
    @Bean
    fun wxMpConfigStorage(): WxMpConfigStorage = WxMpDefaultConfigImpl().apply {
        this.appId = wechatProperties.appId
        this.secret = wechatProperties.appsecret
    }

    @Bean
    fun wxMpService(): WxMpService = WxMpServiceImpl().apply {
        this.wxMpConfigStorage = wxMpConfigStorage()
    }
}