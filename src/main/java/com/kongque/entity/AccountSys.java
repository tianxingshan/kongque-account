/**
* @author pengcheng
* @since 2017年10月16日
 */
package com.kongque.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 账户和系统关联关系实体类
 * 
 * @author pengcheng
 * @since 2017年10月16日
 */
@Entity
@Table(name="t_account_sys")
@DynamicInsert(true)
@DynamicUpdate(true)
public class AccountSys  implements Serializable {

	private static final long serialVersionUID = -4164888086105707315L;

	/**
	 * 主键ID
	 */
	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	@Id
	@Column(name = "c_id")
	private String id;
	
	/**
	 * 账户系统关联中的账户ID
	 */
	@Column(name="c_account_id")
	private String accountId;

	/**
	 * 账户系统关联中账户所关联的系统唯一标识
	 */
	@Column(name="c_sys_id")
	private String sysId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

}
