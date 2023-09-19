package com.example.oa.biz.controller.process

import com.example.oa.biz.service.process.ProcessService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.process.Process
import com.example.oa.model.vo.process.ProcessQueryVo
import com.example.oa.model.vo.process.ProcessVo
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/process")
class ProcessController(private val processService: ProcessService) {
    @GetMapping("/{page}/{limit}")
    @PreAuthorize("hasAuthority('btn.process.list')")
    fun pageQueryProcess(
        @PathVariable("page") page: Long,
        @PathVariable("limit") limit: Long,
        processQueryVo: ProcessQueryVo
    ): PageResult<ProcessVo> {
        return processService.pageQueryProcess(page, limit, processQueryVo)
    }
}