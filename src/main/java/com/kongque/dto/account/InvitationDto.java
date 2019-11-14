/**
* @author pengcheng
* @since 2018年5月4日
 */
package com.kongque.dto.account;

import java.util.Arrays;

/**
 * @author pengcheng
 * @since 2018年5月4日
 */
public class InvitationDto {

	private String id;

	//被邀请人
	private String inviteeId;
	
	//被邀请人平台账户id列表
	private String[] inviteeIds;

	//推荐人
	private String inviterId;
	
	//推荐人平台账户id列表
	private String[] inviterIds;
	
	//推荐人平台账户名称
	private String inviterAccountName;

	//修改后的推荐人id
	private String referrerId;

	//投诉内容
	private String complaintInfo;

	//推荐人会员类型
	private String inviterType;


	public String getInviteeId() {
		return inviteeId;
	}

	public void setInviteeId(String inviteeId) {
		this.inviteeId = inviteeId;
	}

	public String[] getInviteeIds() {
		return inviteeIds;
	}

	public void setInviteeIds(String[] inviteeIds) {
		this.inviteeIds = inviteeIds;
	}

	public String getInviterId() {
		return inviterId;
	}

	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}

	public String[] getInviterIds() {
		return inviterIds;
	}

	public void setInviterIds(String[] inviterIds) {
		this.inviterIds = inviterIds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComplaintInfo() {
		return complaintInfo;
	}

	public void setComplaintInfo(String complaintInfo) {
		this.complaintInfo = complaintInfo;
	}

	public String getReferrerId() {
		return referrerId;
	}

	public void setReferrerId(String referrerId) {
		this.referrerId = referrerId;
	}

	public String getInviterType() {
		return inviterType;
	}

	public void setInviterType(String inviterType) {
		this.inviterType = inviterType;
	}

	public String getInviterAccountName() {
		return inviterAccountName;
	}

	public void setInviterAccountName(String inviterAccountName) {
		this.inviterAccountName = inviterAccountName;
	}

	@Override
	public String toString() {
		return "InvitationDto [id=" + id + ", inviteeId=" + inviteeId + ", inviteeIds=" + Arrays.toString(inviteeIds)
				+ ", inviterId=" + inviterId + ", inviterIds=" + Arrays.toString(inviterIds) + ", inviterAccountName="
				+ inviterAccountName + ", referrerId=" + referrerId + ", complaintInfo=" + complaintInfo
				+ ", inviterType=" + inviterType + "]";
	}

}
