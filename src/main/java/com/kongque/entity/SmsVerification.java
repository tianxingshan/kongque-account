package com.kongque.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "t_sms_verification")
@DynamicInsert(true)
@DynamicUpdate(true)
public class SmsVerification implements Serializable{

	private static final long serialVersionUID = 7521280222146946349L;
	
	/**
	 * ID主键
	 */
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	@Column(name = "c_id")
	private String id;
	/**
	 * 手机号
	 */
	@Column(name = "c_phone")
	private String phone;
	/**
	 * 验证码
	 */
	@Column(name = "c_verification")
	private String verification;
	/**
	 * 创建日期
	 */
	@Column(name = "c_create_time")
	private Date createTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getVerification() {
		return verification;
	}
	public void setVerification(String verification) {
		this.verification = verification;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
