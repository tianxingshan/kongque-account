/**
* @author pengcheng
* @since 2018年6月8日
 */
package com.kongque.service.liaision;

import java.util.Map;

import com.kongque.dto.account.MembershipDetailDto;
import com.kongque.util.Pagination;
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
@FeignClient(name="KONGQUE-MEMBERSHIP/kongque-membership",configuration=FeignInterceptor.class)
public interface IMemebershipFeignService {

	/**
	 * 根据平台账户id获取对应的会员信息
	 * 
	 * @author pengcheng
	 * @since 2018年6月21日
	 * @param kongqueAccountId 平台账户id
	 * @return
	 */
	@GetMapping(value="/membership/detail/get")
	public Result getMembershipInfo(@RequestParam("kongqueAccountId") String kongqueAccountId);
	
	/**
	 * 修改会员信息
	 * 
	 * @author pengcheng
	 * @since 2018年6月21日
	 * @param arguments 修改请求参数
	 * @return
	 */
	@PostMapping(value="/manage/membership/detail/edit")
	public Result editMembershipDetail(@RequestBody Map<String, Object> arguments);

	/**
	 * 修改推荐人
	 * 获取上级销售人员分页列表信息
	 * @author lilishan
	 * @since 2018年7月18日
	 * @param map 分页信息
	 * @return
	 */
	@GetMapping(value="/membership/type/sales-level/list")
	public Pagination<MembershipDetailDto> getSalesLevelList(@RequestParam("map") Map<String,Object> map);

}
