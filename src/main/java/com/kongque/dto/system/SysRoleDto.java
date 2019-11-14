/**
 * 
 */
package com.kongque.dto.system;

import java.util.List;

/**
 * @author yuehui
 *
 * @2017年12月29日
 * 
 * 系统角色添加系统id标识
 */
public class SysRoleDto<T> {

	private String id;
	
	private String roleName;
	
	private List<T> childList;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<T> getChildList() {
		return childList;
	}

	public void setChildList(List<T> childList) {
		this.childList = childList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
