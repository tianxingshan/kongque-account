package com.kongque.interceptor;

import com.kongque.component.IRedisClient;
import com.kongque.component.ThreadCache;
import com.kongque.constants.Constants;
import com.kongque.service.account.ILogInOutService;
import com.kongque.service.oauth.OauthService;
import com.kongque.util.JsonUtil;
import com.kongque.util.Result;
import com.kongque.util.StringUtils;
import com.kongque.util.SysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class LogInterceptor implements HandlerInterceptor {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private AntPathMatcher pathMatcher = new AntPathMatcher();
	
	private String preUrl="/kongque-account";

	// 不校验的接口集合
	private Set<String> ignoreUriSet = new HashSet<String>() {

		private static final long serialVersionUID = 3045542936051658594L;

		{
			add("error");
			add("/account/add");
			add("/account/manage/sys/role");
			add("/account/manage/sys/accountRole");
			add("/account/registerCompany");
			add("/account/check/check-phone/**");
			add("/account/check/check-account-name/**");
			add("/account/updateAccountForMsgFlag/**");
			add("/account/login/check/**");
			add("/account/login");
			add("/account/login/verify");
			add("/account/loginPermission");//登录并获取权限
			add("/account/get/{accountId}");
			add("/sms/code/send");
			add("/sms/account/only");
			add("/sms/edit/appPassword");//忘记密码
			add("/sms/code/istrue");//PC端校验验证码
			add("/sms/app/check-Captcha");//移动端校验验证码
			add("/sms/password/reset");//用户通过手机号重置密码
			add("/app/account/checking/WeChat-arguments/*");//根据微信平台参数检查账户信息
		}
	};

	@Resource
	private ILogInOutService logInOutService;

	@Resource
	private OauthService oauthService;

	/**
	 * 前端功能调用请求拦截过滤
	 * 
	 * @author pengcheng
	 * @since 2017-09-21
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(Constants.SYSCONSTANTS.IS_DEV)
			return true;		
		if (request.getMethod().equals("OPTIONS")) {// 如果是跨域授权请求则不进行业务过滤拦截
			return true;
		}
		String targetUri = request.getRequestURI().toString();// 获取请求要访问的Uri
		
		for(String ignoreUri : ignoreUriSet){// 检测获取到的uri是否是需要对请求进行拦截过滤的uri：如果获取到的uri不是需要拦截的请求			
			if(pathMatcher.match(preUrl+ignoreUri, targetUri)){
				return true;// 通过拦截校验
			}
		}

		String token = SysUtil.getToken();// 获取header中的登录凭证参数（token）//request.getHeader("token");
		response.setContentType("application/json");// 设置响应的内容类型属性为json字符串
		response.setHeader("Access-Control-Allow-origin", "*");// 设置允许浏览器跨域访问的属性
		if (StringUtils.isBlank(token)) {// 如果本次访问的header属性中没有登录凭证（token）参数
			PrintWriter out = response.getWriter();// 从响应实体类对象中获取输出流
			out.write(JsonUtil.objToJson(new Result(Constants.RESULT_CODE.UN_AUTHORIZED, "没有登录授权，拒绝本次请求")).toString());// 把要返回给请求端的业务状态码和相关信息写入输出流
			out.flush();// 释空输出流缓冲区
			logger.info("接口[" + targetUri + "]调用请求的header信息中缺少token参数");// 记录业务日志
			return false;// 拦截校验没有通过
		}
		String resultCode = logInOutService.checkToken(token, null);// 对header中携带的登录凭证参数（token）进行校验
		if (!Constants.RESULT_CODE.SUCCESS.equals(resultCode)) {// 如果获取到的登录凭证经检测已经过期失效
			Result result = new Result();
			result.setReturnCode(resultCode);
			switch (resultCode) {
			case Constants.RESULT_CODE.NOT_LOG_IN:
				result.setReturnMsg("登录超时，请重新登录");
			case Constants.RESULT_CODE.ACCOUNT_EXCEPTION:
				result.setReturnMsg("账号已被冻结或删除，请联系管理员。");

			}
			PrintWriter out = response.getWriter();// 从响应实体类对象中获取输出流
			out.write(JsonUtil.objToJson(result).toString());// 把要返回给请求端的业务状态码和相关信息写入输出流
			out.flush();// 释空输出流缓冲区
			logger.error("接口[" + targetUri + "]调用请求所携带的的token[" + token + "]已经过期");// 记录业务日志
			return false;// 拦截校验没有通过
		}
		// 权限认证
		if (StringUtils.isNotBlank(request.getHeader("targeUri")))
			targetUri = request.getHeader("targeUri");
		if (!oauthService.checkoutUrl(targetUri, token, request.getMethod())) {
			logger.error("接口[" + targetUri + "]调用请求所携带的的token[" + token + "]权限不足");// 记录业务日志
			PrintWriter out = response.getWriter();// 从响应实体类对象中获取输出流
			out.write(JsonUtil.objToJson(new Result(Constants.RESULT_CODE.NOT_PERMISSION, "权限不足")).toString());// 把要返回给请求端的业务状态码和相关信息写入输出流
			out.flush();// 释空输出流缓冲区
			return false;
		}

		return true;// 通过拦截校验
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		ThreadCache.removePostRequestParams();
	}

}
