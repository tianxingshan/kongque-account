/**
* @author pengcheng
* @since 2017年10月16日
 */
package com.kongque.controller.account;

import com.kongque.component.impl.JsonMapper;
import com.kongque.dto.account.*;import com.kongque.constants.Constants;import com.kongque.service.account.IAccountService;
import com.kongque.util.Result;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 前台用户账户信息管理功能调用响应接口类
 * 
 * @author pengcheng
 * @since 2017年10月16日
 */
@RestController
public class AccountController {

	private static Logger logger=LoggerFactory.getLogger(AccountController.class);

	@Resource
	private IAccountService service;
	
	/**
	 * 按查询条件查询符合要求的账户信息
	 * 
	 * @author pengcheng
	 * @since 2018年12月25日
	 * @param queryDto
	 * @return
	 */
	@GetMapping(value = "/account/list", produces = "application/json")
	public Result getAccountList(AccountQueryDto queryDto){
		logger.info("请求根据以下参数获取平台账户信息信息："+queryDto);
		return service.queryAccountList(queryDto);
	}

    /**
     * 添加账号
     * @param accountRegisterDto
     * @return
     */
	@RequestMapping(value="/account/add",method= RequestMethod.POST,produces="application/json")
	public @ResponseBody Result addAccount(@RequestBody AccountRegisterDto accountRegisterDto){
		logger.info("添加账号accountRegisterDto:"+JSONObject.fromObject(accountRegisterDto) );
		return service.addAccount(accountRegisterDto);
	}

    /**
     * 修改密码
     * @param accountUpdateDto
     * @return
     */
	@RequestMapping(value="/account/password/update",method= RequestMethod.POST,produces="application/json")
	public @ResponseBody Result updatePassword(@RequestBody AccountUpdateDto accountUpdateDto){
		logger.info("修改账号:"+accountUpdateDto.getKongqueAccountId());
		return service.updatePassword(accountUpdateDto);
	}
	
	 /**
     * 校验已登录用户的密码
     * @param loginDto
     * @return
     */
	@RequestMapping(value="/account/password/verify",method= RequestMethod.POST,produces="application/json")
	public @ResponseBody Result verifyPassword(@RequestBody AccountLoginDto loginDto){
		logger.info("校验已登录用户的密码。");
		return service.verifyPassword(loginDto);
	}
	
    /**
     *检测指定账号是否与所给参数sysId对应的系统关联
     * @param accountSysDto
     * @return
     */
    @RequestMapping(value="/account/sysId/verify",method= RequestMethod.GET,produces="application/json")
    public @ResponseBody Result accountSysIdVerify(AccountSysDto accountSysDto){
        return service.verifyAccountSys(accountSysDto);
    }
	
    /**
     * 查询账号关联的系统表列表
     * @param accountSysDto
     * @return
     */
    @RequestMapping(value="/account/sysId/list",method= RequestMethod.GET,produces="application/json")
    public @ResponseBody Result findAccountSysList(String accountId){
    	logger.info("请求查询账户[accountId:"+accountId+"]所关联的系统ID列表");
        return service.findAccountSysList(accountId);
    }

    /**
     * 新增账号所属系统表
     * @param accountSysDto
     * @return
     */
    @RequestMapping(value="/account/sysId/add",method= RequestMethod.POST,produces="application/json")
	public @ResponseBody Result addAccountSys(@RequestBody AccountSysDto accountSysDto){
        logger.info("前端请求为账号[accountId:"+accountSysDto.getKongqueAccountId()+"]新增关联系统[sysIdList："+Arrays.asList((accountSysDto.getSysIds()))+"]");
	    return service.addAccountSys(accountSysDto);
    }
    
    /**
     * 删除账号所属系统表
     * @param accountSysDto
     * @return
     */
    @RequestMapping(value="/account/sysId/delete",method= RequestMethod.POST,produces="application/json")
    public @ResponseBody Result deleteAccountSys(@RequestBody AccountSysDto accountSysDto){
    	logger.info("前端请求为账号[accountId:"+accountSysDto.getKongqueAccountId()+"]删除关联系统[sysIdList："+Arrays.asList(accountSysDto.getSysIds())+"]");
        return service.deleteAccountSys(accountSysDto);
    }    

