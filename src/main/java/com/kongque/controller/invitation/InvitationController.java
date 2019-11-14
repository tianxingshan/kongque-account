/**
* @author pengcheng
* @since 2018年5月4日
 */
package com.kongque.controller.invitation;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.kongque.dto.account.InvitationDto;
import com.kongque.service.invitation.IInvitationService;
import com.kongque.util.Result;
import com.kongque.util.SysUtil;

/**
 * @author pengcheng
 * @since 2018年5月4日
 */
@RestController
public class InvitationController {
	
	private static Logger logger=LoggerFactory.getLogger(InvitationController.class);

	@Resource
	private IInvitationService invitationService;

    /**
     * 添加新的邀请关系
     * @param invitationDto 新邀请关系参数封装
     * @return
     */
	@PostMapping(value="/invitation/add",produces="application/json")
	public Result addInvitation(@RequestBody InvitationDto invitationDto){
		return new Result(invitationService.addInvitation(invitationDto.getInviteeId(), invitationDto.getInviterId()));
	}
	
	/**
	 * 获取登录用户的邀请人平台账户的用户名称
	 * 
	 * @author pengcheng
	 * @since 2018年5月18日
	 * @return
	 */
	@GetMapping(value="/invitation/inviter-name/get",produces="application/json")
	public Result getInviterNamForLoggedInvitee(){
		return invitationService.getInviterName();
	}
	
	/**
	 * 获取邀请人信息
	 * @author yuehui 
	 * @date: 2018年5月29日上午11:40:09
	 */
	@GetMapping("/invitater/info")
	public Result getInviter(){
		return new Result(invitationService.getInvitationForInvitee(SysUtil.getAccountId()));
	}
	
	/**
	 * 获取邀请人信息两级
	 * @author yuehui 
	 * @date: 2018年5月29日上午11:40:09
	 */
	@GetMapping("/invitaters/info")
	public Result getInviters(String accountId){
		return invitationService.getInvitationForInvitees(accountId);
	}


	/**
	 * 获取邀请人信息列表
	 *（用于会员系统显示邀请人账号业务）
	 * @author lilishan
	 * @since 2018年8月6日
	 * @return
	 */
	@GetMapping(value="/invitation/inviter/get/list",produces="application/json")
	public Result getInviterListByIds(String [] acountIds){
		return invitationService.getInviterListByIds(acountIds);
	}

	/**
	 * 按查询参数获取符合要求的邀请关系
	 * 
	 * @author pengcheng
	 * @since 2018年9月21日
	 * @param queryDto
	 * @return
	 */
	@GetMapping(value="/invitation/list",produces="application/json")
	public Result getInvitation(InvitationDto queryDto){
		logger.info("请求按以下参数查询账户邀请关系："+queryDto);
		return invitationService.queryInvitation(queryDto);
	}

	/**
	 * 根据推荐人id查询被推荐人id列表
	 *（用依品有调系统合伙人指派量体师业务）
	 * @author lilishan
	 * @since 2018年9月27日
	 * @return
	 */
	@GetMapping(value="/invitation/inviteeId/{accountId}",produces="application/json")
	public Result getInviteeIdsByInviterId(@PathVariable String  accountId){
		logger.info("根据推荐人id查询被推荐人id列表：合伙人ID["+accountId+"]");
		return invitationService.getInviteeIdsByInviterId(accountId);
	}

	/**
	 * 根据被推荐人id查询推荐人账号名称
	 *（用依品有调系统合伙人指派量体师业务）
	 * @author lilishan
	 * @since 2018年9月29日
	 * @return
	 */
	@GetMapping(value="/invitation/inviterName/{accountId}",produces="application/json")
	public Result getInviterName(@PathVariable String accountId){
		logger.info("根据被推荐人id["+accountId+"]查询推荐人账号名称");
		return invitationService.getInviterName(accountId);
	}


}
