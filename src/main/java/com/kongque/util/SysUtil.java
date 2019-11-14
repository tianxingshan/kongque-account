/**
 * 
 */
package com.kongque.util;

import javax.servlet.http.HttpServletRequest;

import com.kongque.component.ThreadCache;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kongque.component.SpringContextUtil;
import com.kongque.model.ModelForLogin;
import com.kongque.service.account.IAccountService;
import com.kongque.service.account.ILogInOutService;

/**
 * 岳辉
 * 2017年10月24日
 */
public class SysUtil {

	public static HttpServletRequest getRequest(){
		if(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())!=null)
			return (((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
		return null;
	}

	public static String getToken() {
		HttpServletRequest request = getRequest();
		String token = null;
		if(request==null){
			token= ThreadCache.getToken();
		}else
	    if(StringUtils.isNotBlank(request.getHeader("token"))){
	    	token = request.getHeader("token");
	    }
	    else if(request.getAttribute("token") != null){
	    	token = request.getAttribute("token").toString();
	    }
	    else if(StringUtils.isNotBlank(request.getParameter("token"))){
	    	token = request.getParameter("token");
	    }
	    return token;
	}
	
	public static String getAccountId() {
		Result rs = ((ILogInOutService)SpringContextUtil.getBean("logInOutServiceImpl")).getLoginInfo(getToken());
		if (rs.getReturnCode().equals("200"))
			return ((ModelForLogin) rs.getReturnData()).getAccountId();
		else
			return "";
	}
	
	/**
	 * 获取当前用户id和name
	 * @return
	 */
	public static String getAccount() {
		Result rs = ((IAccountService)SpringContextUtil.getBean("accountServiceImpl")).findAccountByToken(getToken());
		if (rs.getReturnCode().equals("200"))
			return JsonUtil.objToJson(rs.getReturnData()).toString();
		else
			return "";
	}
}
