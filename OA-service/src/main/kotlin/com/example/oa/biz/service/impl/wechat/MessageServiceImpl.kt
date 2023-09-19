package com.example.oa.biz.service.impl.wechat

import com.alibaba.fastjson2.JSON
import com.example.oa.biz.config.properties.WechatProperties
import com.example.oa.biz.service.SysUserService
import com.example.oa.biz.service.process.ProcessService
import com.example.oa.biz.service.process.ProcessTemplateService
import com.example.oa.biz.service.wechat.MessageService
import com.example.oa.model.thread.CurrentUser
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class MessageServiceImpl(
    private val processService: ProcessService,
    private val sysUserService: SysUserService,
    private val processTemplateService: ProcessTemplateService,
    private val wechatProperties: WechatProperties,
    private val wxMpService: WxMpService
) : MessageService {
    override fun sendPendingMessage(processId: Long, userId: Long, taskId: String) {
        //准备发送的信息
        //获取提交的表格数据
        val process = processService.getById(processId)
        val processFormData = process?.let {
            JSON.parseObject(process.formValues).getJSONObject("formShowData").map {
                "${it.key}:${it.value}"
            }
        }?.joinToString(System.lineSeparator())
        //获取申请者的信息
        val launcher = sysUserService.getById(process.userId)
        //获取审批模板信息
        val processTemplateName = processTemplateService.getById(process.processTemplateId).name
        //获取消息接收者的openid
        val openId = sysUserService.getById(CurrentUser.userId.get())?.openId
        openId?.let {
            if (it.isBlank()) return
            val service = WxMpTemplateMessage.builder()
                //接收者的openId
                .toUser(it)
                //消息模板
                .templateId(wechatProperties.message?.pendingApprove)
                //点击跳转地址
                .url("${wechatProperties.employeeHost}/#/show/${processId}/${taskId}")
                .build()
            service.apply {
                addData(WxMpTemplateData().apply {
                    this.name = "first"
                    this.value = "【${launcher.name}】发起了【${processTemplateName}】申请，请注意查看"
                    this.color = "#0076ff"
                })
                addData(WxMpTemplateData().apply {
                    this.name = "keyword1"
                    this.value = process.processCode
                    this.color = "#0076ff"
                })
                addData(WxMpTemplateData().apply {
                    this.name = "keyword2"
                    this.value = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    this.color = "#0076ff"
                })
                addData(WxMpTemplateData().apply {
                    this.name = "content"
                    this.value = processFormData.toString()
                    this.color = "#0076ff"
                })
            }
            //发送消息
            wxMpService.templateMsgService.sendTemplateMsg(service)
        }
    }

    override fun sendFinishedMessage(processId: Long, userId: Long, status: Int) {
        val process = processService.getById(processId)
        val processTemplate = processTemplateService.getById(process.processTemplateId)
        val sysUser = sysUserService.getById(userId)
        val currentSysUser = sysUserService.getById(CurrentUser.userId.get())
        val openId = sysUser.openId
        val templateMessage = WxMpTemplateMessage.builder()
            .toUser(openId)//要推送的用户openid
            .templateId(wechatProperties.message?.finishedApprove)//模板id
            .url("${wechatProperties.employeeHost}/#/show/${processId}/0")
            .build()
        val jsonObject = JSON.parseObject(process.formValues)
        val formShowData = jsonObject.getJSONObject("formShowData")
        val content = StringBuffer()
        formShowData.forEach {
            content.append(it.key).append(":").append(it.value).append(System.lineSeparator())
        }
        templateMessage.addData(
            WxMpTemplateData(
                "first",
                "你发起的${processTemplate.name}审批申请已经被处理完成了，请注意查看。",
                "#272727"
            )
        );
        templateMessage.addData(WxMpTemplateData("keyword1", process.processCode, "#272727"));
        templateMessage.addData(
            WxMpTemplateData(
                "keyword2",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "#272727"
            )
        );
        templateMessage.addData(WxMpTemplateData("keyword3", currentSysUser.name, "#272727"));
        templateMessage.addData(
            WxMpTemplateData(
                "keyword4",
                if (status == 1) "审批通过" else "审批拒绝", if (status == 1) "#009966" else "#FF0033"
            )
        );
        templateMessage.addData(WxMpTemplateData("content", content.toString(), "#272727"));
        wxMpService.templateMsgService.sendTemplateMsg(templateMessage);
    }
}