package com.example.oa.biz.service.wechat

interface MessageService {
    /**
     * 发送消息给负责审批的人
     */
    fun sendPendingMessage(processId: Long, userId: Long, taskId: String)

    /**
     * 审批完成，通知发起审批的人
     */
    fun sendFinishedMessage(processId: Long, userId: Long, status: Int)
}