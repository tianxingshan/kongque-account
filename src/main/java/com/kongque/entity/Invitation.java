/**
* @author pengcheng
* @since 2018年5月4日
 */
package com.kongque.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author pengcheng
 * @since 2018年5月4日
 */
@Entity
@Table(name="t_invitation")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Invitation implements Serializable {

	private static final long serialVersionUID = 480388366149522249L;
	
	/**
	 * ID主键
	 */
	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	@Id
	@Column(name = "c_id")
	private String id;
	
	/**
	 * 被邀请人
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="c_invitee_id")
	private Account inviteeAccount;
	
	/**
	 * 邀请人
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="c_inviter_id")
	private Account inviterAccount;

	/**
	 * 修改人id
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="c_update_account_id")
	private Account updateAccount;

	/**
	 * 被邀请人类型（普通：PT、销售：XS、合伙人：HHR）
	 */
	@Column(name="c_inviter_type")
	private String inviterType;

	/**
	 * 投诉内容
	 */
	@Column(name="c_complaint_info")
	private String complaintInfo;

	@Column(name="c_create_time")
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Account getInviteeAccount() {
		return inviteeAccount;
	}

	public void setInviteeAccount(Account inviteeAccount) {
		this.inviteeAccount = inviteeAccount;
	}

	public Account getInviterAccount() {
		return inviterAccount;
	}

	public void setInviterAccount(Account inviterAccount) {
		this.inviterAccount = inviterAccount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Account getUpdateAccount() {
		return updateAccount;
	}

	public void setUpdateAccount(Account updateAccount) {
		this.updateAccount = updateAccount;
	}

	public String getInviterType() {
		return inviterType;
	}

	public void setInviterType(String inviterType) {
		this.inviterType = inviterType;
	}

	public String getComplaintInfo() {
		return complaintInfo;
	}

	public void setComplaintInfo(String complaintInfo) {
		this.complaintInfo = complaintInfo;
	}
}
