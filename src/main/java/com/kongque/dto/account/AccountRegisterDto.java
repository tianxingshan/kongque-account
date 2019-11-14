package com.kongque.dto.account;

import java.util.Arrays;

/**
 * 接收前台前端注册信息的对象转换类
 * @author JinXiaoyang  2017/10/17.
 */
public class AccountRegisterDto {

	/**
	 * 注册用户名
	 */
    private String username;

    /**
     * 注册密码
     */
    private String password;
    
    /**
     * 注册手机号
     */
    private String phone;
    /**
     * 注册来源：[1]PC端[2]App端[3]小程序
     */
    private String source;
    
    /**
     * 注册来源业务系统id
     */
    private String sysId;

    /**
     * 注册账户所关联的系统id列表
     */
    private String[] sysIds;
    
    /**
     * 通过登录微信平台注册的用户的openid
     */
    private String openid;
    
    /**
     * 通过登录微信平台注册的用户的unionid
     */
    private String unionid;

    /**
     * 注册成功后用户账户的id
     */
    private String kongqueAccountId;

    /**
     * 注册用户完成登录后的token
     */
    private String token;
    
    /**
     * 通过登录微信平台注册的用户本次微信平台登录的session_key
     */
    private String sessionKey;

    /**
     * 注册用户登录的token有效期
     */
    private Integer expire;
    
    /**
     * 注册用户邀请人的邀请码
     */
    private String inviterCode;
    
    /**
     * 注册用户注册成功后属于自己的邀请码
     */
    private String invitationCode;

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

    public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getKongqueAccountId() {
        return kongqueAccountId;
    }

    public void setKongqueAccountId(String kongqueAccountId) {
        this.kongqueAccountId = kongqueAccountId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
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

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getInviterCode() {
		return inviterCode;
	}

	public void setInviterCode(String inviterCode) {
		this.inviterCode = inviterCode;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	@Override
	public String toString() {
		return "AccountRegisterDto [username=" + username + ", password=" + password + ", phone=" + phone + ", source="
				+ source + ", sysId=" + sysId + ", sysIds=" + Arrays.toString(sysIds) + ", openid=" + openid
				+ ", unionid=" + unionid + ", kongqueAccountId=" + kongqueAccountId + ", token=" + token
				+ ", sessionKey=" + sessionKey + ", expire=" + expire + ", inviterCode=" + inviterCode
				+ ", invitationCode=" + invitationCode + "]";
	}
    
}
