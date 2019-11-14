/**
* @author pengcheng
* @since 2018年6月6日
 */
package com.kongque.component.RequestCustom;

import com.kongque.util.SysUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author pengcheng
 * @since 2018年6月6日
 */
public class FeignInterceptor implements RequestInterceptor {

	/* (non-Javadoc)
	 * @see feign.RequestInterceptor#apply(feign.RequestTemplate)
	 */
	@Override
	public void apply(RequestTemplate requestTemplate) {
		requestTemplate.header("token", SysUtil.getToken());
	}

}