    /**
     * 根据账号ID查询账号信息
     * @param accountSysDto
     * @return
     */
    @RequestMapping(value="/account/get/{accountId}",method= RequestMethod.GET,produces="application/json")
    public @ResponseBody Result getAccountById(@PathVariable String accountId){
    	logger.info("请求查询账户[accountId:"+accountId+"]信息");
        return service.findAccountById(accountId);
    }
    /**
     * 根据账号token查询账号信息
     * @param accountSysDto
     * @return
     */
    @GetMapping(value="/account/token/{token}")
    public @ResponseBody Result getAccountByToken(@PathVariable String token){
        return service.findAccountByToken(token);
    }
    
    /**
     * 根据指定的账号id，只查询id对应的账户信息本身，返回数据中不带有任何关联信息
     * 
     * @author pengcheng
     * @since 2018年5月22日
     * @param accountId
     * @return
     */
    @GetMapping(value="/account/get-account-self/{accountId}")
    public Result getAccountSelf(@PathVariable String accountId){
    	return service.findAccountInfo(accountId, false);
    }
    /**
     * 只查询登录账户的账户信息本身，返回数据中不带有任何关联信息
     * @author pengcheng
     * @since 2018年5月17日
     * @return
     */
    @GetMapping(value="/account/logged-account-self/get")
    public Result getLoggedAccountSelf(){
    	return service.findLoggedAccountInfo(false);
    }

    /**
     * 根据账号ID查询登录名
     * @param accountId
     * @return
     */
    @RequestMapping(value="/account/username/get/{accountId}",method= RequestMethod.GET,produces="application/json")
    public @ResponseBody Result getAccountUsernameById(@PathVariable String accountId){
    	logger.info("请求查询账户[accountId:"+accountId+"]登录名");
        return service.getAccountUsernameById(accountId);
    }
    
    /**
     * 根据关键字查询账户信息是否已经存在
     * 
     * @author pengcheng
     * @since 2018年4月24日
     * @param keyWord
     * @return
     */
    @GetMapping(value="/account/check/exist/{keyWord}")
    public Result checkExistingAccount(@PathVariable String keyWord,String registerSource){
    	return service.checkExistingAccount(keyWord,registerSource);
    }
    
    /**
     * 修改账号的手机号
     * @param smsDto
     * @return
     */
    @PostMapping(value="/account/sms/edit/phone")
	public Result editAppletPhone(@RequestBody SmsDto smsDto){
		logger.info("app用户修改手机号："+JsonMapper.toJson(smsDto));
		return service.editAppletPhone(smsDto);
	}
    
    
    /**
     * @Description: 绑定手机号(衣品有调)
     * @param @param dto
     * @param @return   
     * @return Result  
     * @author sws
     * @date 2019年9月10日
     */
    @PostMapping(value="/account/bindPhone")
    public Result bindAccountPhone(@RequestBody SmsDto dto) {
    	logger.info("wx小程序绑定手机号开始  : " + JsonMapper.toJson(dto));
    	return service.bindPhone(dto);
    }
    

	/**
	 * 获取当前用户的橙意账号
	 * @author yuehui 
	 * @date: 2018年5月30日上午9:53:02
	 */
	@GetMapping("/account/chengyiAccount")
	public Result getChengYiAccount(String accountId){
		return service.getChengYiAccount(accountId);
	}

    /**
     * 根据名称模糊查询账号信息
     * @param accountName
     * @return
     */
    @GetMapping("/account/by/{accountName}")
    public Result getAccountByName(@PathVariable String accountName){
        logger.info("前端请求根据名称模糊查询账号信息列表参数信息：accountName:["+accountName+"]");
        return service.getAccountByName(accountName);
    }

