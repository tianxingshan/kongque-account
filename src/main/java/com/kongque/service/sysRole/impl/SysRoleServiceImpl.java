/**
 * 
 */
package com.kongque.service.sysRole.impl;

import com.codingapi.tx.annotation.TxTransaction;
import com.kongque.component.IRedisClient;
import com.kongque.constants.Constants;
import com.kongque.dao.IDaoService;
import com.kongque.dto.system.SysRoleDto;
import com.kongque.entity.Account;
import com.kongque.entity.SysResoure;
import com.kongque.entity.SysRole;
import com.kongque.service.liaision.ITenantFeignService;
import com.kongque.service.sysRole.ISysRoleService;
import com.kongque.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author yuehui
 *
 * @2017年12月13日
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

	private static Logger logger = LoggerFactory.getLogger(SysRoleServiceImpl.class);

	@Resource
	private IDaoService dao;

	@Resource
	private IRedisClient redisClient;
	
	@Resource
	private ITenantFeignService tenantFeignService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysRole.ISysRoleService#getAllSysRole()
	 */
	@Override
	public Result getAllSysRole() {
		return new Result(JsonUtil.arrayToJson(dao.findListByProperty(SysRole.class, "del", "否"),
				new String[] { "resourceSet" }, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysRole.ISysRoleService#getSysRoleByToken()
	 */
	@Override
	public Result getSysRoleByToken() {

		return new Result(JsonUtil.arrayToJson(dao.findById(Account.class, SysUtil.getAccountId()).getRoleSet(),
				new String[] { "resourceSet" }, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysRole.ISysRoleService#saveSysRole(com.kongque.
	 * entity.SysRole)
	 */
	@Override
	public Result saveSysRole(SysRole sysRole) {
		dao.save(sysRole);
		return new Result(sysRole.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysRole.ISysRoleService#editSysRole(com.kongque.
	 * entity.SysRole)
	 */
	@Override
	public Result editSysRole(SysRole sysRole) {
		SysRole dbrole = dao.findById(SysRole.class, sysRole.getId());

		BeanUtil.beanCopy(dbrole, sysRole);

		dao.update(dbrole);
		return new Result(dbrole.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysRole.ISysRoleService#editAccountSysRole(java.lang.
	 * String, java.lang.String[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	@TxTransaction
	public Result editAccountSysRole(String accountId, Object[] sysRolesIds) {

		Account account = dao.findById(Account.class, accountId);
		List<SysRole> roleList = new ArrayList<SysRole>();
		if (sysRolesIds.length > 0) {
			Criteria cri = dao.createCriteria(SysRole.class);
			cri.add(Restrictions.in("id", sysRolesIds));
			roleList = cri.list();
		}
		if(account.getRoleSet() == null){
			account.setRoleSet(new HashSet<SysRole>());
		}
		account.getRoleSet().clear();
		account.getRoleSet().addAll(roleList);
		dao.update(account);

		return new Result(accountId);
	}

	/* (non-Javadoc)
	 * @see com.kongque.service.sysRole.ISysRoleService#editAccountSysRole(java.lang.String, java.lang.String, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	@TxTransaction
	public Result editAccountSysRole(String accountId, String sysId, Object[] sysRolesIds) {
		Account account = dao.findById(Account.class, accountId);
		List<SysRole> roleList = new ArrayList<SysRole>();
		if (sysRolesIds.length > 0) {
			Criteria cri = dao.createCriteria(SysRole.class);
			cri.add(Restrictions.in("id", sysRolesIds));
			cri.add(Restrictions.eq("system.id", sysId));
			roleList = cri.list();
		}
		if(account.getRoleSet() == null){
			account.setRoleSet(new HashSet<SysRole>());
		}
		if(CollectionUtils.isNotEmpty(account.getRoleSet())) account.getRoleSet().removeIf(role -> sysId.equals(role.getSystem().getId()));
		account.getRoleSet().addAll(roleList);
		dao.update(account);

		return new Result(accountId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysRole.ISysRoleService#delSysRole(java.lang.String)
	 */
	@Override
	@TxTransaction
	public Result delSysRole(String roleId) {

		SysRole role = dao.findById(SysRole.class, roleId);
		role.setDel("是");
		dao.update(role);
		return new Result(roleId);
	}

	@Override
	public String redisTokenHandle(String id, String token) {

		Account account = dao.findById(Account.class, id);
		// 权限set
		Set<String> urlset = new HashSet<String>();
		for (SysRole r : account.getRoleSet()) {
			if (r.getDel().equals("否"))
				for (SysResoure s : r.getResourceSet()) {
					if (s.getCheck().equals("是") && s.getDel().equals("否")) {
						urlset.addAll(Arrays.asList(s.getUrlMatch().split(",")));
					}
				}
		}

		// 默认访问全部
		if (urlset.size() == 0)
			urlset.add("/**");
		String s = redisClient.set(Constants.REDIS_HASH_KEY.SECURITY_KEY + token,Constants.SYSCONSTANTS.TOKEN_TIMEOUT,
				JsonUtil.arrayToJson(urlset).toString());
		if (!("OK").equals(s)) {
				return null;
		}
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysRole.ISysRoleService#editRoleResource(java.lang.
	 * String, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Result editRoleResource(String roleId, Object[] sysResourceIds,boolean addChildWithoutMenu) {

		SysRole role = dao.findById(SysRole.class, roleId);
		List<SysResoure> resourceList = new ArrayList<SysResoure>();
		if (sysResourceIds.length > 0) {
			Criteria cri = dao.createCriteria(SysResoure.class);
			cri.add(Restrictions.in("id", sysResourceIds));
			resourceList = cri.list();
		}
		// 父节点处理
		Set<SysResoure> resourceList2 = new HashSet<SysResoure>();
		for (SysResoure s : resourceList) {
			getAllNode(s, resourceList2);
		}
		if(addChildWithoutMenu) {
			for(Object resourceId : sysResourceIds) {
				SysResoure r = dao.findById(SysResoure.class, (String)resourceId);
				if(!"menu:".startsWith(r.getUrlMatch())) {
					Criteria cri = dao.createCriteria(SysResoure.class);
					cri.add(Restrictions.like("fatherIds", (String)resourceId, MatchMode.ANYWHERE));
					cri.add(Restrictions.eq("del", "否"));
					List<SysResoure> sysResoureList = cri.list();
					resourceList2.addAll(sysResoureList);
				}
			}
		}
		role.getResourceSet().clear();
		role.getResourceSet().addAll(resourceList2);
		dao.update(role);

		return new Result(roleId);
	}

	/**
	 * 获取所有父节点（包含自己）
	 * 
	 * @param list
	 * @param l
	 */
	private void getAllNode(SysResoure s, Set<SysResoure> l) {
		if (s != null) {
			l.add(s);
			getAllNode(s.getFather(), l);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysRole.ISysRoleService#saveAccountSysRole(java.lang.
	 * String, java.lang.Object[])
	 */
	@Override
	@TxTransaction
	public Result saveAccountSysRole(String accountId, Object[] sysRolesIds) {

		Criteria cri = dao.createCriteria(SysRole.class);
		cri.add(Restrictions.in("id", sysRolesIds));
		@SuppressWarnings("unchecked")
		List<SysRole> roleList = cri.list();

		Account account = dao.findById(Account.class, accountId);
		account.getRoleSet().addAll(roleList);
		dao.update(account);

		// 重新登录刷新权限信息
		return new Result(accountId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kongque.service.sysRole.ISysRoleService#getAllSysRoleMapSysId()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Result getAllSysRoleMapSysId(String sysId) {

		Criteria criteria=dao.createCriteria(SysRole.class);
		if(StringUtils.isNotBlank(sysId))
			criteria.add(Restrictions.eq("system.id",sysId));
		 criteria.add(Restrictions.eq("del","否"));
		List<SysRole> list = criteria.list();
		// 所有商户
		Set<String> tenantIds = list.stream().filter(s -> StringUtils.isNotBlank(s.getBusinessId()))
				.map(SysRole::getBusinessId).collect(Collectors.toSet());
		JSONArray tenantArray = new JSONArray();
		if (tenantIds.size() > 0) {
//			String rsJson = HttpClientUtils.doPostJson(Constants.API.TENANT_LIST_BY_URL,JSONArray.fromObject(tenantIds).toString());
//			tenantArray = JSONObject.fromObject(rsJson).optJSONArray("returnData");
			Result getResult = tenantFeignService.getTenantList(tenantIds);
			tenantArray = JSONArray.fromObject(getResult.getReturnData());
		}

		// 分组函数
		Map<String, List<SysRole>> map = list.stream().collect(Collectors.groupingBy(s -> s.getSystem().getLable()));
		List<SysRoleDto> rsList = new ArrayList<SysRoleDto>();
		for (Entry<String, List<SysRole>> e : map.entrySet()) {
			SysRoleDto sd = new SysRoleDto();
			sd.setId(e.getValue().get(0).getSystem().getId());
			sd.setRoleName(e.getKey());
			List<SysRole> l1 = new ArrayList<SysRole>();
			List<SysRole> l2 = new ArrayList<SysRole>();
			for (SysRole s : e.getValue()) {
				if (StringUtils.isBlank(s.getBusinessId())) {
					l1.add(s);
				} else {
					l2.add(s);
				}
			}
			Map<String, List<SysRole>> tenanListMap = l2.stream()
					.collect(Collectors.groupingBy(SysRole::getBusinessId));
			for (Entry<String, List<SysRole>> te : tenanListMap.entrySet()) {
				List<SysRoleDto> lr = sd.getChildList();
				if(lr==null)
					lr=new ArrayList<SysRoleDto>();
				SysRoleDto srd = new SysRoleDto();
				// 获取商户名
				String tenantName = "";
				for (Object o : tenantArray) {
					JSONObject j = (JSONObject) o;
					if (j.optString("id").equals(te.getValue().get(0).getBusinessId()) )
						tenantName = JSONObject.fromObject(j.optString("detail")).optString("companyName");
				}
				srd.setId(te.getKey());
				srd.setRoleName(tenantName);
				srd.setChildList(te.getValue());
				lr.add(srd);
				sd.setChildList(lr);
			}
			List<Object> empl = sd.getChildList();
			if (empl == null)
				empl = new ArrayList<Object>();
			empl.addAll(l1);
			sd.setChildList(empl);
			rsList.add(sd);
		}

		return new Result(JsonUtil.arrayToJson2(rsList, new String[] { "id", "system", "roleName", "roleOrder", "sysId",
				"childList", "businessId" }));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kongque.service.sysRole.ISysRoleService#getRole(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Result getRole(String name, String businessFlag, String businessId, String sysId) {

		Criteria cri = dao.createCriteria(SysRole.class);
		cri.add(Restrictions.eq("del", "否"));
		if (StringUtils.isNotBlank(name)) cri.add(Restrictions.eq("roleName", name));
		
		if ("1".equals(businessFlag) || StringUtils.isNotBlank(businessId))  cri.add(Restrictions.eq("businessId", businessId));

		if (StringUtils.isNotBlank(sysId)) cri.add(Restrictions.eq("system.id", sysId));
		
		return new Result(JsonUtil.arrayToJson(cri.list(), new String[] { "resourceSet" }, null));
	}

	/* (non-Javadoc)
	 * @see com.kongque.service.sysRole.ISysRoleService#getsysRoleResourceChild(java.lang.String)
	 */
	@Override
	public Result getsysRoleResourceChild(String roleId) {
		
		SysRole role=dao.findById(SysRole.class, roleId);
		List<String> rsId=new ArrayList<String>();
		if(role!=null)
			role.getResourceSet().stream().filter(r->(r.getChildSet()==null||r.getChildSet().size()==0)).forEach(r->rsId.add(r.getId()));
		return new Result(rsId);
	}

	@Override
	public Result getSysRoleByToken(String sysId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.kongque.service.sysRole.ISysRoleService#getRoleListForAccounts(java.lang.String[])
	 */
	@Override
	public Result getRoleListForAccounts(String[] accountIds,String[] businessIds, String[] sysIds) {
		Map<String, Set<SysRole>> accountRoleMap = new HashMap<>();
		Criteria criteria = dao.createCriteria(Account.class);
		criteria.add(Restrictions.in("id", Arrays.asList(accountIds)));
		@SuppressWarnings("unchecked")
		List<Account> accountList = criteria.list();
		if(accountList != null){
			Set<String> businessIdSet = businessIds == null || businessIds.length == 0 ? null : new HashSet<>(Arrays.asList(businessIds));
			Set<String> sysIdSet = sysIds == null || sysIds.length == 0 ? null : new HashSet<>(Arrays.asList(sysIds));
			for(Account account : accountList){
				if(sysIdSet != null && sysIdSet.size() > 0)  account.getRoleSet().removeIf(role -> !sysIdSet.contains(role.getSystem().getId()));
				if(businessIdSet != null && businessIdSet.size() > 0) account.getRoleSet().removeIf(role -> !businessIdSet.contains(role.getBusinessId()));
				accountRoleMap.put(account.getId(), account.getRoleSet());
			}
		}
		return new Result(JsonUtil.toJson(accountRoleMap,new String[]{"resourceSet"}));
	}

}
