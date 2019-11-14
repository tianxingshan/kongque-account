/**
* @author pengcheng
* @since 2017年10月17日
 */
package com.kongque.model;

import java.util.Set;

/**登录成功后需要返回给前端的相关账户信息的封装类
 * @author pengcheng
 * @since 2017年10月17日
 */
public class ModelForLogin {

	/**
	 * 账户的ID主键
	 */
	private String accountId;
	
	/**
	 * 账户的邀请码
	 */
	private String invitationCode;
	
	/**
	 * 账户登录成功后，在保存在Redis中的账户信息所对应的token（键名）
	 */
	private String token;
	
	/**
	 * 当账户还同时登录了微信平台时，
	 * 保存登录微信平台得到的返回参数session_key
	 */
	private String sessionKey;
	
	/**
	 * 账户状态
	 * 1：正常2：冻结3：删除
	 */
	private String status;
	
	/**
	 * 有户名
	 */
	private String userName;
	
	/**
	 *与当前账户建立了关联关系的系统唯一标识列表
	 */
	private Set<String> sysSet;

	/**
	 * 新手标识
	 */
	private String newFlag;

	/**
	 * 手机号
	 * @return
	 */
	private String phone;

	/**
	 * 信息完善标识
	 */
	private String msgFlag;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public Set<String> getSysSet() {
		return sysSet;
	}

	public void setSysSet(Set<String> sysSet) {
		this.sysSet = sysSet;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNewFlag() {
		return newFlag;
	}

	public void setNewFlag(String newFlag) {
		this.newFlag = newFlag;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMsgFlag() {
		return msgFlag;
	}

	public void setMsgFlag(String msgFlag) {
		this.msgFlag = msgFlag;
	}

	@Override
	public String toString() {
		return "ModelForLogin{" +
			   "accountId='" + accountId + '\'' +
			   ", invitationCode='" + invitationCode + '\'' +
			   ", token='" + token + '\'' +
			   ", sessionKey='" + sessionKey + '\'' +
			   ", status='" + status + '\'' +
			   ", userName='" + userName + '\'' +
			   ", sysSet=" + sysSet +
			   ", newFlag='" + newFlag + '\'' +
			   ", phone='" + phone + '\'' +
			   ", msgFlag='" + msgFlag + '\'' +
			   '}';
	}
}
