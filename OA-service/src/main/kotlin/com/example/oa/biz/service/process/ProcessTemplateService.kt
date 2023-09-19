package com.example.oa.biz.service.process

import com.baomidou.mybatisplus.extension.service.IService
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.process.ProcessTemplate
import com.example.oa.model.vo.UploadFileResponseVo
import org.springframework.web.multipart.MultipartFile

interface ProcessTemplateService : IService<ProcessTemplate> {
    /**
     * 查询流程模板信息，包含类型名称
     */
    fun queryProcessTemplatePageWithProcessTypeName(page: Long, limit: Long): PageResult<ProcessTemplate>

    /**
     * 上传流程定义文件
     */
    fun uploadProcessFile(file: MultipartFile): Result<UploadFileResponseVo>

    /**
     * 发布流程定义
     */
    fun publishProcessDefinition(id: Long): Boolean
}