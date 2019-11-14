package com.kongque.dto.account;

public class SmsDto {
	
	/**
	 * 手机验证码默认有效期：1天
	 */
	public static long DEFUAULT_EXPIRES = (24 * 60 * 60 * 1000); 
	
	private String phone;
	
	private String number;
	
	private String newNumber;
	
	private String accountId;
	
	private String newPassWord;
	
	private String newPhone;
	
	private String accountName;
	
	private String sysId;
	
	private Long expires;//手机验证码业务专属有效期
	
	private boolean forcedExpiration = false;//验证成功后手机验证码是否强制失效，默认不强制失效
	

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getNewPassWord() {
		return newPassWord;
	}

	public void setNewPassWord(String newPassWord) {
		this.newPassWord = newPassWord;
	}

	public String getNewPhone() {
		return newPhone;
	}

	public void setNewPhone(String newPhone) {
		this.newPhone = newPhone;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getNewNumber() {
		return newNumber;
	}

	public void setNewNumber(String newNumber) {
		this.newNumber = newNumber;
	}

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	public void setForcedExpiration(boolean forcedExpiration) {
		this.forcedExpiration = forcedExpiration;
	}

	public boolean isForcedExpiration() {
		return forcedExpiration;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	@Override
	public String toString() {
		return "SmsDto [phone=" + phone + ", number=" + number + ", newNumber=" + newNumber + ", accountId=" + accountId
				+ ", newPassWord=" + newPassWord + ", newPhone=" + newPhone + ", accountName=" + accountName
				+ ", expires=" + expires + ", forcedExpiration=" + forcedExpiration + "]";
	}

}
