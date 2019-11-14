/**
* @author pengcheng
* @since 2017年10月16日
 */
package com.kongque.dto.account;

import com.kongque.util.CryptographyUtils;
import com.kongque.util.StringUtils;

/**
 * 接收前台前端登入信息的对象转换类
 * 
 * @author pengcheng
 * @since 2017年10月16日
 */
public class AccountLoginDto {

	/**
	 * 账户的登录账号名称
	 */
	private String username;

	/**
	 * 账户的登录密码
	 */
	private String password;
	
	/**
	 * 账户要登录的具体业务系统的唯一识别标识
	 */
	private String sysId;

	/**
	 * 登录账户注册时的来源系统id
	 */
	private String registerSource;
	
	/**
	 * 登录来源
	 */
	private String loginSource;
	
	/**
	 * 账户的微信平台openid
	 */
	private String openid;
	
	/**
	 * 账户的微信平台unionid
	 */
	private String unionid;

	/**
	 * 账户本次登录微信平台所获得的sessionKey
	 */
	private String sessionKey;
	
	/**
	 * 账户本次登录的有效期（单位：秒）
	 */
	private Integer expire;
	
	/**
	 * 验证码
	 */
	private String captcha;

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
		this.password = StringUtils.isNotBlank(password) ? CryptographyUtils.md5(password) : null;
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

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public Integer getExpire() {
		return expire;
	}

	public void setExpire(Integer expire) {
		this.expire = expire;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getRegisterSource() {
		return registerSource;
	}

	public void setRegisterSource(String registerSource) {
		this.registerSource = registerSource;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	

	public String getLoginSource() {
		return loginSource;
	}

	public void setLoginSource(String loginSource) {
		this.loginSource = loginSource;
	}

	@Override
	public String toString() {
		return "AccountLoginDto [username=" + username + ", password=" + password + ", sysId=" + sysId
				+ ", registerSource=" + registerSource + ", loginSource=" + loginSource + ", openid=" + openid
				+ ", unionid=" + unionid + ", sessionKey=" + sessionKey + ", expire=" + expire + ", captcha=" + captcha
				+ "]";
	}

}
