package com.example.oa.biz.controller.process

import com.example.oa.biz.service.process.ProcessTemplateService
import com.example.oa.model.process.ProcessTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import com.example.oa.model.dto.Result
import com.example.oa.model.dto.PageResult
import com.example.oa.model.vo.UploadFileResponseVo
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/admin/process/processTemplate")
class ProcessTemplateController(private val processTemplateService: ProcessTemplateService) {
    /**
     * 发布流程
     */
    @GetMapping("/publish/{id}")
    fun publishProcessDefinition(@PathVariable("id") id: Long): Result<Unit> {
        return if (processTemplateService.publishProcessDefinition(id)) Result.ok()
        else Result.fail()
    }

    /**
     * 上传流程文件
     */
    @PostMapping("/uploadProcessDefinition")
    @PreAuthorize("hasAuthority('btn.processTemplate.templateSet')")
    fun uploadProcessFile(file: MultipartFile): Result<UploadFileResponseVo> {
        return processTemplateService.uploadProcessFile(file)
    }

    @GetMapping("/{page}/{limit}")
    @PreAuthorize("hasAuthority('btn.processTemplate.list')")
    fun index(
        @PathVariable("page") page: Long,
        @PathVariable("limit") limit: Long
    ): PageResult<ProcessTemplate> {
        return processTemplateService.queryProcessTemplatePageWithProcessTypeName(page, limit)
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('btn.processTemplate.list')")
    operator fun get(@PathVariable id: Long): Result<ProcessTemplate> {
        val processTemplate = processTemplateService.getById(id)
        return Result.ok(processTemplate)
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('btn.processTemplate.templateSet')")
    fun save(@RequestBody processTemplate: ProcessTemplate): Result<Unit> {
        return if (processTemplateService.save(processTemplate)) Result.ok()
        else Result.fail()
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('btn.processTemplate.templateSet')")
    fun updateById(@RequestBody processTemplate: ProcessTemplate): Result<Unit> {
        return if (processTemplateService.updateById(processTemplate)) Result.ok()
        else Result.fail()
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('btn.processTemplate.remove')")
    fun remove(@PathVariable id: Long): Result<Unit> {
        return if (processTemplateService.removeById(id)) Result.ok()
        else Result.fail()
    }
}