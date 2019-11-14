/**
 * @author pengcheng
 * @since 2017年10月16日
 */
package com.kongque.service.account;

import com.kongque.controller.account.RegisterCompanyDto;
import com.kongque.dto.account.*;
import com.kongque.entity.Account;
import com.kongque.entity.AccountSys;
import com.kongque.util.PageBean;
import com.kongque.util.Result;

import java.util.List;

/**
 * 账号信息管理业务层功能接口
 *
 * @author pengcheng
 * @since 2017年10月16日
 */
public interface IAccountService {

    /**
     * 添加账号
     * @param registerInfo
     * @return
     */
    Result addAccount(AccountRegisterDto registerInfo);

    /**
     * 校验登录账号的密码
     *
     * @author pengcheng
     * @since 2018年4月20日
     * @param loginDto
     * @return
     */
    Result verifyPassword(AccountLoginDto loginDto);

    /**
     * 修改密码
     * @param updateInfo
     * @return
     */
    Result updatePassword(AccountUpdateDto updateInfo);

    /**
     *添加关联关系（内部调用）
     * @param account
     * @param sysIds
     */
    void sysRelate(Account account, String[] sysIds);

    /**
     * 添加账号和系统关联关系
     * @param sys
     * @return
     */
    Result addAccountSys(AccountSysDto sys);

    /**
     * 账号所属的系统中是否包含参数sysId
     * @param sys
     * @return
     */
    Result verifyAccountSys(AccountSysDto sys);

    /**
     * 查询账号所属的系统表列表
     * @param sys
     * @return
     */
    Result findAccountSysList(String accountId);

    /**
     * 根据账户ID查找账户信息
     *
     * @author pengcheng
     * @since 2017年12月5日
     * @param accountId
     * @return
     */
    Result findAccountById(String accountId);

    /**
     * 根据账户ID查找账户信息。（不包含密码信息）
     * 当isAll为true时返回该账户的信息及其所有关联信息；
     * 当isAll为false时只返回账户本身的信息
     *
     * @author pengcheng
     * @since 2018年5月22日
     * @param accountId
     * @param isAll
     * @return
     */
    Result<Account> findAccountInfo(String accountId, Boolean isAll);

    /**
     * 根据token查询用户
     * @param token
     * @return
     */
    Result findAccountByToken(String token);

    /**
     * 查找登录用户的完整账户信息（不包含密码信息）
     * 当isAll为true时返回该账户的信息及其所有关联信息；
     * 当isAll为false时只返回账户本身的信息
     *
     * @author pengcheng
     * @since 2018年5月17日
     * @return
     */
    Result findLoggedAccountInfo(Boolean isAll);

    /**
     * 根据token查询用户的微信平台信息
     *
     * @author pengcheng
     * @since 2018年5月10日
     * @param token
     * @return
     */
    Result findWeChatInfo();

    /**
     * 根据openid检查显存账户信息，如果账户存在则返回账户的用户名称
     * @author pengcheng
     * @since 2018年5月4日
     * @param openid
     * @return
     */
    Result accountCheckingByWeChatArguments(String openid, String unionid);

    /**
     * 根据用户登录名和登录用户的注册来源业务id查找账号
     *
     * @author pengcheng
     * @since 2018年7月9日
     * @param loginName 用户登录名
     * @param registerSource 注册来源业务id查找账号
     * @return
     */
    Account findByLoginName(String loginName, String registerSource);

    /**
     * 根据id查找账号信息
     * @param id
     * @return
     */
    Account findById(String id);

    /**
     * 修改账号信息，登录时间、token等
     * @param account
     */
    boolean updateAccount(Account account);

    /**
     * 根据账号id和系统标识查找
     * @param accountId
     * @param sysId
     * @return
     */
    AccountSys findByAccountIdAndSysId(String accountId, String sysId);

    /**
     * 根据token获取账户信息
     * @param token
     * @return
     */
    Account findByToken(String token);

    /**
     * 分页查询账号
     * @param p
     * @param username
     * @return
     */
    Result getAccountList(PageBean p, String username, String status);
    
  
    /**
     * @Description: 根据手机号模糊分页查询账号
     * @param @param page
     * @param @param rows
     * @param @param phone
     * @param @return   
     * @return Result<List<Account>>  
     * @author sws
     * @date 2019年8月30日
     */
    Result getAccountListByPhone(PageBean page,String phone,String sysId);

