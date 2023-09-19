package com.example.oa.model.process;

import com.example.oa.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;


@TableName("oa_process_record")
public class ProcessRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("process_id")
    private Long processId;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @TableField("operate_user_id")
    private Long operateUserId;

    @TableField("operate_user")
    private String operateUser;

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }
}