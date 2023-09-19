package com.example.oa.model.vo

class UploadFileResponseVo(processDefinitionPath: String, processDefinitionKey: String) {

    var processDefinitionPath: String? = null
    var processDefinitionKey: String? = null

    init {
        this.processDefinitionPath = processDefinitionPath
        this.processDefinitionKey = processDefinitionKey
    }
}