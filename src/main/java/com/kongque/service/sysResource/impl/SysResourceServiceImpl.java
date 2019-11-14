/**
 * 
 */
package com.kongque.service.sysResource.impl;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import com.kongque.component.IRedisClient;
import com.kongque.constants.Constants;
import com.kongque.dao.IDaoService;
import com.kongque.entity.Account;
import com.kongque.entity.SysResoure;
import com.kongque.entity.SysRole;
import com.kongque.entity.SystemInfo;
import com.kongque.service.sysResource.ISysResourceService;
import com.kongque.util.BeanUtil;
import com.kongque.util.JsonUtil;
import com.kongque.util.Result;
/**
 * @author yuehui
 *
 * @2017年12月13日
 */
@Service
public class SysResourceServiceImpl implements ISysResourceService {
	@Resource
	private IRedisClient redisService;
	@Resource
	private IDaoService dao;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysResource.ISysResource#getAllSysResource()
	 */
	@Override
	public Result getAllSysResource(String sysId) {
		Criteria cri = dao.createCriteria(SysResoure.class);
		if(StringUtils.isNotBlank(sysId))
			cri.add(Restrictions.eq("system.id",sysId));
		cri.add(Restrictions.isNull("father"));
		cri.add(Restrictions.eq("del", "否"));
		return new Result(JsonUtil.arrayToJson(cri.list(), new String[] { "father" }, null));
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysResource.ISysResource#getSysResourceBySysRole(java
	 * .lang.String)
	 */
	@Override
	public Result getSysResourceListBySysRole(String roleId) {
		SysRole role = dao.findById(SysRole.class, roleId);
		return new Result(JsonUtil.arrayToJson(role.getResourceSet(), new String[] { "father", "childSet" }, null));
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysResource.ISysResourceService#
	 * getSysResourceTreeBySysRole(java.lang.String)
	 */
	@Override
	public Result getSysResourceTreeBySysRole(String roleId, String type) {
		SysRole role = dao.findById(SysRole.class, roleId);
		List<SysResoure> resouceList = role.getResourceSet().stream().collect(Collectors.toList());
		if (StringUtils.isNotBlank(type))
			resouceList = resouceList.stream().filter(r -> r.getType().equals(type)).collect(Collectors.toList());
		for (SysResoure re : resouceList) {
			listToTree(re);
		}
		resouceList = resouceList.stream().filter(r -> r.getFather() == null).collect(Collectors.toList());
		return new Result(JsonUtil.arrayToJson(resouceList, new String[] { "father", "childSet" }, null));
	}
	/**
	 * list转为tree
	 * @param re
	 */
	private void listToTree(SysResoure re) {
		if (re.getFather() != null && "否".equals(re.getDel())) {
			List<SysResoure> emp = re.getFather().getSysList();
			if (emp == null)
				emp = new ArrayList<SysResoure>();
			if (!emp.stream().anyMatch(p -> p.getId().equals(re.getId())))
				emp.add(re);
			re.getFather().setSysList(emp);
			listToTree(re.getFather());
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysResource.ISysResource#saveSysResource(com.kongque.
	 * entity.SysResoure)
	 */
	@Override
	public Result saveSysResource(SysResoure resource) {
		// 数据保存
		resource.setSystem(dao.findById(SysResoure.class, resource.getFather().getId()).getSystem());
		dao.save(resource);
		// 刷新redis权限
		initSecuritySysResource();
		return new Result(resource.getId());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysResource.ISysResource#editSysResource(com.kongque.
	 * entity.SysResoure)
	 */
	@Override
	public Result editSysResource(SysResoure resoure) {
		SysResoure dbResource = dao.findById(SysResoure.class, resoure.getId());
		BeanUtil.beanCopy(dbResource, resoure);
		dao.update(dbResource);
		// 刷新redis权限
		initSecuritySysResource();
		return new Result(resoure.getId());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysResource.ISysResource#delSysResource(java.lang.
	 * String)
	 */
	@Override
	public Result delSysResource(String id) {
		SysResoure dbResource = dao.findById(SysResoure.class, id);
		dbResource.setDel("是");
		dao.update(dbResource);
		// 刷新redis权限
		initSecuritySysResource();
		return new Result(id);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysResource.ISysResourceService#
	 * initSecuritySysResource()
	 */
	@Override
	public void initSecuritySysResource() {
		Criteria cri = dao.createCriteria(SysResoure.class);
		cri.add(Restrictions.eq("check", "是"));
		cri.add(Restrictions.eq("del", "否"));
		@SuppressWarnings("unchecked")
		List<SysResoure> resourceList = cri.list();
		if (resourceList != null)
			redisService.set(Constants.REDIS_HASH_KEY.SECURITY_ALL_KEY,
					JsonUtil.arrayToJson(
							resourceList.stream().map(SysResoure::getUrlMatch).collect(Collectors.toList()))
					.toString());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysResource.ISysResourceService#
	 * getSysResourceByAccountWeb(java.lang.String)
	 */
	@Override
	public Result getSysResourceByAccountWeb(String accountId) {
		Account account = dao.findById(Account.class, accountId);
		Set<SysResoure> roleSet = new HashSet<SysResoure>();
		account.getRoleSet().stream().forEach(r -> {
			r.getResourceSet().stream().forEach(s -> {
				if (s.getDel().equals("否") && s.getCheck().equals("否")) {
					s.setUrlMatch(s.getUrlMatch().split("\\+")[0]);
					roleSet.add(s);
				}
			});
		});
		return new Result(JsonUtil.arrayToJson2(roleSet, new String[] { "urlMatch" }));
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysResource.ISysResourceService#
	 * getSysResourceMenuByToken(java.lang.String, java.lang.String)
	 */
	@Override
	public Result getSysResourceMenuByToken(String token, String sysId) {
		Account account = dao.findUniqueByProperty(Account.class, "token", token);
		Set<SysResoure> sourceSet = new HashSet<SysResoure>();
		account.getRoleSet().stream().filter(r -> r.getSystem().getId().equals(sysId))
				.forEach(r -> sourceSet.addAll(r.getResourceSet()));
		List<SysResoure> list = sourceSet.stream().filter(s -> s.getType().equals("菜单")).collect(Collectors.toList());
		return new Result(JsonUtil.arrayToJson(list, new String[] { "father", "sysList", "childSet" }, null));
	}
	// 数据排序
	@SuppressWarnings("unused")
	private List<SysResoure> filterData(List<SysResoure> s, Comparator<SysResoure> c) {
		s.sort(c);
		for (SysResoure t : s) {
			if (t.getChildSet() != null && t.getChildSet().size() > 0) {
				t.setSysList(t.getChildSet().stream().collect(Collectors.toList()));
				filterData(t.getSysList(), c);
			}
		}
		return s;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysResource.ISysResourceService#findAllSys()
	 */
	@Override
	public Result findAllSys() {
		return new Result(dao.findAll(SystemInfo.class));
	}
	@Override
	public Result checkSysResourceMenuByToken(String token, String sysId, String urls) {
		Account account = dao.findUniqueByProperty(Account.class, "token", token);
		Set<SysResoure> sourceSet = new HashSet<SysResoure>();
		account.getRoleSet().stream().filter(r -> r.getSystem().getId().equals(sysId))
				.forEach(r -> sourceSet.addAll(r.getResourceSet()));
		List<SysResoure> list = sourceSet.stream().filter((s -> s.getType().equals("菜单")&&urls.equals(s.getUrlMatch()))).collect(Collectors.toList());
		return new Result(list.size()>0?1:0) ;
	}
}
