/**
 * 
 */
package com.kongque.component.oauth;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.kongque.service.sysResource.ISysResourceService;

/**
 * @author yuehui
 *
 * @2017年12月12日
 */
@Component
public class SecurityRedisInit {

	@Resource
	private ISysResourceService resourceService;

	/**
	 * 初始化需要验证的url
	 */
	@PostConstruct
	public void intiSecuritySysResource() {

		resourceService.initSecuritySysResource();
	}

}
