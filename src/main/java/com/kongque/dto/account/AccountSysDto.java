package com.kongque.dto.account;

/**
 * @author JinXiaoyang  2017/10/18.
 */
public class AccountSysDto {

    private String kongqueAccountId;

    private String[] sysIds;

    public String getKongqueAccountId() {
        return kongqueAccountId;
    }

    public void setKongqueAccountId(String kongqueAccountId) {
        this.kongqueAccountId = kongqueAccountId;
    }

    public String[] getSysIds() {
        return sysIds;
    }

    public void setSysIds(String[] sysIds) {
        this.sysIds = sysIds;
    }
}
