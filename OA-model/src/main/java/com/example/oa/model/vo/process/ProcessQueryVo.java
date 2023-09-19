package com.example.oa.model.vo.process;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;


public class ProcessQueryVo {

	private String keyword;

	private Long userId;

	private Long processTemplateId;

	private Long processTypeId;

	private String createTimeBegin;
	private String createTimeEnd;

	private Integer status;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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

	public String getCreateTimeBegin() {
		return createTimeBegin;
	}

	public void setCreateTimeBegin(String createTimeBegin) {
		this.createTimeBegin = createTimeBegin;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}