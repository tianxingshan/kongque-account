package com.kongque.entity.department;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="t_department")
@DynamicInsert(true)  
@DynamicUpdate(true)
public class Department implements Serializable{

	private static final long serialVersionUID = 3927773429220082685L;
	/**
	 * ID主键
	 */
	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	@Id
	@Column(name = "c_id")
	private String id;
	/**
	 * 名称
	 * */
	@Column(name="c_name")
	private String name;
	/**
	 * 所有父id路径排列，使用英文逗号隔开
	 */
	@Column(name="c_path_ids")
	private String pathIds;
	/**
	 * 所属父id
	 */
	@Column(name="c_parent_id")
	private String parentId;
	/**
	 * 系统标识
	 */
	@Column(name="c_sys_id")
	private String sysId;
	/**
	 * 业务id
	 * */
	@Column(name="c_business_id")
	private String businessId;
	/**
	 * 备注
	 * */
	@Column(name="c_remarks")
	private String remarks;
	/**
	 * 创建日期
	 * */
	@Column(name="c_create_time")
	private Date createTime;
	/**
	 * 修改日期
	 * */
	@Column(name="c_update_time")
	private Date updateTime;
	/**
	 * 创建人（账号系统账号id）
	 * */
	@Column(name="c_creator_id")
	private String creatorId;
	/**
	 * 最后修改人（账号系统账号id）
	 * */
	@Column(name="c_last_modifier")
	private String lastModifier;
	/**
	 * 状态：0：正常、1：删除
	 * */
	@Column(name="c_del")
	private String del;
	/**
	 * 子节点
	 * */
	@Transient
	private List<Department> childList;
	/**
	 *  是否是叶子节点
	 * */
	@Transient
	private boolean isLeaf;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathIds() {
		return pathIds;
	}

	public void setPathIds(String pathIds) {
		this.pathIds = pathIds;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getLastModifier() {
		return lastModifier;
	}

	public void setLastModifier(String lastModifier) {
		this.lastModifier = lastModifier;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public List<Department> getChildList() {
		return childList;
	}

	public void setChildList(List<Department> childList) {
		this.childList = childList;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
}
