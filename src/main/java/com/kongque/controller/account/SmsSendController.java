package com.kongque.controller.account;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kongque.component.impl.JsonMapper;
import com.kongque.dto.account.SmsDto;
import com.kongque.service.account.ISmsSendService;
import com.kongque.util.JsonUtil;
import com.kongque.util.Result;

/**
 * 
 * @author qixl
 * @since 2018年02月27日
 */
@RestController
public class SmsSendController {
	
	private static Logger logger = LoggerFactory.getLogger(SmsSendController.class);
	
	@Resource
	ISmsSendService service;
	
	
	/**
	 * 发送短信验证码
	 * @param smsDto
	 * @return
	 */
	@RequestMapping(value="/sms/code/send",method= RequestMethod.GET)
	public Result SMSsend(SmsDto smsDto){
		logger.info("验证码发送："+JsonMapper.toJson(smsDto));
		return service.smsSend(smsDto);
	}
	
	/**
	 * 判断验证码是否正确
	 * @param smsDto
	 * @return
	 */
	@RequestMapping(value="/sms/code/istrue",method= RequestMethod.GET)
	public Result SmsIstrue(SmsDto smsDto){
		return service.smsIstrue(smsDto);
	}
	
	/**
	 * 手机验证成功后修改新密码
	 * @param smsDto
	 * @return
	 */
	@RequestMapping(value="/sms/edit/password",method= RequestMethod.GET)
	public Result editPassword(SmsDto smsDto){
		logger.info("用户手机验证码修改密码"+JsonMapper.toJson(smsDto));
		return service.editPassword(smsDto);
	}
	
	/**
	 * 通过手机验证重置用户登录密码
	 * 
	 * @author pengcheng
	 * @since 2018年4月23日
	 * @param smsDto
	 * @return
	 */
	@RequestMapping(value="/sms/password/reset",method= RequestMethod.PUT)
	public Result resetPassword(@RequestBody SmsDto smsDto){
		logger.info("前端用户请求重置密码："+JsonUtil.toJson(smsDto, new String[]{"newPassWord"}));
		return service.editAppPassword(smsDto);
	}

	
	
	/**
	 * 手机验证成功后修改新手机号
	 * @param smsDto
	 * @return
	 */
	@RequestMapping(value="/sms/edit/phone",method= RequestMethod.GET)
	public Result editPhone(SmsDto smsDto){
		logger.info("会员修改手机号"+JsonMapper.toJson(smsDto));
		return service.editPhone(smsDto);
	}
	
	/**
	 * 判断验证码及账号唯一性
	 * @param smsDto
	 * @return
	 */
	@RequestMapping(value="/sms/account/only",method= RequestMethod.GET)
	@ResponseBody
	public Result accountOnly(SmsDto smsDto){
		return service.accountOnly(smsDto);
	}

}
