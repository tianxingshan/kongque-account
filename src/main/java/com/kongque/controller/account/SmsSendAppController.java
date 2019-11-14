/**
 * @author yuehui 
 * @date: 2018年3月29日
 */
package com.kongque.controller.account;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kongque.component.impl.JsonMapper;
import com.kongque.dto.account.SmsDto;
import com.kongque.service.account.ISmsSendService;
import com.kongque.util.Result;

/**
 * @author yuehui
 *
 * @date: 2018年3月29日 下午1:44:45
 */
@RestController
public class SmsSendAppController {
	
	private static Logger logger = LoggerFactory.getLogger(SmsSendController.class);
	@Resource
	ISmsSendService service;

	/**
	 * 手机验证成功后修改新密码
	 * @param smsDto
	 * @return
	 */
	@PostMapping(value="/sms/edit/appPassword")
	public Result editAppPassword(@RequestBody SmsDto smsDto){
		logger.info("app用户忘记密码："+JsonMapper.toJson(smsDto));
		return service.editAppPassword(smsDto);
	}

	/**
	 * 判断验证码是否正确
	 * 
	 * @author pengcheng
	 * @since 2018年5月3日
	 * @param smsDto
	 * @return
	 */
	@PostMapping(value="/sms/app/check-Captcha")
	public Result checkCaptcha(@RequestBody SmsDto smsDto){
		return service.smsIstrue(smsDto);
	}

}
