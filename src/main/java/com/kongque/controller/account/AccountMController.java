/**
* @author pengcheng
* @since 2017年10月16日
 */
package com.kongque.controller.account;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kongque.dto.account.AccountQueryDto;
import com.kongque.service.account.IAccountService;
import com.kongque.util.PageBean;
import com.kongque.util.Result;

import java.util.List;

/**
 * 后台用户账户信息管理功能调用响应接口类
 * 
 * @author pengcheng
 * @since 2017年10月16日
 */
@RestController
public class AccountMController {

	@Resource
	private IAccountService accountService;
	
	/**
	 * @Description: 分局手机号模糊查询账号
	 * @param @param page -分页
	 * @param @param phone - 手机
	 * @param @param sysId -系统标识
	 * @param @return   
	 * @return Result  
	 * @author sws
	 * @date 2019年8月30日
	 */
	@GetMapping("/account/manage/accountListByPhone")
	public Result getAccountListByPhone(PageBean page,String phone,String sysId){
		
		return accountService.getAccountListByPhone(page, phone, sysId);
	}
	
	/**
	 * 分页查询账号
	 * @param p
	 * @param username
	 * @return
	 */
	@GetMapping("/account/manage/accountList")
	public Result getAccountList(PageBean p,String username){
		
		return accountService.getAccountList(p, username, "1");
	}

	/**
	 * 获取账号为橙意系统的账号id集合
	 * @author lilishan
	 * @since 2018年7月30日
	 * @return
	 */
	@GetMapping(value="/account/chengyi/acountIds")
	public List<String> getChengYiAccountIds(AccountQueryDto queryDto){
		return accountService.getChengYiAccountIds(queryDto);
	}
}
