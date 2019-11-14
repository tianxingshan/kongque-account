/**
* @author pengcheng
* @since 2018年10月24日
 */
package com.kongque.dto.account;

import java.util.Arrays;

/**
 * @author pengcheng
 * @since 2018年10月24日
 */
public class AccountQueryDto {
	
	/**
	 * 页码
	 */
	private Integer page;
	
	/**
	 * 行数
	 */
	private Integer rows;

	/**
	 * 排序属性
	 */
	private String[] sortingProperties;
	
	/**
	 * 排序方向
	 */
	private int[] sortingDirection;
	
	/**
	 * 平台账户id
	 */
	private String id;

	/**
	 * 平台账户id数组
	 */
	private String[] ids;
	
	/**
	 *  平台账户所属业务系统标识
	 */
	private String sysId;
	
	/**
	 * 平台账户所属业务系统标识数组
	 */
	private String[] sysIds;
	
	/**
	 * 平台账户名称
	 */
	private String names;
	
	/**
	 * 平台账户绑定的手机号码
	 */
	private String phone;
	
	/**
	 * 平台账户绑定的手机号码：用于手机号码的精确匹配
	 */
	private String accountPhone;
	
	/**
	 * 平台账户注册来源
	 */
	private String[] sources;
	
	/**
	 * 平台账户邀请码
	 */
	private String[] invitationCode;
	
	/**
	 * 返回数据是否带有用户的关联系统里列表信息：true[带];false[不带]
	 */
	private Boolean withSysList;

	private int total=0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String[] getSysIds() {
		return sysIds;
	}

	public void setSysIds(String[] sysIds) {
		this.sysIds = sysIds;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String[] getSources() {
		return sources;
	}

	public void setSources(String[] sources) {
		this.sources = sources;
	}

	public String[] getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String[] invitationCode) {
		this.invitationCode = invitationCode;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getAccountPhone() {
		return accountPhone;
	}

	public void setAccountPhone(String accountPhone) {
		this.accountPhone = accountPhone;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public String[] getSortingProperties() {
		return sortingProperties;
	}

	public void setSortingProperties(String[] sortingProperties) {
		this.sortingProperties = sortingProperties;
	}

	public int[] getSortingDirection() {
		return sortingDirection;
	}

	public void setSortingDirection(int[] sortingDirection) {
		this.sortingDirection = sortingDirection;
	}

	public Boolean getWithSysList() {
		return withSysList;
	}

	public void setWithSysList(Boolean withSysList) {
		this.withSysList = withSysList;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	@Override
	public String toString() {
		return "AccountQueryDto [page=" + page + ", rows=" + rows + ", sortingProperties="
				+ Arrays.toString(sortingProperties) + ", sortingDirection=" + Arrays.toString(sortingDirection)
				+ ", ids=" + Arrays.toString(ids) + ", sysId=" + sysId + ", sysIds=" + Arrays.toString(sysIds) + ", names=" + names
				+ ", phone=" + phone + ", accountPhone=" + accountPhone + ", sources=" + Arrays.toString(sources)
				+ ", invitationCode=" + Arrays.toString(invitationCode) + ", withSysList=" + withSysList + ", total="
				+ total + "]";
	}

}
