/**
* @author pengcheng
* @since 2018年6月8日
 */
package com.kongque.service.liaision;

import java.util.Map;
import java.util.Set;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.kongque.component.RequestCustom.FeignInterceptor;
import com.kongque.util.Result;

/**
 * @author pengcheng
 * @since 2018年6月21日
 */
@FeignClient(name="KONGQUE-TENANT/kongque-tenant",configuration=FeignInterceptor.class)
public interface ITenantFeignService {
	
	/**
	 * 根据商户id获取商户列表名称
	 * 
	 * @author pengcheng
	 * @since 2018年6月21日
	 * @param tenantIds
	 * @return
	 */
	@PostMapping(value="/manage/tenant/listByIdList")
	public Result getTenantList(@RequestBody Set<String> tenantIds);
	
	/**
	 * 商户详情修改
	 * 
	 * @author pengcheng
	 * @since 2018年6月21日
	 * @param arguments 修改请求参数
	 * @return
	 */
	@PostMapping(value="/tenant/detail/edit")
	public Result editTenantDetail(@RequestBody Map<String, Object> arguments);
		
	/**
	 * 根据平台账户id获取商户详情
	 * 
	 * @author pengcheng
	 * @since 2018年6月21日
	 * @param kongqueAccountId 平台账户id
	 * @return
	 */
	@GetMapping(value="/tenant/detail/center/get")
	public Result getTenantDetailByAccountId(@RequestParam("kongqueAccountId") String kongqueAccountId);
}
