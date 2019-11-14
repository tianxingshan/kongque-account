/**
 * 
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
 * @author yuehui
 *
 * @2018年1月26日
 */
@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="t_sys_info")
public class SystemInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3861809022795943744L;

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	@Column(name = "c_id")
	private String id;
	
	@Column(name="c_value")
	private String value;
	
	@Column(name="c_lable")
	private String lable;
	
	@Column(name="c_remarks")
	private String remarks;

	public SystemInfo(){}
	
	public SystemInfo (String id){
		this.id=id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
