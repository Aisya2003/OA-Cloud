package com.example.oa.biz.controller.process

import com.example.oa.biz.service.process.ProcessTypeService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.process.ProcessType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.*
import java.io.File


@RestController
@RequestMapping("/admin/process/processType")
class ProcessTypeController(private val processTypeService: ProcessTypeService) {
    @GetMapping("/findAll")
    @PreAuthorize("hasAuthority('btn.processType.list')")
    fun getAllProcessType(): Result<List<ProcessType>> {
        return Result.ok(processTypeService.list())
    }

    @GetMapping("/{page}/{limit}")
    @PreAuthorize("hasAuthority('btn.processType.list')")
    fun pageQueryProcessType(
        @PathVariable page: Long,
        @PathVariable limit: Long
    ): PageResult<ProcessType> {
        return processTypeService.pageQueryProcessType(page, limit)
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('btn.processType.list')")
    operator fun get(@PathVariable id: Long): Result<ProcessType> {
        val processType = processTypeService.getById(id)
        return Result.ok(processType)
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('btn.processType.add')")
    fun save(@RequestBody processType: ProcessType): Result<Unit> {
        return if (processTypeService.save(processType)) Result.ok()
        else Result.fail()
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('btn.processType.update')")
    fun updateById(@RequestBody processType: ProcessType): Result<Unit> {
        return if (processTypeService.updateById(processType)) Result.ok()
        else Result.fail()
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('btn.processType.remove')")
    fun remove(@PathVariable id: Long): Result<Unit> {
        return if (processTypeService.removeById(id)) Result.ok()
        else Result.fail()
    }
}
