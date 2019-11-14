/**
 * @author yuehui 
 * @date: 2018年3月29日
 */
package com.kongque.controller.account;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.kongque.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kongque.component.impl.JsonMapper;
import com.kongque.dto.account.AccountUpdateDto;
import com.kongque.service.account.IAccountService;
import com.kongque.util.Result;

/**
 * @author yuehui
 * App账号相关接口
 * @date: 2018年3月29日 下午2:02:21
 */
@RestController
public class AccountAppController {


	private static Logger logger=LoggerFactory.getLogger(AccountAppController.class);

	@Resource
	private IAccountService service;
	

    /**
     * 修改密码
     * @param accountUpdateDto
     * @return
     */
	@RequestMapping(value="/app/account/password/update",method= RequestMethod.POST,produces="application/json")
	public @ResponseBody Result updatePassword(@RequestBody AccountUpdateDto accountUpdateDto){
		logger.info("app用户修改密码:"+JsonMapper.toJson(accountUpdateDto));
		return service.updatePassword(accountUpdateDto);
	}
    
    /**
     * 根据微信平台参数检查孔雀云平台账户信息
     * 
     * @author pengcheng
     * @since 2018年5月4日
     * @param weChatDto
     * @return
     */
    @GetMapping(value="/app/account/checking/WeChat-arguments/{openid}")
    public Result checkAccountByWeChatArguments(@PathVariable String openid){
    	return service.accountCheckingByWeChatArguments(openid,null);
    }
        
    /**
     * 通过请求header中的token获取对应账户中的微信平台相关信息
     * @author pengcheng
     * @since 2018年5月10日
     * @return
     */
    @GetMapping(value="/app/account/WeChat-info/get",produces="application/json")
    public Result getWeChatInfo(){
		return service.findWeChatInfo();
	}
        
    /**
     * 为授权的橙意小程序登录用户修改其默认注册平台账号的登录用户名的专用接口
     * 
     * @author pengcheng
     * @since 2018年5月18日
     * @param nickname
     * @return
     */
    @PutMapping(value="/app/account/ChengYi/modify-username/with-nickname")
    public Result modifyUsernameWithNickname(@RequestBody Map<String, String> arguments){
    	return service.updateUserNameForChengYiAccount(arguments.get("nickname"));
    }
    
    /**
     * 通过id列表批量获取不带关联信息的孔雀云平台账户信息
     * 
     * @author pengcheng
     * @since 2018年5月31日
     * @param ids 云平台账户id列表
     * @return
     */
    @GetMapping(value="/app/account/batch-get",produces="application/json")
    public Result<List<Account>> getAccountInBatch(String[] ids){
    	logger.info("请求按以下id列表查询对应的平台账户信息："+Arrays.toString(ids));
		return service.findAccountListById(ids, false);
	}
}
