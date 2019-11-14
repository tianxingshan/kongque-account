package com.kongque.dto.account;

/**
 * 员工注册
 * @author : zongt
 * @dateCreated : 2019/6/4 9:47
 * @description :
 */
public class RegisterPersonDto {

    private String   accountId;
    private String   tenantId;
    private String   accountName;
    private String   phone;
    private String   pwd;
    private String   name;
    private String[] roleIds;
    private String   companyId;
    private String   storeId;
    private String[] manageStoreIds;
    private String   code;
    private String   sysAccountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String[] roleIds) {
        this.roleIds = roleIds;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String[] getManageStoreIds() {
        return manageStoreIds;
    }

    public void setManageStoreIds(String[] manageStoreIds) {
        this.manageStoreIds = manageStoreIds;
    }

    public String getSysAccountId() {
        return sysAccountId;
    }

    public void setSysAccountId(String sysAccountId) {
        this.sysAccountId = sysAccountId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
