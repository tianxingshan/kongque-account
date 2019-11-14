package com.kongque.service.department.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.kongque.constants.Constants;
import com.kongque.dao.IDaoService;
import com.kongque.dto.department.DepartmentDto;
import com.kongque.entity.department.Department;
import com.kongque.service.department.IDepartmentService;
import com.kongque.util.Result;
import com.kongque.util.StringUtils;
import com.kongque.util.SysUtil;

@Service
public class DepartmentServiceImpl implements IDepartmentService{
	@Resource
	private IDaoService dao;

	private Result verifyIfNameRepeat(String sysId,String businessId,String name) {
		Criteria criteria = dao.createCriteria(Department.class);
		if(StringUtils.isNotBlank(sysId)) {
			criteria.add(Restrictions.eq("sysId", sysId));
		}
		if(StringUtils.isNotBlank(businessId)) {
			criteria.add(Restrictions.eq("businessId", businessId));
		}
		if(StringUtils.isNotBlank(name)) {
			criteria.add(Restrictions.eq("name", name));
		}
		criteria.add(Restrictions.eq("del", "0"));
		criteria.setProjection(Projections.rowCount());
		Long total = (Long) criteria.uniqueResult();
		if ( ( null != total ) && ( total > 0 ) ) {
			return new Result(Constants.DEPARTMENT.DEPARTMENT_ALREADY_EXIST,"部门名称已存在");
		}
		return new Result();
	}
	
	@Override
	public Result addDepartment(DepartmentDto departmentDto) {

		if(departmentDto.isIfNameVerifyRepeatBusinessId()) {
			//判断部门名称是否重复
			Result result = verifyIfNameRepeat(departmentDto.getSysId(),departmentDto.getBusinessId(),departmentDto.getName());
			if(!Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())) {
				return result;
			}
		}

		Department department = new Department();
		department.setName(departmentDto.getName());
		//非一级部门
		if(StringUtils.isNotBlank(departmentDto.getId())){
			Department parentDepartment = dao.findById(Department.class, departmentDto.getId());
			if(StringUtils.isBlank(parentDepartment.getPathIds())){
				department.setPathIds(parentDepartment.getId());
			}else{
				department.setPathIds(parentDepartment.getPathIds()+","+parentDepartment.getId());
			}
			department.setParentId(parentDepartment.getId());
		}
		department.setSysId(departmentDto.getSysId());
		department.setBusinessId(departmentDto.getBusinessId());
		department.setRemarks(departmentDto.getRemarks());
		department.setCreateTime(new Date());
		new SysUtil();
		department.setCreatorId(SysUtil.getAccountId());
		department.setDel("0");

		dao.save(department);
		if(StringUtils.isBlank(department.getId())) {
			return new Result(Constants.DEPARTMENT.DEPARTMENT_SAVE_ERROR,"部门保存失败");
		}

		return new Result(department);
	}
	
	@Override
	public Result deleteDepartment(DepartmentDto departmentDto) {
		if(StringUtils.isBlank(departmentDto.getId())) {
			return new Result(Constants.DEPARTMENT.DEPARTMENT_LACK_PARAMS,"部门ID未传递");
		}
		Department department = dao.findById(Department.class, departmentDto.getId());

		Criteria criteria = dao.createCriteria(Department.class);
		criteria.add(Restrictions.eq("parentId", department.getId()));
		criteria.add(Restrictions.eq("del", "0"));
		criteria.setProjection(Projections.rowCount());
		Long total = (Long) criteria.uniqueResult();
		if ( ( null != total ) && ( total > 0 ) ) {
			return new Result(Constants.DEPARTMENT.DEPARTMENT_DEL_ERROR,"待删除部门含有子部门");
		}

		department.setDel("1");
		dao.update(department);
		return new Result();
	}

	@Override
	public Result updateDepartment(DepartmentDto departmentDto) {
		Department department = dao.findById(Department.class, departmentDto.getId());
		if(null == department) {
			return new Result(Constants.DEPARTMENT.DEPARTMENT_NOT_EXIST,"部门不存在");
		}
		if(StringUtils.isNotBlank(departmentDto.getName()) 
				&& (!departmentDto.getName().equals(department.getName())) ) {
			if(departmentDto.isIfNameVerifyRepeatBusinessId()) {
				Result result = verifyIfNameRepeat(department.getSysId(),department.getBusinessId(),departmentDto.getName());
				if(!Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())) {
					return result;
				}
			}
			department.setName(departmentDto.getName());
		}
		if(StringUtils.isNotBlank(departmentDto.getRemarks()) 
				&& (!departmentDto.getRemarks().equals(department.getRemarks())) ) {
			department.setRemarks(departmentDto.getRemarks());
		}
		department.setUpdateTime(new Date());
		new SysUtil();
		department.setLastModifier(SysUtil.getAccountId());
		dao.update(department);
		return new Result(department);
	}

	@Override
	public Result getDepartmentListBySysIdbusinessId(DepartmentDto departmentDto) {
		Criteria criteria = dao.createCriteria(Department.class);
		if(StringUtils.isNotBlank(departmentDto.getSysId()) ){
			criteria.add(Restrictions.eq("sysId", departmentDto.getSysId()));
		}
		if(StringUtils.isNotBlank(departmentDto.getBusinessId()) ){
			criteria.add(Restrictions.eq("businessId", departmentDto.getBusinessId()));
		}
		criteria.add(Restrictions.eq("del", "0"));
		@SuppressWarnings("unchecked")
		List<Department> dbDepartmentList = criteria.list();

		Map<String,List<Department>> map = new HashMap<String,List<Department>>();
		for(Department item : dbDepartmentList) {
			String key = StringUtils.isBlank(item.getParentId())?"null":item.getParentId();
			if(null == map.get(key)) {
				map.put(key, new ArrayList<Department>());
			}
			map.get(key).add(item);
		}

		for(Department item : dbDepartmentList) {
			item.setChildList(map.get(item.getId()));
			item.setLeaf( ((null == item.getChildList()) || (0 == item.getChildList().size())) );
		}

		return new Result(map.get("null"));
	}
	
}
