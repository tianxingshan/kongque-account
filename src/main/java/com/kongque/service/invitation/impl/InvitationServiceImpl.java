/**
* @author pengcheng
* @since 2018年5月4日
 */
package com.kongque.service.invitation.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kongque.constants.Constants;
import com.kongque.dao.IDaoService;
import com.kongque.dto.account.InvitationDto;
import com.kongque.entity.Account;
import com.kongque.entity.Invitation;
import com.kongque.service.invitation.IInvitationService;
import com.kongque.service.liaision.IMemebershipFeignService;
import com.kongque.util.JsonUtil;
import com.kongque.util.Result;
import com.kongque.util.StringUtils;
import com.kongque.util.SysUtil;

import net.sf.json.JSONObject;

/**
 * @author pengcheng
 * @since 2018年5月4日
 */
@Service
public class InvitationServiceImpl implements IInvitationService {

	private static Logger logger=LoggerFactory.getLogger(InvitationServiceImpl.class);
	
	@Resource
	private IDaoService daoService;

	@Resource
	private IMemebershipFeignService memebershipFeignService;
	/* (non-Javadoc)
	 * @see com.kongque.service.invitation.IInvitationService#getInviterInfoOfLoggedAccount()
	 */
	@Override
	public Result getInviterName() {
		Result result = new Result();
		Invitation invitation = getInvitationForInvitee(SysUtil.getAccountId());
		if(invitation == null){
			result.setReturnCode(Constants.RESULT_CODE.INVITATION_NOT_EXIST);
			result.setReturnMsg("当前账户没有邀请人");
			return result;
		}
		Account inviterAccount = invitation.getInviterAccount();
		String inviterName = inviterAccount.getUsername();
		if("3".equals(inviterAccount.getSource()) && !inviterName.startsWith("ChengYi")){
			inviterName = inviterName.substring(0,inviterName.length() - 9);
		}
		result.setReturnData(inviterName);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.kongque.service.account.IInvitationService#addAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public String addInvitation(String inviteeId, String inviterId) {
		Account inviteeAccount = new Account();
		inviteeAccount.setId(inviteeId);
		Account inviterAccount = new Account();
		inviterAccount.setId(inviterId);
		Invitation invitation = new Invitation();
		invitation.setInviteeAccount(inviteeAccount);
		invitation.setInviterAccount(inviterAccount);
		invitation.setCreateTime(new Date());
		//查询销售
		Result memberRs=memebershipFeignService.getMembershipInfo(inviterId);
		// 会员类型（"PT":普通会员、"XS":销售、"HHR":合伙人）
		invitation.setInviterType(JSONObject.fromObject(memberRs.getReturnData()).optString("memberType"));
		daoService.save(invitation);
		logger.info("[用户："+invitation.getInviterAccount().getUsername()+"]邀请[用户："+invitation.getInviteeAccount().getUsername()+"]的邀请关系创建完成。");
		return invitation.getId();
	}
	
	/* (non-Javadoc)
	 * @see com.kongque.service.invitation.IInvitationService#checkExistingInvitation(java.lang.String)
	 */
	@Override
	public Boolean checkInvitationFroInvitee(String inviteeId) {
		return getInvitationForInvitee(inviteeId) != null;
	}

	/* (non-Javadoc)
	 * @see com.kongque.service.invitation.IInvitationService#getInvitationForInvitee(java.lang.String)
	 */
	@Override
	public Invitation getInvitationForInvitee(String inviteeId) {
		return daoService.findUniqueByProperty(Invitation.class, "inviteeAccount.id", inviteeId);
	}

	/* (non-Javadoc)
	 * @see com.kongque.service.invitation.IInvitationService#getInvitationForInviter(java.lang.String)
	 */
	@Override
	public List<Invitation> getInvitationForInviter(String inviterId) {
		return daoService.findListByProperty(Invitation.class, "inviterAccount.id", inviterId);
	}


