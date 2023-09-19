package com.example.oa.biz.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "wechat")
class WechatProperties {
    var appId: String? = null
    var appsecret: String? = null
    var callbackUrl: String? = null
    var host: String? = null
    var employeeHost: String? = null
    var message: Message? = null

    class Message {
        var pendingApprove: String? = null
        var finishedApprove: String? = null
    }
}