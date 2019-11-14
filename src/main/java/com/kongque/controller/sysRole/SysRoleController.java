/**
 * 
 */
package com.kongque.controller.sysRole;

import java.util.Arrays;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kongque.entity.SysRole;
import com.kongque.service.sysRole.ISysRoleService;
import com.kongque.util.JsonUtil;
import com.kongque.util.Result;
import com.kongque.util.SysUtil;

import net.sf.json.JSONObject;

/**
 * @author yuehui
 *
 * @2017年12月12日
 */
@RestController
public class SysRoleController {
	private static Logger logger=LoggerFactory.getLogger(SysRoleController.class);

	@Resource
	private ISysRoleService roleService;
	
	/**
	 * 获取所有角色
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/role/all")
	public Result allSysRole(){
		return roleService.getAllSysRole();
	}
	
	/**
	 * 获取所有角色系统分组
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/roleSysId/all")
	public Result allSysRoleMapSysId(String sysId){
		return roleService.getAllSysRoleMapSysId(sysId);
	}
	
	/**
	 * 角色查询
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/role")
	public Result sysRole(String name,String businessFlag,String businessId,String sysId){
		return roleService.getRole(name, businessFlag, businessId, sysId);
	}
	
	/**
	 * 角色权限查询,角色id获取所有节点（父级，包含自己）
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/role/{roleId}")
	public Result sysRoleResourceChild(@PathVariable String roleId){
		return roleService.getsysRoleResourceChild(roleId);
	}
	
	
	/**
	 * 保存角色信息
	 * @param role
	 * @return
	 */
	@PostMapping(value="/account/manage/sys/role")
	public Result saveSysRole(@RequestBody SysRole role){
		
		logger.info("用户["+SysUtil.getAccountId()+"]创建角色："+JsonUtil.objToJson(role));
		return roleService.saveSysRole(role);
	}
	
	/**
	 * 修改角色信息
	 * @param role
	 * @return
	 */
	@PutMapping(value="/account/manage/sys/role")
	public Result editSysRole(@RequestBody SysRole role){

		logger.info("用户["+SysUtil.getAccountId()+"]修改角色："+JsonUtil.objToJson(role));
		return roleService.editSysRole(role);
	}
	
	/**
	 * 删除角色信息
	 * @param roleId
	 * @return
	 */
	@DeleteMapping(value="/account/manage/sys/role/{roleId}")
	public Result delSysRole(@PathVariable String roleId){
		logger.info("用户["+SysUtil.getAccountId()+"]删除用户角色:"+roleId);
		return roleService.delSysRole(roleId);
	}
	
	/**
	 * 获取当前用户角色
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/roleBytoken")
	public Result sysRole(){
		return roleService.getSysRoleByToken();
	}

	/**
	 * 根据指定的商户id和账户id，获取这些账户Id对应的账户在指定系统指定商户中的角色信息映射列表
	 * @author pengcheng
	 * @since 2018年11月14日
	 * @param accountIds
	 * @param businessIds 指定的商户id，如没有指定则获取结果没有商户id限制
	 * @param sysIds 指定的系统id，如没有指定则获取结果没有系统限制 
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/get-accountRole")
	public Result getAccountSysRole(String[] accountIds, String[] businessIds, String[] sysIds){
		logger.info("根据以下指定账户id列表获取账户在指定系统[sysIds:"+Arrays.toString(sysIds)+"]的指定商户[tenantIds:"+Arrays.toString(businessIds)+"]中对应的角色列表信息："+Arrays.toString(accountIds));
		return roleService.getRoleListForAccounts(accountIds,businessIds, sysIds);
	}
	
	/**
	 * 获取当前用户系统下的角色
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/roleBySysToken")
	public Result sysRole(String sysId){
		return roleService.getSysRoleByToken();
	}
	
	/**
	 * 添加用户角色关系
	 * @param accountId
	 * @param sysRoles
	 * @return
	 */
	@PostMapping(value="/account/manage/sys/accountRole")
	public Result saveAccountRole(@RequestBody JSONObject json){
		
		logger.info("用户["+SysUtil.getAccountId()+"]添加用户角色:"+json);
		return roleService.saveAccountSysRole(json.optString("account"),json.optJSONArray("roleIds").toArray());
	}
	
	/**
	 * 修改用户角色关系
	 * @param accountId
	 * @param sysRoles
	 * @return
	 */
	@PutMapping(value="/account/manage/sys/accountRole")
	public Result editAccountRole(@RequestBody JSONObject json){
		
		logger.info("用户["+SysUtil.getAccountId()+"]修改用户角色:"+json);
		return roleService.editAccountSysRole(json.optString("accountId"),json.optJSONArray("roleIds").toArray());
	}
	
	/**
	 * 修改在特定系统中用户的角色关系
	 * @author pengcheng
	 * @since 2019年1月2日
	 * @param json
	 * @return
	 */
	@PutMapping(value="/account/manage/sys/accountRole-in-given-sys")
	public Result editAccountRoleInGivenSystem(@RequestBody JSONObject json){		
		logger.info("用户["+SysUtil.getAccountId()+"]修改用户[accountId:"+json.getString("accountId")+"]在["+json.getString("sysId")+"]系统中的角色:"+json);
		return roleService.editAccountSysRole(json.getString("accountId"),json.getString("sysId"), json.optJSONArray("roleIds").toArray());
	}
	
	/**
	 * 修改角色权限关系
	 * @param accountId
	 * @param sysRoles
	 * @return
	 */
	@PutMapping(value="/account/manage/sys/roleResource")
	public Result editRoleRescource(@RequestBody JSONObject json){
		
		logger.info("用户["+SysUtil.getAccountId()+"]修改角色权限:"+json);
		return roleService.editRoleResource(json.optString("roleId"),json.optJSONArray("resourceIds").toArray(),json.optBoolean("addChildWithoutMenu"));
	}	
	
}
