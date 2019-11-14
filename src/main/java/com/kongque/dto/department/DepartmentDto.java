package com.kongque.dto.department;

import java.util.Date;

public class DepartmentDto {
	/**
	 * ID主键
	 */
	private String id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 所有父id路径排列，使用英文逗号隔开
	 */
	private String pathIds;
	/**
	 * 所属父id
	 */
	private String parentId;
	/**
	 * 系统标识
	 */
	private String sysId;
	/**
	 * 业务id
	 * */
	private String businessId;
	/**
	 * 是否根据BusinessId对name进行重复性校验，默认true
	 * */
	private boolean ifNameVerifyRepeatBusinessId = true;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 创建日期
	 */
	private Date createTime;
	/**
	 * 修改日期
	 */
	private Date updateTime;
	/**
	 * 创建人（账号系统账号id）
	 */
	private String creatorId;
	/**
	 * 最后修改人（账号系统账号id）
	 */
	private String lastModifier;
	/**
	 * 状态：0：正常、1：删除
	 */
	private String del;

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
	public boolean isIfNameVerifyRepeatBusinessId() {
		return ifNameVerifyRepeatBusinessId;
	}
	public void setIfNameVerifyRepeatBusinessId(boolean ifNameVerifyRepeatBusinessId) {
		this.ifNameVerifyRepeatBusinessId = ifNameVerifyRepeatBusinessId;
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
	
}
