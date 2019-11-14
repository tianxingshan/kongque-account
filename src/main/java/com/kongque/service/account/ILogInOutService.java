/**
* @author pengcheng
* @since 2017年10月16日
 */
package com.kongque.service.account;

import com.kongque.dto.account.AccountLoginDto;
import com.kongque.dto.account.LoginVerifyDto;
import com.kongque.entity.Account;
import com.kongque.model.ModelForLogin;
import com.kongque.util.Result;

/**
 * 账号系统登录管理模块业务层功能接口
 * 
 * @author pengcheng
 * @since 2017年10月16日
 */
public interface ILogInOutService {

	/**
	 * 对Redis同步操作的具体执行行为是：把相关信息添加到Redis中
	 */
	public static final int REDIS_APPEND_SYSIDS = 1;
	
	/**
	 * 对Redis同步操作的具体执行行为是：删除Redis中的相关信息
	 */
	public static final int REDIS_REMOVE_SYSIDS = -1;
	
	/**
	 * 对Redis同步操作的具体执行行为是：修改Redis中账户的状态信息
	 */
	public static final int REDIS_UPDATE_ACCOUNT_STATUS = 2;
	
	/**
	 * 根据登录信息参数处理用户登入系统的业务逻辑
	 * 
	 * @author pengcheng
	 * @since 2017年10月17日
	 * @param dto 封装登录信息参数的包装类
	 * @return com.kongque.util.Result: ModelForLogin returnData
	 */
	public Result logIn(AccountLoginDto dto);

	/**
	 * yuehui
	 * 2019-04-16
	 * 登录并获取权限
	 * @param dto
	 * @return
	 */
	public Result loginForPermission(AccountLoginDto dto);
	
	/**
	 * 根据token参数，处理该token对应的账户登出系统的业务逻辑
	 * 
	 * @author pengcheng
	 * @since 2017年10月17日
	 * @param token 要退出系统的账号在redis中对应的token
	 * @return com.kongque.util.Result
	 */
	public Result logOut(String token);
	
	/**
	 * 根据token参数验证用户是否已经登录或该token是否过期
	 * 
	 * @author pengcheng
	 * @since 2017年10月17日
	 * @param dto 登录验证参数包装类，其中包括：要进行登录验证的账号在redis中对应的凭证（token），以及要验证是否与登录账户具有关联关系的系统的唯一识别标志
	 * @return com.kongque.util.Result boolean——true：验证通过；false：验证失败
	 */
	public Result verify(LoginVerifyDto dto);
	
	/**
	 * 根据登录凭证(token)参数查询该凭证对应的登录信息
	 * 
	 * @author pengcheng
	 * @since 2017年10月24日
	 * @param token 登录凭证(token)参数
	 * @return com.kongque.util.Result ModelForLogin returnData
	 */
	public Result getLoginInfo(String token);
	
	/**
	 * 验证所给token对应的账户信息是否符合登录sysId所标识系统的要求
	 * 
	 * @author pengcheng
	 * @since 2017年10月20日
	 * @param token 登录成功时由后端返回的账户登录凭证
	 * @param sysId 需要验证与token所对应账户是否具有关联关系的系统的唯一识别标识
	 * @return String "200"：验证通过；"010007"：账户未登录；"010005"：系统和账号未建立关联
	 */
	public String checkToken(String token,String sysId);
	
	/**
	 * 根据参数获取该参数对应的登录账户信息的相关数据
	 * 
	 * @author pengcheng
	 * @since 2017年10月17日
	 * @param token 要获取相关数据的登录账户对应的token
	 * @return com.kongque.entity.Account 返回的账户信息；null 账户没有登录或登录已失效
	 */
	public ModelForLogin fetchLoginInfo(String token);
	
	/**
	 * 把登录成功的账户信息添加到登录信息存储容器中
	 * 
	 * @author pengcheng
	 * @since 2017年10月18日
	 * @param account 登录成功的账户信息
	 * @param expire 本次登录的有效时间（单位：秒）
	 * @return ModelForLogin 当前登录用户保存在登录数据信息存储容器的登录信息数据模型；null 账号登录信息保存失败
	 */
	public ModelForLogin saveLoginInfo(Account account,Integer expire);
	
	/**
	 * 向Redis中保存的相关登录账号信息添加新的或删除已有的账号和系统的关联关系
	 * 
	 * @author pengcheng
	 * @since 2017年10月20日
	 * @param token 需要同步关联关系的账户号信息在Redis中对应的token
	 * @param sysIdList 需要同步到Redis中的系统唯一标识
	 * @param operationCode 本次同步的需要执行的具体操作代码 REDISOPERATION_ADD：添加操作；REDISOPERATION_REMOVE：删除操作
	 * @return Boolean 本次同步操作是否执行成功的布尔类型标识 true：执行成功；false：执行失败
	 */
	public Boolean synchronizeRedis(ModelForLogin updatingModel,int operationCode);
	
	/**
	 * 向Redis中保存的相关登录账号信息添加账号和系统的新关联关系
	 * 
	 * @author pengcheng
	 * @since 2017年10月18日
	 * @param token 需要添加新关联关系的账户号信息在Redis中对应的token
	 * @param sysId 需要关联的新系统的唯一标识
	 */	
	public Boolean addAccountSysToRedis(String token,String[] sysIdList);
	
	/**
	 * 从Redis中保存的相关登录账号信息删除账号和某个系统的关联关系
	 * 
	 * @author pengcheng
	 * @since 2017年10月18日
	 * @param token 需要删除已有关联关系的账户号信息在Redis中对应的token
	 * @param sysId 需要删除与其关联的系统的唯一标识
	 */	
	public Boolean removeAccountSysFromRedis(String token,String[] sysIdList);

	/**
	 * 登录校验用户系统权限接口
	 * @param accountLoginDto
	 * @return
	 */
	Result accountLogInCheckUser(AccountLoginDto accountLoginDto);
}
