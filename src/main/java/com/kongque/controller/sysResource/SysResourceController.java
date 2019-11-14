/**
 * 
 */
package com.kongque.controller.sysResource;

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
import com.kongque.dao.IDaoService;
import com.kongque.entity.SysResoure;
import com.kongque.service.sysResource.ISysResourceService;
import com.kongque.util.JsonUtil;
import com.kongque.util.Result;
import com.kongque.util.SysUtil;

/**
 * @author yuehui
 * 权限
 * @2017年12月12日
 */
@RestController
public class SysResourceController {

	private static Logger logger=LoggerFactory.getLogger(SysResourceController.class);

	@Resource
	private ISysResourceService resourceService;
	
	@Resource
	private IDaoService dao;
	
	/**
	 * 获取所有权限
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/resource")
	public Result allSysResource(String sysId){
		return resourceService.getAllSysResource(sysId);
	}
	
	/**
	 * 根据角色获取权限list
	 * @param roleId
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/resourceList/role/{roleId}")
	public Result getSysResourceListByRoleId(@PathVariable String roleId){
		
		return resourceService.getSysResourceListBySysRole(roleId);
	}
	
	/**
	 * 根据角色获取权限tree
	 * @param roleId
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/resourceTree/role/{roleId}")
	public Result getSysResourceTreeByRoleId(@PathVariable String roleId,String type){
		
		return resourceService.getSysResourceTreeBySysRole(roleId,type);
	}
	/**
	 * 根据token和sysId获取用户菜单列表
	 * @param token
	 * @param sysId
	 * @return
	 */
	@GetMapping(value="/account/manage/sys/resourceMenu")
	public Result getSysResourceMenuByToken(String token,String sysId){
		return resourceService.getSysResourceMenuByToken(token, sysId);
	}
	
	/**
	 * 根据token和sysId获取用户菜单列表
	 * @param token
	 * @param sysId
	 * @return  1 ：有0：无
	 */
	@GetMapping(value="/account/manage/sys/resourceMenu/check")
	public Result checkSysResourceMenuByToken(String token,String sysId,String urls){
		return resourceService.checkSysResourceMenuByToken( token, sysId, urls);
	}
	
	/**
	 * 用户保存数据
	 * @param resource
	 * @return
	 */
	@PostMapping(value="/account/manage/sys/resource")
	public Result saveSysResource(@RequestBody SysResoure resource){
		
		logger.info("用户["+SysUtil.getAccountId()+"]保存权限数据："+JsonUtil.objToJson(resource));
		return resourceService.saveSysResource(resource);
	}
	
	/**
	 * 用户修改数据
	 * @param resource
	 * @return
	 */
	@PutMapping(value="/account/manage/sys/resource")
	public Result editSysResource(@RequestBody SysResoure resource){
		logger.info("用户["+SysUtil.getAccountId()+"]修改权限数据："+JsonUtil.objToJson(resource));
		return resourceService.editSysResource(resource);
	}
	
	/**
	 * 删除权限数据
	 * @param id
	 * @return
	 */
	@DeleteMapping(value="/account/manage/sys/resource/{id}")
	public Result delSyResource(@PathVariable String id){
		new SysUtil();
		logger.info("用户["+SysUtil.getAccountId()+"]删除权限数据："+id);
		return resourceService.delSysResource(id);
	}
	
	/**
	 * 系统列表
	 * @return
	 */
	@GetMapping(value="/account/manage/sys")
	public Result getSystemy(){
		return resourceService.findAllSys();
	}
	
}
