package com.example.oa.model.vo

import com.example.oa.model.process.Process
import com.example.oa.model.process.ProcessRecord
import com.example.oa.model.process.ProcessTemplate

class ProcessDetailVo {
    var process: Process? = null
    var processRecordList: List<ProcessRecord>? = null
    var processTemplate: ProcessTemplate? = null
    var isApprove: Boolean? = null
}