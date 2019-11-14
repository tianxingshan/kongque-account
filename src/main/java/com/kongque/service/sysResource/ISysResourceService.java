/**
 * 
 */
package com.kongque.service.sysResource;

import com.kongque.entity.SysResoure;
import com.kongque.util.Result;

/**
 * @author yuehui
 *
 * @2017年12月12日
 */
public interface ISysResourceService {

	/**
	 * 获取所有权限
	 * @return
	 */
	public Result getAllSysResource(String sysId);
	
	/**
	 * 权限redis初始化
	 */
	public void initSecuritySysResource();
	
	/**
	 * 根据角色id获取角色所有权限List
	 * @param roleId
	 * @return
	 */
	public Result getSysResourceListBySysRole(String roleId);
	
	/**
	 * 根据角色id获取角色所有权限tree
	 * @param roleId：角色id  type：权限类型
	 * @return
	 */
	public Result getSysResourceTreeBySysRole(String roleId,String type);
	
	
	/**
	 * 根据用户token,sysId获取菜单列表
	 * @param token
	 * @return
	 */
	public Result getSysResourceMenuByToken(String token,String sysId);
	
	/**
	 * 根据用户token,sysId校验拥有改权限url（前端）
	 * @param token
	 * @return 1 ：有0：无
	 */
	public Result checkSysResourceMenuByToken(String token,String sysId,String urls);
	
	/**
	 * 用户获取url,无权限
	 * @param token
	 * @return
	 */
	public Result getSysResourceByAccountWeb(String accountId);
	/**
	 * 保存权限数据
	 * @param resource
	 * @return
	 */
	public Result saveSysResource(SysResoure resource);
	
	
	/**
	 * 修改权数据
	 * @param resoure
	 * @return
	 */
	public Result editSysResource(SysResoure resoure);
	
	/**
	 * 删除权限数据
	 * @param id
	 * @return
	 */
	public Result delSysResource(String id);
	
	/**
	 * 系统名
	 * @return
	 */
	public Result findAllSys();
}
