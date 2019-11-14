/**
 * 
 */
package com.kongque.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

/**
 * @author yuehui
 *
 * @2017年12月4日
 */
@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="t_sys_role")
public class SysRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	@Column(name = "c_id")
	private String id;
	
	@Column(name="c_role_name")
	private String roleName;
	
	/**
	 * 排序
	 */
	@Column(name="c_role_order")
	private int roleOrder=1;
	
	@Column(name="c_del")
	private String del="否";
	
	@Column(name="c_remarks")
	private String remarks;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="c_sys_id")
	private SystemInfo system;
	
	/**
	 * 角色类型，0：普通，1：系统默认角色(系统预置的角色，禁止修改),11系统预置的商户管理员
	 */
	@Column(name="c_role_type")
	private String roleType="0";
	
	/**
	 * 云平台专属商户id
	 */
	@Column(name="c_business_id")
	private String businessId;

	@ManyToMany
	@Where(clause=" c_del='否'")
	@OrderBy( "sourceOrder")
	@JoinTable(name="t_role_resource",joinColumns={@JoinColumn(name="c_role_id")},
		inverseJoinColumns={@JoinColumn(name="c_resource_id")})
	private Set<SysResoure> resourceSet=new HashSet<SysResoure>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Set<SysResoure> getResourceSet() {
		return resourceSet;
	}

	public void setResourceSet(Set<SysResoure> resourceSet) {
		this.resourceSet = resourceSet;
	}

	public int getRoleOrder() {
		return roleOrder;
	}

	public void setRoleOrder(int roleOrder) {
		this.roleOrder = roleOrder;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public SystemInfo getSystem() {
		return system;
	}

	public void setSystem(SystemInfo system) {
		this.system = system;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
}
