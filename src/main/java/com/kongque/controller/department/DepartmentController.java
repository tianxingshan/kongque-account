package com.kongque.controller.department;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kongque.component.impl.JsonMapper;
import com.kongque.dto.department.DepartmentDto;
import com.kongque.service.department.IDepartmentService;
import com.kongque.util.Result;

@Controller
public class DepartmentController {
	@Resource
	private IDepartmentService departmentService;
	

	private static Logger logger = LoggerFactory.getLogger(DepartmentController.class);

	/**
     * 添加部门
     * @param DepartmentDto
     * @return
     */
	@RequestMapping(value="/department/add",method= RequestMethod.POST,produces="application/json")
	@ResponseBody
	public Result addDepartment(@RequestBody DepartmentDto departmentDto){
		logger.info("添加部门参数:"+JsonMapper.toJson(departmentDto));
		return departmentService.addDepartment(departmentDto);
	}
	
	/**
     * 删除部门
     * @param DepartmentDto
     * @return
     */
	@RequestMapping(value="/department/delete",method= RequestMethod.POST,produces="application/json")
	@ResponseBody
	public Result deleteDepartment(@RequestBody DepartmentDto departmentDto){
		logger.info("删除部门参数:"+JsonMapper.toJson(departmentDto));
		return departmentService.deleteDepartment(departmentDto);
	}
	
	/**
     * 修改部门
     * @param DepartmentDto
     * @return
     */
	@RequestMapping(value="/department/update",method= RequestMethod.POST,produces="application/json")
	@ResponseBody
	public Result updateDepartment(@RequestBody DepartmentDto departmentDto){
		logger.info("修改部门信息参数:"+JsonMapper.toJson(departmentDto));
		return departmentService.updateDepartment(departmentDto);
	}

	/**
     * 根据系统标识以及业务id 按层级关系查询所有部门
     * @param sysId:系统标识
     * @param businessId:业务id
     * @return
     */
    @RequestMapping(value="/department/list/bysysIdbusinessId",method= RequestMethod.GET,produces="application/json")
    @ResponseBody
    public Result getDepartmentListBySysIdbusinessId(DepartmentDto departmentDto){
        return departmentService.getDepartmentListBySysIdbusinessId(departmentDto);
    }
}
