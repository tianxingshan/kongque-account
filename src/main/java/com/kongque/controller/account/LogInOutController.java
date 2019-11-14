package com.kongque.controller.account;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.kongque.component.impl.JsonMapper;
import com.kongque.dto.account.AccountLoginDto;
import com.kongque.dto.account.LoginVerifyDto;
import com.kongque.service.account.ILogInOutService;
import com.kongque.util.Result;
import com.kongque.util.StringUtils;
import com.kongque.util.SysUtil;

/**
 * 前台用户登入登出功能调用响应接口类
 * 
 * @author pengcheng
 * @since 2017年10月16日
 */
@RestController
public class LogInOutController {

	private static Logger logger=LoggerFactory.getLogger(LogInOutController.class);
	
	@Resource
	private ILogInOutService service;
	
	/**
	 * 账户登录接口：响应账号登录请求
	 * 
	 * @author pengcheng
	 * @since 2017年10月23日
	 * @param accountLoginDto
	 * @return
	 */
	@RequestMapping(value="/account/login",method=RequestMethod.POST,produces="application/json")
	public  Result accountLogIn(@RequestBody AccountLoginDto accountLoginDto){
		logger.info(
				StringUtils.isNotBlank(accountLoginDto.getUsername())
				? "前端用户["+accountLoginDto.getUsername()+"]请求登录："+JsonMapper.toJson(accountLoginDto)
				: "前端用户使用token登录："+SysUtil.getToken()
		);
		return service.logIn(accountLoginDto);
	}

	/**
	 * 账户登录接口校验账号登录系统权限
	 *
	 * @author lilishan
	 * @since 2017年12月4日
	 * @param accountLoginDto
	 * @return
	 */
	@RequestMapping(value="/account/login/check",method=RequestMethod.POST,produces="application/json")
	public  Result accountLogInCheckUser(@RequestBody AccountLoginDto accountLoginDto){
		logger.info(
				StringUtils.isNotBlank(accountLoginDto.getUsername())
				? "前端用户["+accountLoginDto.getUsername()+"]请求登录："+JsonMapper.toJson(accountLoginDto)
				: "前端用户使用token登录："+SysUtil.getToken()
		);
		return service.accountLogInCheckUser(accountLoginDto);
	}

	/**
	 * 账户登出接口：响应账号登出请求
	 * 
	 * @author pengcheng
	 * @since 2017年10月23日
	 * @param token
	 * @return
	 */
	@RequestMapping(value="/account/logout",method=RequestMethod.GET,produces="application/json")
	public  Result accountLogOut(String token){
		logger.info("前端用户请求登出系统。");
		return service.logOut(token);
	}
	
	/**
	 * 账户登录校验：响应登录验证请求。对账号是否已经登录，以及该账号是否能够请求该系统接口进行校验。
	 * 
	 * @author pengcheng
	 * @since 2017年10月23日
	 * @param loginVerifyDto
	 * @return 
	 */
	@RequestMapping(value="/account/login/verify",method=RequestMethod.GET,produces="application/json")
	public  Result accountLoginVerify(LoginVerifyDto loginVerifyDto){
		//logger.info("业务系统[sysId:"+loginVerifyDto.getSysId()+"]请求账户登录校验。");
		return service.verify(loginVerifyDto);
	}
	
	/**
	 * 账户登录信息获取：根据token返回对应的账户登录信息。
	 * 
	 * @author pengcheng
	 * @since 2017年10月24日
	 * @param token 账户登录凭证
	 * @return
	 */
	@RequestMapping(value="/account/loginInfo",method=RequestMethod.GET,produces="application/json")
	public  Result accountLoginInfo(String token){
		return service.getLoginInfo(token);
	}

	@PostMapping("/account/loginPermission")
	public Result loginForPermission(@RequestBody AccountLoginDto accountLoginDto){
	    logger.info(
                StringUtils.isNotBlank(accountLoginDto.getUsername())
                        ? "前端用户["+accountLoginDto.getUsername()+"]请求登录："+JsonMapper.toJson(accountLoginDto)
                        : "前端用户使用token登录："+SysUtil.getToken()
        );
	    return service.loginForPermission(accountLoginDto);
    }
	
}
