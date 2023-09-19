package com.example.oa.model.process;

import com.example.oa.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("oa_process")
public class Process extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("process_code")
    private String processCode;

    @TableField("user_id")
    private Long userId;

    @TableField("process_template_id")
    private Long processTemplateId;

    @TableField("process_type_id")
    private Long processTypeId;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("form_values")
    private String formValues;

    @TableField("process_instance_id")
    private String processInstanceId;

    @TableField("current_auditor")
    private String currentAuditor;

    @TableField("status")
    private Integer status;

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProcessTemplateId() {
        return processTemplateId;
    }

    public void setProcessTemplateId(Long processTemplateId) {
        this.processTemplateId = processTemplateId;
    }

    public Long getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(Long processTypeId) {
        this.processTypeId = processTypeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormValues() {
        return formValues;
    }

    public void setFormValues(String formValues) {
        this.formValues = formValues;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getCurrentAuditor() {
        return currentAuditor;
    }

    public void setCurrentAuditor(String currentAuditor) {
        this.currentAuditor = currentAuditor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}