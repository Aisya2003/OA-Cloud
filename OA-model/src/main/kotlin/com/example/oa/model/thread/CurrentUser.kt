package com.example.oa.model.thread

class CurrentUser {
    companion object{
        @JvmField
        val userId:ThreadLocal<Long> = ThreadLocal()
        @JvmField
        val username:ThreadLocal<String> = ThreadLocal()
    }
}