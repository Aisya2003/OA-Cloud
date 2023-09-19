package com.example.oa.model.vo.system;

import lombok.Data;


public class SysPostQueryVo {
	
	private String postCode;

	private String name;

	private Boolean status;

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}

