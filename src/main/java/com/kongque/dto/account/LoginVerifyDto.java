/**
* @author pengcheng
* @since 2017年10月20日
 */
package com.kongque.dto.account;

/**
 * 登录验证参数包装类
 * 
 * @author pengcheng
 * @since 2017年10月20日
 */
public class LoginVerifyDto {

	/**
	 * 要进行登录验证的账号在redis中对应的凭证
	 */
	private String token;
	
	/**
	 * 要验证与登录账号是否具有关联关系的系统的唯一标识
	 */
	private String sysId;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

}