    /**
     * 查询id为accountIds范围内的账号信息列表
     * @param accountIds：账号的id数组
     * @return
     */
    @GetMapping("/account/by/accountIds")
    public Result getAccountByIds( String[] accountIds ){
        logger.info("前端请求查询账号信息列表参数信息：accountIds["+JSONArray.fromObject(accountIds)+"]");
        return service.getAccountByIds(accountIds);
    }


    //---------------------->新业务

    /**
     * zongt
     * 2019年5月31日10:29:12
     * 添加 企业账号 接口。
     * @param paramsMap
     * @return
     */
    @PostMapping("/account/registerCompany")
    public Result registerCompany(@RequestBody RegisterCompanyDto rc){

        logger.info("添加企业账号参数信息：["+JSONArray.fromObject(rc).toString()+"]");
        return service.registerCompany(rc);
    }

    /**
     * zongt
     * 2019年6月5日10:56:15
     * 添加 员工账号
     * @param paramsMap
     * @return
     */
    @PostMapping("/account/registerPerson")
    public Result registerPerson(@RequestBody RegisterPersonDto rp){

        logger.info("添加员工账号参数信息：["+JSONArray.fromObject(rp).toString()+"]");
        return service.registerPerson(rp);
}

    /**
     * zongt
     * 2019年6月5日10:56:15
     * 添加 工厂账号
     * @param paramsMap
     * @return
     */
    @PostMapping("/account/registerFactory")
    public Result registerFactory(@RequestBody RegisterFactoryDto rfd){

        logger.info("添加工厂账号参数信息：["+JSONArray.fromObject(rfd).toString()+"]");
        return service.registerFactory(rfd);
    }

    /**
     * zongt
     * 2019年6月5日10:56:15
     * 校验 手机号
     * @param paramsMap
     * @return
     */
    @GetMapping("/account/check/check-phone/{phone}")
    public Result checkPhone(@PathVariable("phone") String phone){

        logger.info("校验 手机号：["+phone+"]");
        return service.getAccountByPhone(phone, Constants.KONGQUE_CLOUD_PLATFORM_SYSID);
    }
    /**
     * zongt
     * 2019年6月5日10:56:15
     * 校验 账号名
     * @param paramsMap
     * @return
     */
    @GetMapping("/account/check/check-account-name/{accountName}")
    public Result checkAccountName(@PathVariable("accountName") String accountName){

        logger.info("校验 账号名：["+accountName+"]");
        return service.getAccountByUsername(accountName,Constants.KONGQUE_CLOUD_PLATFORM_SYSID);
    }

    /**
     * 修改账号新手标识
     * @param accountId
     * @return
     */
    @GetMapping("/account/updateAccountForNewFlag/{accountId}")
    public Result updateAccountForNewFlag(@PathVariable("accountId") String accountId){

        logger.info("修改新手标识:"+accountId);
        return service.updateAccountForNewFlag(accountId);
    }

    /**
     * 修改账号新手标识
     * @param messageFlag
     * @return
     */
    @GetMapping("/account/updateAccountForMsgFlag/{accountId}")
    public Result updateAccountForMsgFlag(@PathVariable(name="accountId") String accountId){

        logger.info("修改企业完善信息标识:"+accountId);
        return service.updateAccountForMsgFlag(accountId);
    }

    /**
     * 校验管理员
     * 2019年6月18日09:25:07
     * zongt
     * @param messageFlag
     * @return
     */
    @PostMapping("/account/check/check-admin")
    public Result checkAdmin(@RequestBody RegisterPersonDto rp){

        logger.info("校验管理员:"+rp.toString());
        return service.checkAdmin(rp);
    }

    /**
     * 修改密码
     * zongt
     * 2019年5月31日15:52:48
     */
    @PostMapping("/update/updatePwd")
    public Result updatePwd(@RequestBody RegisterPersonDto rp) {

        logger.info("修改密码:"+rp.toString());
        return service.updatePwd(rp);
    }

}
