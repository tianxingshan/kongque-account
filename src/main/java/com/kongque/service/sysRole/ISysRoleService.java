/**
 * 
 */
package com.kongque.service.sysRole;

import com.kongque.entity.SysRole;
import com.kongque.util.Result;

/**
 * @author yuehui
 *
 * @2017年12月12日
 */
public interface ISysRoleService {

	/**
	 * 获取所有角色
	 * @return
	 */
	public Result getAllSysRole();
	
	/**
	 * 系统标识map角色
	 * @return
	 */
	public Result getAllSysRoleMapSysId(String sysId);
	
	/**
	 * 根据token获取用户角色
	 * @return
	 */
	public Result getSysRoleByToken();	
	
	
	/**
	 * 根据token和系统获取用户角色
	 * @return
	 */
	public Result getSysRoleByToken(String sysId);
	
	/**
	 * 根据指定的商户id和账户id，获取这些账户Id对应的账户的账户Id和角色信息的映射列表
	 * 
	 * @author pengcheng
	 * @since 2018年11月14日
	 * @param accountIds 指定的账户id
	 * @param businessIds 指定的商户id，如没有指定则获取结果没有商户id限制
	 * @param sysIds 指定的系统id，如没有指定则获取结果没有系统限制 
	 * @return
	 */
	public Result getRoleListForAccounts(String[] accountIds, String[] businessIds, String[] sysIds);	
	
	/**
	 * 保存角色
	 * @param sysRole
	 * @return
	 */
	public Result saveSysRole(SysRole sysRole);
	
	/**
	 * 修改角色
	 * @param sysRole
	 * @return
	 */
	public Result editSysRole(SysRole sysRole);
	
	/**
	 * 添加用户角色
	 * @param sysRoles
	 * @return
	 */
	public Result saveAccountSysRole(String accountId ,Object[] sysRolesIds);
	
	
	/**
	 * 修改用户角色
	 * @param sysRoles
	 * @return
	 */
	public Result editAccountSysRole(String accountId ,Object[] sysRolesIds);
	
	/**
	 * 修改用户在特定系统中的角色
	 * 
	 * @author pengcheng
	 * @since 2019年1月2日
	 * @param accountId
	 * @param sysId
	 * @param sysRolesIds
	 * @return
	 */
	public Result editAccountSysRole(String accountId ,String sysId, Object[] sysRolesIds);
	
	/**
	 * 修改角色对应权限
	 * @param roleId
	 * @param sysResourceIds
	 * @return
	 */
	public Result editRoleResource(String roleId,Object[] sysResourceIds,boolean addChildWithoutMenu);
	
	/**
	 * 删除角色
	 * @param roleId
	 * @return
	 */
	public Result delSysRole(String roleId);
	
	/**
	 * redis token权限处理
	 * @param id
	 * @param token
	 * @return
	 */
	public String redisTokenHandle(String id, String token);
	
	/**
	 * 角色查询
	 * businessFlag是否查询空商户,1：查询
	 * @param name
	 * @param business
	 * @param sysId
	 * @return
	 */
	public Result getRole(String name,String businessFlag,String business,String sysId);
	
	/**
	 * 获取所有节点（父级，包含自己）
	 * @param roleId
	 * @return
	 */
	public Result getsysRoleResourceChild(String roleId);
	
}
