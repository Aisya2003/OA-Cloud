package com.example.oa.model.vo.wechat;

import lombok.Data;


public class BindPhoneVo {

    private String phone;

    private String openId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
