package com.example.oa.biz.service.impl.process

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.oa.biz.mapper.process.ProcessTemplateMapper
import com.example.oa.biz.service.process.ProcessTemplateService
import com.example.oa.biz.service.process.ProcessTypeService
import com.example.oa.exception.BizException
import com.example.oa.model.constant.StatusConstant
import com.example.oa.model.dto.PageResult
import com.example.oa.model.dto.Result
import com.example.oa.model.process.ProcessTemplate
import com.example.oa.model.vo.UploadFileResponseVo
import com.example.oa.utils.QueryPageUtil
import org.activiti.engine.RepositoryService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.zip.ZipInputStream

@Service
class ProcessTemplateServiceImpl(
    private val processTypeService: ProcessTypeService,
    private val repositoryService: RepositoryService
) : ServiceImpl<ProcessTemplateMapper, ProcessTemplate>(), ProcessTemplateService {
    override fun queryProcessTemplatePageWithProcessTypeName(page: Long, limit: Long): PageResult<ProcessTemplate> {
        val pageResult = QueryPageUtil.getPage(page, limit, this, null)
        pageResult.records.forEach {
            processTypeService.getById(it.processTypeId)?.let { processType -> it.processTypeName = processType.name }
        }
        return PageResult(page, limit, pageResult.total, pageResult.records)
    }

    override fun uploadProcessFile(file: MultipartFile): Result<UploadFileResponseVo> {
        file.originalFilename?.let {
            //获取类路径
            val resourcePath = Thread.currentThread().contextClassLoader.getResource("")
            val destinationDir = File("${resourcePath?.path}processes/")
            //创建文件
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }
            //上传文件
            file.transferTo(File("${destinationDir.path}\\${it}"))
            return Result.ok(UploadFileResponseVo("processes/${it}", it.substringBefore(".")))

        } ?: throw BizException(msg = "文件名不能为空！")
    }

    override fun publishProcessDefinition(id: Long): Boolean {
        var processTemplate = this.getById(id)
        processTemplate.processDefinitionPath?.let {
            if (it.isNotBlank()) this.deployProcessDefinition(it)
        }
        //修改状态为发布
        processTemplate = ProcessTemplate().apply {
            this.id = id
            this.status = StatusConstant.ENABLED
        }
        return this.updateById(processTemplate)
    }

    /**
     * 部署流程实例
     */
    private fun deployProcessDefinition(zipFilePath: String): Boolean {
        //获取文件
        val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(zipFilePath)
        val zipInputStream = inputStream?.let { ZipInputStream(it) }
        //部署
        repositoryService.createDeployment()
            .addZipInputStream(zipInputStream)
            .deploy()
        return true
    }
}