	@Override
	public Result getInvitationForInvitees(String accountId) {
		
		if(StringUtils.isBlank(accountId)) accountId=SysUtil.getAccountId();		
		List<Account> rsList=new ArrayList<Account>();
		
		//一级邀请人
		Invitation inv1=daoService.findUniqueByProperty(Invitation.class, "inviteeAccount.id", accountId);
		if(inv1!=null){
			rsList.add(inv1.getInviterAccount());
			//二级邀请人
			Invitation inv2=daoService.findUniqueByProperty(Invitation.class, "inviteeAccount", inv1.getInviterAccount());
			if(inv2!=null)
				rsList.add(inv2.getInviterAccount());
			
			return new Result(JsonUtil.arrayToJson2(rsList, new String[]{"id","username","phone","openid","unionid","source","sysId","status","invitationCode"}), rsList.size()*1L);
			
		}
		
		return new Result();
	}

	/**
	 * 获取邀请人信息列表
	 *（用于会员系统显示邀请人账号业务）
	 * @author lilishan
	 * @since 2018年8月6日
	 * @return
	 */
	@Override
	public Result getInviterListByIds(String[] acountIds) {
		Criteria criteria=daoService.createCriteria(Invitation.class);
		List<Account> list=new ArrayList<>();
		for (String acountId:acountIds) {
			//被邀请人
			Account inviteeAccount=new Account();
			inviteeAccount.setId(acountId);
			list.add(inviteeAccount);
		}
		criteria.add(Restrictions.in("inviteeAccount",list));
		@SuppressWarnings("unchecked")
		List<Invitation> data=criteria.list();
		for (Invitation i:data) {
			if (i.getInviterAccount() != null) {
				i.getInviterAccount().setSysList(null);
			}
			if (i.getInviteeAccount() != null) {
				i.getInviteeAccount().setSysList(null);
			}
		}
		return new Result(data);
	}

	/* (non-Javadoc)
	 * @see com.kongque.service.invitation.IInvitationService#queryInvitation(com.kongque.dto.account.InvitationDto)
	 */
	@Override
	public Result queryInvitation(InvitationDto queryDto) {
		Result result = new Result();
		Criteria criteria=daoService.createCriteria(Invitation.class);
		if(StringUtils.isNotBlank(queryDto.getInviterAccountName())){
			criteria.createAlias("inviterAccount", "inviterAccount").add(Restrictions.like("inviterAccount.username", queryDto.getInviterAccountName(),MatchMode.ANYWHERE));
		}
		if(queryDto.getInviteeIds() != null && queryDto.getInviteeIds().length > 0){
			criteria.add(Restrictions.in("inviteeAccount.id", Arrays.asList(queryDto.getInviteeIds())));
		}
		if(queryDto.getInviterIds() != null && queryDto.getInviterIds().length > 0){
			criteria.add(Restrictions.in("inviterAccount.id", Arrays.asList(queryDto.getInviterIds())));
		}
		@SuppressWarnings("unchecked")
		List<Invitation> invitationList = criteria.list();
		result.setReturnData(invitationList);
		return result;
	}

	@Override
	public Result getInviteeIdsByInviterId(String accountId) {
		List<String> ids=new ArrayList<>();
		Criteria criteria=daoService.createCriteria(Invitation.class);
		criteria.createAlias("inviterAccount","inviter").add(Restrictions.eq("inviter.id",accountId));
		List<Invitation> list = criteria.list();
		if (null != list && list.size() > 0) {
			for (Invitation i : list) {
				ids.add(i.getInviteeAccount().getId());
			}
		}
		return new Result(ids);
	}

	@Override
	public Result getInviterName(String accountId) {

		Result result = new Result();
		Invitation invitation = getInvitationForInvitee(accountId);
		if(invitation == null){
			result.setReturnData("");
			result.setReturnMsg("当前账户"+accountId+"没有邀请人");
			return result;
		}
		Account inviterAccount = invitation.getInviterAccount();
		String inviterName = inviterAccount.getUsername();
		if("3".equals(inviterAccount.getSource()) && !inviterName.startsWith("ChengYi")){
			inviterName = inviterName.substring(0,inviterName.length() - 9);
		}
		result.setReturnData(inviterName);
		return result;
	}


}
