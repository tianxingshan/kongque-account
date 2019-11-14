/**
 * 
 */
package com.kongque.component.RequestCustom;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.kongque.component.ThreadCache;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuehui
 *
 * @2018年1月17日
 */
@Configuration
@WebFilter(filterName="wrappedFilter",urlPatterns="/*")
public class SimpleWrappedFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        	 //cach requestBody,异常时打印
            WrappedHttpServletRequest requestWrapper = new WrappedHttpServletRequest((HttpServletRequest) request);
            ThreadCache.setPostRequestParams(requestWrapper.getRequestParams());
            // 这里doFilter传入我们实现的子类
            chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {

    }
}
