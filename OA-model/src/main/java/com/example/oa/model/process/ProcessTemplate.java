package com.example.oa.model.process;

import com.example.oa.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("oa_process_template")
public class ProcessTemplate extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableField("name")
	private String name;

	@TableField("icon_url")
	private String iconUrl;

	@TableField("process_type_id")
	private Long processTypeId;

	@TableField("form_props")
	private String formProps;

	@TableField("form_options")
	private String formOptions;

	@TableField("description")
	private String description;

	@TableField("process_definition_key")
	private String processDefinitionKey;

	@TableField("process_definition_path")
	private String processDefinitionPath;

	@TableField("process_model_id")
	private String processModelId;

	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private String processTypeName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Long getProcessTypeId() {
		return processTypeId;
	}

	public void setProcessTypeId(Long processTypeId) {
		this.processTypeId = processTypeId;
	}

	public String getFormProps() {
		return formProps;
	}

	public void setFormProps(String formProps) {
		this.formProps = formProps;
	}

	public String getFormOptions() {
		return formOptions;
	}

	public void setFormOptions(String formOptions) {
		this.formOptions = formOptions;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getProcessDefinitionPath() {
		return processDefinitionPath;
	}

	public void setProcessDefinitionPath(String processDefinitionPath) {
		this.processDefinitionPath = processDefinitionPath;
	}

	public String getProcessModelId() {
		return processModelId;
	}

	public void setProcessModelId(String processModelId) {
		this.processModelId = processModelId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getProcessTypeName() {
		return processTypeName;
	}

	public void setProcessTypeName(String processTypeName) {
		this.processTypeName = processTypeName;
	}
}