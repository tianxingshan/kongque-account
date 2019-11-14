package com.kongque.service.account;

import com.kongque.dto.account.SmsDto;
import com.kongque.util.Result;

public interface ISmsSendService {
	
	public Result smsSend(SmsDto smsDto);
	
	public Result smsIstrue(SmsDto smsDto);
	
	public Result editPassword(SmsDto smsDto);
	
	/**
	 * 手机验证码修改密码
	 * @author yuehui 
	 * @date: 2018年3月29日上午10:54:22
	 */
	public Result editAppPassword(SmsDto smsDto);
	
	public Result editPhone(SmsDto smsDto);

	public Result accountOnly(SmsDto smsDto);

}