    /**
     * 根据账号ID查询登录名
     * */
    Result getAccountUsernameById(String accountId);

    /**
     * 根据关键字检查账户信息是否已经存在
     *
     * @author pengcheng
     * @since 2018年4月24日
     * @param keyWord
     * @return
     */
    Result checkExistingAccount(String keyWord, String registerSource);

    /**
     *
     * @param smsDto
     * @return
     */
    Result editAppletPhone(SmsDto smsDto);
    
    /**
     * @Description: 绑定手机号
     * @param @param dto
     * @param @return   
     * @return Result  
     * @author sws
     * @date 2019年9月10日
     */
    Result bindPhone(SmsDto dto);

    /**
     * 删除账号所属系统表
     * @param sys
     * @return
     */
    Result deleteAccountSys(AccountSysDto sys);

    /**
     * 为授权的橙意小程序登录用户修改其默认注册平台账号的登录用户名的专用接口
     *
     * @author pengcheng
     * @since 2018年5月18日
     * @param nickname
     * @return
     */
    Result updateUserNameForChengYiAccount(String nickname);

    /**
     * 根据账号获取对应的橙意账号
     * @author yuehui
     * @date: 2018年5月30日上午9:55:24
     */
    Result getChengYiAccount(String accountId);

    /**
     * 根据账户id列表批量获取与id对应的云平台账户信息
     *
     * @author pengcheng
     * @since 2018年5月31日
     * @param ids 云平台账户id列表
     * @param wholeAccount 是否返回账户的其它关联信息：true返回；false不返回
     * @return
     */
    Result<List<Account>> findAccountListById(String[] ids, boolean wholeAccount);

    /**
     * 根据名称模糊查询账号信息列表
     * @param accountName
     * @return
     */
    Result getAccountByName(String accountName);

    /**
     * 查询账号id为：accountIds范围内的账号信息
     * @param accountIds
     * @return
     */
    Result getAccountByIds(String[] accountIds);

    /**
     * 获取账号为橙意系统的账号id集合
     * @author lilishan
     * @since 2018年7月30日
     * @return
     */
    List<String> getChengYiAccountIds(AccountQueryDto queryDto);

    /**
     * 根据查询参数查询符合要求的账户信息
     *
     * @author pengcheng
     * @since 2018年12月25日
     * @param queryDto
     * @return
     */
    Result queryAccountList(AccountQueryDto queryDto);


    //------------------新业务

    /**
     * zongt
     * 2019年5月31日10:29:12
     * 添加 企业账号 接口。
     * @param paramsMap
     * @return
     */
    Result registerCompany(RegisterCompanyDto rc);

    /**
     * zongt
     * 2019年5月31日10:29:12
     * 添加 员工 接口。
     * @param paramsMap
     * @return
     */
    Result registerPerson(RegisterPersonDto rp);

    /**
     * zongt
     * 2019年6月5日10:56:15
     * 添加 工厂账号 接口。
     * @param paramsMap
     * @return
     */
    Result registerFactory(RegisterFactoryDto rfd);

    /**
     * 根据用户名和系统id查询用户
     * zongt
     * 2019年6月5日09:35:12
     * @param username
     * @param sysId
     * @return
     */
    Result getAccountByUsername(String username,String sysId);

    /**
     * 根据用户名和系统id查询用户
     * zongt
     * 2019年6月5日09:35:12
     * @param username
     * @param sysId
     * @return
     */
    Result getAccountByPhone(String phone,String sysId);

    /**
     * 修改新手标识
     * zongt
     * 2019年6月5日09:35:12
     * @param username
     * @param sysId
     * @return
     */
    Result updateAccountForNewFlag(String accountId);

    /**
     * 修改账号新手标识
     * @param accountId
     * @return
     */
    Result updateAccountForMsgFlag(String accountId);


    /**
     * 校验管理员
     * @param rp
     * @return
     */
    Result checkAdmin(RegisterPersonDto rp);

    /**
     * 修改密码
     * zongt
     * 2019年5月31日15:52:48
     */
    Result updatePwd(RegisterPersonDto rp);
}
