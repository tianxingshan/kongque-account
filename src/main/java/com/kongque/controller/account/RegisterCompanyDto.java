package com.kongque.controller.account;

import java.io.Serializable;

/**
 * @author : zongt
 * @dateCreated : 2019/6/4 8:56
 * @description : 注册企业账号 参数类
 */
public class RegisterCompanyDto {


    /**
     * 姓名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 重复密码
     */
    private String repwd;
    /**
     * 手机号
     */
    private String phone;

    /**
     * 注册来源
     */
    private String   source;
    /**
     * 验证码
     */
    private String   code;
    /**
     * 系统id
     */
    private String   sysId;
    private String[] sysIds;
    private String   unionid;
    private String   openid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String[] getSysIds() {
        return sysIds;
    }

    public void setSysIds(String[] sysIds) {
        this.sysIds = sysIds;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getRepwd() {
        return repwd;
    }

    public void setRepwd(String repwd) {
        this.repwd = repwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
