package com.kongque.service.department;

import com.kongque.dto.department.DepartmentDto;
import com.kongque.util.Result;

public interface IDepartmentService {
	/**
	 * 添加部门
	 * */
	Result addDepartment(DepartmentDto departmentDto);
	/**
	 * 删除部门
	 * */
	Result deleteDepartment(DepartmentDto departmentDto);
	/**
	 * 修改部门
	 * */
	Result updateDepartment(DepartmentDto departmentDto);
	/**
	 * 根据系统标识以及业务id 按层级关系查询所有部门
	 * */
	Result getDepartmentListBySysIdbusinessId(DepartmentDto departmentDto);
	
}
