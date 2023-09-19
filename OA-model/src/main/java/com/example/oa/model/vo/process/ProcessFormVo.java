package com.example.oa.model.vo.process;

import lombok.Data;

@Data
public class ProcessFormVo {

    private Long processTemplateId;

    private Long processTypeId;

    private String formValues;

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

    public String getFormValues() {
        return formValues;
    }

    public void setFormValues(String formValues) {
        this.formValues = formValues;
    }
}