package com.kongque.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kongque.component.ThreadCache;

@Configuration
@Order(-2)
public class CommonExceptionHandler  extends DefaultHandlerExceptionResolver {

	Logger log = LoggerFactory.getLogger(CommonExceptionHandler.class);

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		try {
			log.error("系统错误uri:" + request.getRequestURI() +" ,"+request.getMethod()+ "\n参数：" + new ObjectMapper().writeValueAsString(request.getParameterMap())+"\nbody:"+ ThreadCache.getPostRequestParams(), ex);
			ThreadCache.removePostRequestParams();
		} catch (IOException e) {
			log.error("未知错误", e);
		}
		return super.doResolveException(request, response, handler, ex);
	}
} 
