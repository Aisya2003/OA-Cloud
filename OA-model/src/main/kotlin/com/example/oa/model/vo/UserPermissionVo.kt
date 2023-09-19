package com.example.oa.model.vo

import com.example.oa.model.vo.system.RouterVo

class UserPermissionVo {
    var roles: List<String>? = null
    var name: String? = null
    var avatar: String? = null
    var routers: List<RouterVo>? = null
    var buttons: List<String>? = null
}