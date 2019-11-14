package com.kongque.dto.account;

/**
 * @author JinXiaoyang  2017/10/17.
 */
public class AccountUpdateDto {

    private String kongqueAccountId;

    private String oldPassword;

    private String newPassword;

    public String getKongqueAccountId() {
        return kongqueAccountId;
    }

    public void setKongqueAccountId(String kongqueAccountId) {
        this.kongqueAccountId = kongqueAccountId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
