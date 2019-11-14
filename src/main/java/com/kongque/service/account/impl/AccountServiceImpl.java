package com.kongque.service.account.impl;

import com.codingapi.tx.annotation.TxTransaction;
import com.kongque.component.IRedisClient;
import com.kongque.component.impl.JsonMapper;
import com.kongque.constants.Constants;
import com.kongque.controller.account.RegisterCompanyDto;
import com.kongque.dao.IDaoService;
import com.kongque.dto.account.*;
import com.kongque.entity.Account;
import com.kongque.entity.AccountSys;
import com.kongque.model.ModelForLogin;
import com.kongque.service.account.IAccountService;
import com.kongque.service.account.ILogInOutService;
import com.kongque.service.account.ISmsSendService;
import com.kongque.service.invitation.IInvitationService;
import com.kongque.service.liaision.IMemebershipFeignService;
import com.kongque.service.sysRole.ISysRoleService;
import com.kongque.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author JinXiaoyang 2017/10/17.
 */
@Service
public class AccountServiceImpl implements IAccountService {

    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Resource
    private IDaoService dao;

    @Resource
    private ILogInOutService logInOutService;

    @Resource
    private ISmsSendService smsSendService;

    @Resource
    private IInvitationService InvitationService;

    @Resource
    private IMemebershipFeignService membershipFeignService;

    @Resource
    private IRedisClient redis;

    @Resource
    private ISysRoleService sysRoleService;

    @Override
    @TxTransaction
    public Result addAccount(AccountRegisterDto registerInfo) {
        //注册新账户
        Account accountList = findByLoginName(registerInfo.getUsername(), registerInfo.getSysId());
        if (accountList != null) {
            logger.error("账号已存在:" + registerInfo.getUsername());
            return new Result(Constants.RESULT_CODE.ACCOUNT_ALREATY_EXIST, "账号已存在，请更换其他账号");
        }
        Account newAccount = new Account();
        newAccount.setCreateTime(new Date());
        newAccount.setUsername(registerInfo.getUsername());
        newAccount.setPassword(CryptographyUtils.md5(registerInfo.getPassword()));
        newAccount.setSysId(registerInfo.getSysId());
        newAccount.setInvitationCode(StringUtils.getUUCode(8));
        if (StringUtils.isNotBlank(registerInfo.getOpenid())) {
            newAccount.setOpenid(registerInfo.getOpenid());
        }
        if (StringUtils.isNotBlank(registerInfo.getUnionid())) {
            newAccount.setUnionid(registerInfo.getUnionid());
        }
        if (StringUtils.isNotBlank(registerInfo.getSource())) {
            newAccount.setSource(registerInfo.getSource());
        }
        boolean isOldAccount = false;
        if (StringUtils.isNotBlank(registerInfo.getPhone())) {
            Criteria criteria = dao.createCriteria(Account.class);
            criteria.add(Restrictions.eq("phone", registerInfo.getPhone()));
            criteria.add(Restrictions.eq("sysId", newAccount.getSysId()));
            Account existedAccount = (Account) criteria.uniqueResult();
            if (existedAccount != null) {//如果当前手机号已绑定到旧衣品有调微信商户下所拥有的平台账户上
                //小程序手机绑定旧账号
                if (registerInfo.getSource().equals(Constants.SYS.REGISTER_SOURCE_WX)) {
                    //把属于旧衣品有调商户的平台账户的微信账户参数，修改为注册信息中所带来的新衣品有调商户下的微信账户参数
                    logger.info("当前注册绑定的手机号[" + registerInfo.getPhone() + "]已绑定到旧衣品有调微信商户下所拥有的平台账户上:" + existedAccount);
                    existedAccount.setOpenid(newAccount.getOpenid());
                    existedAccount.setUnionid(newAccount.getUnionid());
                    if (existedAccount.getPassword().equals(existedAccount.getOpenid())) {
                        existedAccount.setPassword(newAccount.getOpenid());
                    }
                    existedAccount.setToken(null);
                    newAccount   = existedAccount;
                    isOldAccount = true;
                    logger.info("把旧衣品有调微信商户下所拥有平台账户的微信账户参数修改为以下新的微信账户参数" + newAccount);
                }
                else {
                    logger.error("注册手机号已存在：" + JsonUtil.objToJson(registerInfo));
                    return new Result(Constants.RESULT_CODE.POHONE_ALREATY_EXIST, "手机号已存在");
                }

            }
            else {
                newAccount.setPhone(registerInfo.getPhone());
            }
        }
        if (!isOldAccount) {
            newAccount.setStatus("1");
        }
        newAccount.setUpdateTime(new Date());
        newAccount.setLastLoginTime(new Date());
        dao.saveOrUpdate(newAccount);

        //为账户执行登录操作
        if (registerInfo.getExpire() == null) {// 如果登录有效期参数值为空

            registerInfo.setExpire(Constants.SYSCONSTANTS.TOKEN_TIMEOUT);// 本次登录的有效期。
        }
        Account savedAccount = dao.findById(Account.class, newAccount.getId());
        if (StringUtils.isNotBlank(registerInfo.getSessionKey())) {
            savedAccount.setSessionKey(registerInfo.getSessionKey());
        }
        ModelForLogin loginModel = logInOutService.saveLoginInfo(savedAccount, registerInfo.getExpire());
        if (loginModel == null) {
            IDaoService.transactionRollback();
            return new Result(Constants.REDIS_OPERATION_ERROR.SET_ERROR, "登入系统失败：Redis系统保存登录信息失败");
        }
        savedAccount.setToken(loginModel.getToken());
        dao.update(savedAccount);
        if (!isOldAccount) {//如果当前账户是新创建账户
            try {
                sysRelate(newAccount, registerInfo.getSysIds());
            }
            catch (Exception e) {
                IDaoService.transactionRollback();
                logger.error("添加账号关联系统失败", e);
                return new Result(Constants.RESULT_CODE.ASSOCIATION_ESTABLISHMENT_FAILURE, "添加账号关联系统失败，请稍后重试");
            }

            //创建邀请关系
            if (StringUtils.isNotBlank(registerInfo.getInviterCode())) {//如果当前注册用户的邀请人平台用户id不为空
                Account inviterAccount = dao.findUniqueByProperty(Account.class, "invitationCode", registerInfo.getInviterCode());
                SysUtil.getRequest().setAttribute("token", loginModel.getToken());//为下面的外部系统接口请求调用设置token
                InvitationService.addInvitation(newAccount.getId(), inviterAccount.getId());//为当前注册账户创建邀请关系
            }
        }
        registerInfo.setKongqueAccountId(savedAccount.getId());
        registerInfo.setToken(loginModel.getToken());
        registerInfo.setInvitationCode(savedAccount.getInvitationCode());
        String[] s = {"kongqueAccountId", "token", "invitationCode"};
        return new Result(JsonUtil.toJson2(registerInfo, s));
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#verifyPassword(com.kongque.dto.account.AccountLoginDto)
     */
    @Override
    public Result verifyPassword(AccountLoginDto loginDto) {
        Result  result  = new Result();
        Account account = findByToken(SysUtil.getToken());
        result.setReturnData(account != null && account.getPassword().equals(loginDto.getPassword()) ? true : false);
        return result;
    }

    @Override
    public Result updatePassword(AccountUpdateDto updateInfo) {
        Account account = dao.findById(Account.class, updateInfo.getKongqueAccountId());
        if (account != null) {
            if (!account.getPassword().equals(CryptographyUtils.md5(updateInfo.getOldPassword()))) {
                logger.error("旧密码不正确，请重新输入");
                return new Result(Constants.RESULT_CODE.PWD_ERROR, "旧密码不正确，请重新输入");
            }
            account.setPassword(CryptographyUtils.md5(updateInfo.getNewPassword()));
            account.setUpdateTime(new Date());
            dao.update(account);
            return new Result();
        }
        logger.error("无效的账号id:" + updateInfo.getKongqueAccountId());
        return new Result(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST, "无效的账号id");
    }

    @Override
    public void sysRelate(Account account, String[] sysIds) {
        List<AccountSys> list = account.getSysList();
        if (list == null)
            list = new ArrayList<AccountSys>();
        for (String sysId : sysIds) {
            AccountSys sys = new AccountSys();
            sys.setAccountId(account.getId());
            sys.setSysId(sysId);
            list.add(sys);
        }
        account.setSysList(list);
        dao.update(account);
    }

    @Override
    @TxTransaction
    public Result addAccountSys(AccountSysDto sys) {
        Result result = new Result();
        // 向数据库中添加新增的关联关系
        Account account = dao.findById(Account.class, sys.getKongqueAccountId());
        if (account == null) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("账号不存在");
            logger.error("账户ID[" + sys.getKongqueAccountId() + "]对应的账户信息不存在");
            return result;
        }
        try {
            sysRelate(account, sys.getSysIds());
        }
        catch (Exception e) {
            result.setReturnCode(Constants.RESULT_CODE.ASSOCIATION_ESTABLISHMENT_FAILURE);
            result.setReturnMsg("添加账号关联系统失败，请稍后重试");
            logger.error("添加账号关联系统失败", e);
            return result;
        }
        // 同步Redis
        ModelForLogin updatingModel = new ModelForLogin();
        updatingModel.setAccountId(sys.getKongqueAccountId());
        updatingModel.setSysSet(new HashSet<>(Arrays.asList(sys.getSysIds())));
        if (!logInOutService.synchronizeRedis(updatingModel, ILogInOutService.REDIS_APPEND_SYSIDS)) {
            IDaoService.transactionRollback();
            result.setReturnCode(Constants.RESULT_CODE.ASSOCIATION_ESTABLISHMENT_FAILURE);
            result.setReturnMsg("添加账号关联系统失败，请稍后重试");
            logger.error("更新redis失败");
            return result;
        }
        return result;
    }

    @Override
    public Result verifyAccountSys(AccountSysDto sys) {
        Result               result       = new Result();
        Map<String, Boolean> verifyResult = new HashMap<>();
        for (String sysId : sys.getSysIds()) {
            verifyResult.put(sysId, false);
        }
        List<AccountSys> accountSysList = dao.findListByProperty(AccountSys.class, "accountId",
                                                                 sys.getKongqueAccountId());
        if (accountSysList != null) {
            for (AccountSys accountSys : accountSysList) {
                if (verifyResult.keySet().contains(accountSys.getSysId())) {
                    verifyResult.replace(accountSys.getSysId(), true);
                }
            }
        }
        result.setReturnData(verifyResult);
        return result;
    }

    @Override
    public Result findAccountSysList(String accountId) {
        Result      result   = new Result();
        Set<String> sysIdSet = new HashSet<>();
        result.setReturnData(sysIdSet);
        List<AccountSys> accountSysList = dao.findListByProperty(AccountSys.class, "accountId", accountId);
        if (accountSysList != null) {
            for (AccountSys accountSys : accountSysList) {
                sysIdSet.add(accountSys.getSysId());
            }
        }
        logger.info("账户[accountId:" + accountId + "]所关联的系统查询执行成功：" + sysIdSet);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result deleteAccountSys(AccountSysDto sys) {
        Result result = new Result();
        // 删除参数中指定的关联关系在数据库中的数据
        Criteria criteria = dao.createCriteria(AccountSys.class);
        criteria.add(Restrictions.eq("accountId", sys.getKongqueAccountId()));
        criteria.add(Restrictions.in("sysId", (Object[]) sys.getSysIds()));
        List<AccountSys> accountSysList = criteria.list();
        if (accountSysList != null && !accountSysList.isEmpty()) {
            dao.deleteAllEntity(accountSysList);
            // 同步Redis
            ModelForLogin updatingModel = new ModelForLogin();
            updatingModel.setAccountId(sys.getKongqueAccountId());
            updatingModel.setSysSet(new HashSet<>(Arrays.asList(sys.getSysIds())));
            if (!logInOutService.synchronizeRedis(updatingModel, ILogInOutService.REDIS_REMOVE_SYSIDS)) {
                IDaoService.transactionRollback();
                result.setReturnCode(Constants.RESULT_CODE.ASSOCIATION_DELETE_FAILURE);
                result.setReturnMsg("删除账号关联系统失败，请稍后重试");
                logger.error("更新redis失败");
                return result;
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.kongque.service.IAccountService#findAccountById(java.lang.String)
     */
    @Override
    public Result findAccountById(String accountId) {
        Result  result  = new Result();
        Account account = findById(accountId);
        if (account == null) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("账户不存在！");
            logger.error("[id:" + accountId + "]所对应的账户信息不存在");
            return result;
        }
        account.setPassword(null);
        account.setRoleSet(null);
        result.setReturnData(account);
        logger.info("[id:" + accountId + "]所对应的账户信息查询完毕：" + account);
        return result;
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#findAccountInfo(java.lang.String, java.lang.Boolean)
     */
    @Override
    public Result<Account> findAccountInfo(String accountId, Boolean isAll) {
        Result<Account>  result  = new Result();
        Account account = findById(accountId);
        if (account == null) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("账户不存在！");
            logger.error("[id:" + accountId + "]所对应的账户信息不存在");
            return result;
        }
        account.setPassword(null);
        if (!isAll) {
            account.setRoleSet(null);
            account.setSysList(null);
        }
        result.setReturnData(account);
        logger.info("[id:" + accountId + "]所对应的账户信息查询完毕：" + account);
        return result;
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#findLoggedAccountInfo()
     */
    @Override
    public Result findLoggedAccountInfo(Boolean isAll) {
        Result  result  = new Result();
        Account account = findByToken(SysUtil.getToken());
        if (account == null) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("账户已登出或登录超时！");
            logger.error(SysUtil.getToken() + "所对应的账户已登出或登录超时");
            return result;
        }
        account.setPassword(null);
        if (!isAll) {
            account.setRoleSet(null);
            account.setSysList(null);
        }
        result.setReturnData(account);
        logger.info(SysUtil.getToken() + "]所对应的账户信息查询完毕：" + account);
        return result;
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#findByLoginName(java.lang.String, java.lang.String)
     */
    @Override
    public Account findByLoginName(String loginName, String registerSource) {
        if (StringUtils.isBlank(loginName)) {
            return null;
        }
        Criteria criteria = dao.createCriteria(Account.class);
        criteria.add(Restrictions.or(Restrictions.eq("username", loginName), Restrictions.eq("phone", loginName)));
        criteria.add(Restrictions.eq("sysId", registerSource));
        Account account = (Account) criteria.uniqueResult();
		/*if (account != null) {
			Hibernate.initialize(account.getSysList());
			account.setRoleSet(null);
			//Hibernate.initialize(account.getRoleSet());
			//dao.evict(account);
		}*/
        return account;
    }

    @Override
    public Account findById(String id) {
        Account account = dao.findById(Account.class, id);
        if (account != null) {
            Hibernate.initialize(account.getSysList());
            Hibernate.initialize(account.getRoleSet());
            dao.evict(account);
        }
        return account;
    }

    @Override
    public boolean updateAccount(Account account) {
		/*Account persistentAccount = dao.findById(Account.class, account.getId());
		if(!"1".equals(persistentAccount.getStatus())){
			logger.info("账号[" + account.getUsername() + "]状态异常，账户信息修改失败。");
			return false;
		}
		if(StringUtils.isNotBlank(account.getPassword()) && !account.getPassword().equals(persistentAccount.getPassword())){
			persistentAccount.setPassword(account.getPassword());
			persistentAccount.setUpdateTime(new Date());
		}
		if(StringUtils.isNotBlank(account.getOpenid()) && !account.getOpenid().equals(persistentAccount.getOpenid())){
			persistentAccount.setOpenid(account.getOpenid());
		}
		if(StringUtils.isNotBlank(account.getUnionid()) && !account.getUnionid().equals(persistentAccount.getUnionid())){
			persistentAccount.setUnionid(account.getUnionid());
		}
		if(StringUtils.isNotBlank(account.getToken()) && !account.getToken().equals(persistentAccount.getToken())){
			persistentAccount.setToken(account.getToken());
		}
		if(account.getLastLoginTime() != null && account.getLastLoginTime().compareTo(persistentAccount.getLastLoginTime()) > 0 ){
			persistentAccount.setLastLoginTime(account.getLastLoginTime());
		}
		if(account.getLastLogoutTime() != null && account.getLastLogoutTime().compareTo(persistentAccount.getLastLogoutTime()) > 0 ){
			persistentAccount.setLastLogoutTime(account.getLastLogoutTime());
		}*/
        dao.update(account);
        return true;
    }

    @Override
    public AccountSys findByAccountIdAndSysId(String accountId, String sysId) {
        Criteria criteria = dao.createCriteria(AccountSys.class);
        criteria.add(Restrictions.eq("sysId", sysId));
        criteria.add(Restrictions.eq("accountId", accountId));
        AccountSys accountSys = (AccountSys) criteria.uniqueResult();
        return accountSys;
    }

    @Override
    public Account findByToken(String token) {
        if (StringUtils.isBlank(token))
            return null;
        Account account = dao.findUniqueByProperty(Account.class, "token", token);
		/*if (account != null) {
			Hibernate.initialize(account.getSysList());
			Hibernate.initialize(account.getRoleSet());
			dao.evict(account);
		}*/
        return account;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.kongque.service.account.IAccountService#getAccountList(com.kongque.
     * util.PageBean, java.lang.String)
     */
    @Override
    public Result getAccountList(PageBean p, String username, String status) {

        Criteria cri = dao.createCriteria(Account.class);
        if (StringUtils.isNotBlank(status))
            cri.add(Restrictions.eq("status", status));
        if (StringUtils.isNotBlank(username))
            cri.add(Restrictions.like("username", username, MatchMode.ANYWHERE));
        return new Result(JsonUtil.arrayToJson(dao.findListWithPagebeanCriteria(cri, p), new String[]{"resourceSet", "sysList", "password"}, null), dao.findTotalWithCriteria(cri));
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#findAccountByToken(java.lang.String)
     */
    @Override
    public Result findAccountByToken(String token) {

        return new Result(JsonUtil.toJson2(findByToken(token), new String[]{"id", "username"}));
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#findWeChatInfoByToken(java.lang.String)
     */
    @Override
    public Result findWeChatInfo() {
        String token     = SysUtil.getToken();
        Result getResult = logInOutService.getLoginInfo(token);
        if (!Constants.RESULT_CODE.SUCCESS.equals(getResult.getReturnCode())) {
            logger.error("通过token获取登录用户的微信平台信息失败：" + getResult.getReturnMsg());
            return getResult;
        }
        Result  result  = new Result();
        Account account = findByToken(token);
        if (account == null) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("非法token");
            result.setReturnData(new JSONObject());
            logger.error("登录用户的微信平台信息获取失败：" + getResult.getReturnMsg());
        }
        else {
            JSONObject returnData = JsonUtil.toJson2(account, new String[]{"openid", "unionid"});
            returnData.element("sessionKey", JSONObject.fromObject(getResult.getReturnData()).getString("sessionKey"));
            result.setReturnData(returnData);
        }
        return result;
    }

    @Override
    public Result getAccountUsernameById(String accountId) {
        Criteria cri = dao.createCriteria(Account.class);
        cri.add(Restrictions.eq("id", accountId));
        cri.setProjection(Projections.property("username"));
        @SuppressWarnings("unchecked")
        List<String> list = cri.list();
        if ((null != list) && (list.size() > 0)) {
            return new Result(list.get(0));
        }
        else {
            return new Result();
        }
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#checkAccount(java.lang.String)
     */
    @Override
    public Result checkExistingAccount(String keyWord, String registerSource) {
        Result  result  = new Result();
        Account account = findByLoginName(keyWord, registerSource);
        result.setReturnData(account == null ? true : false);
        return result;
    }

    @Override
    @TxTransaction(isStart = true)
    public Result editAppletPhone(SmsDto smsDto) {
        if (StringUtils.isNotBlank(smsDto.getPhone())) {//如果本次手机号码修改之前账户手机号已经存在
            //校验旧手机号码的验证码
            SmsDto smsCheckDto = new SmsDto();
            smsCheckDto.setPhone(smsDto.getPhone());
            smsCheckDto.setNumber(smsDto.getNumber());
            Result rs1 = smsSendService.smsIstrue(smsCheckDto);
            if (!rs1.getReturnCode().equals("200")) {
                logger.error("原手机验证码错误：[" + smsDto.getPhone() + ":" + smsDto.getNumber() + "]");
                return new Result(Constants.SMSCODE.OLD_PHONE_CODE_ERROR, rs1.getReturnMsg());
            }
        }
        // 校验新手机号码的验证码
        SmsDto smsCheckDto = new SmsDto();
        smsCheckDto.setPhone(smsDto.getNewPhone());
        smsCheckDto.setNumber(smsDto.getNewNumber());
        Result rs2 = smsSendService.smsIstrue(smsCheckDto);
        if (!rs2.getReturnCode().equals("200")) {
            logger.error("新手机验证码错误：[" + smsDto.getNewPhone() + ":" + smsDto.getNewNumber() + "]");
            return new Result(Constants.SMSCODE.NEW_PHONE_CODE_ERROR, rs2.getReturnMsg());
        }
        //校验新手机号码在相同注册来源业务系统内是否重复
        Criteria criteria = dao.createCriteria(Account.class);
        criteria.add(Restrictions.eq("phone", smsDto.getNewPhone()));
        criteria.add(Restrictions.ne("status", "3"));
        criteria.add(Restrictions.eq("sysId", Constants.YIPINYOUDIAO_SYSID));
        Account existedAccount = (Account) criteria.uniqueResult();
        if (existedAccount != null) {
            if (StringUtils.isBlank(smsDto.getPhone())) {//衣品有调用户绑定微信信息
                Account loginedAccount = dao.findById(Account.class, smsDto.getAccountId());
                existedAccount.setOpenid(loginedAccount.getOpenid());
                existedAccount.setUnionid(loginedAccount.getUnionid());
                loginedAccount.setOpenid(null);
                loginedAccount.setUnionid(null);
                //登出旧新账户的token
                if (loginedAccount.getToken() != null) {
                    redis.remove(loginedAccount.getToken());
                    loginedAccount.setToken(null);
                }
                dao.updateAllEntity(Arrays.asList(existedAccount, loginedAccount));
                return new Result(Constants.RESULT_CODE.ACCOUNT_RELOGIN, "重新登录");
            }
            else if (!existedAccount.getId().equals(smsDto.getAccountId())) {
                logger.error("以下要修改的新手机号码已被占用：[" + smsDto.getNewPhone() + "]");
                return new Result(Constants.RESULT_CODE.POHONE_ALREATY_EXIST, "该手机号码已经被占用，请换一个号码再试！");
            }
            else {
                return new Result(Constants.RESULT_CODE.SUCCESS, "手机修改成功！");
            }
        }

        Result getMembershipResult = membershipFeignService.getMembershipInfo(smsDto.getAccountId());
        if (Constants.RESULT_CODE.SUCCESS.equals(getMembershipResult.getReturnCode())) {
            // 修改会员信息中的手机号码
            JSONObject membershipInfo   = JSONObject.fromObject(getMembershipResult.getReturnData());
            JSONObject membershipDetail = JSONObject.fromObject(membershipInfo.optString("detail"));
            if (membershipDetail == null || membershipDetail.isNullObject()) {
                membershipDetail = new JSONObject();
            }
            membershipDetail.element("mobile", smsDto.getNewPhone());
            Map<String, Object> mapp = new HashMap<>();
            mapp.put("id", membershipInfo.optString("id"));
            mapp.put("detail", membershipDetail);
            Result editResult = membershipFeignService.editMembershipDetail(mapp);
            if (!Constants.RESULT_CODE.SUCCESS.equals(editResult.getReturnCode())) {
                IDaoService.transactionRollback();
                logger.error(JsonMapper.toJson(smsDto) + "\nrs:" + JSONObject.fromObject(getMembershipResult).toString());
                return editResult;
            }

            //修改账户中的手机号码
            Account loginedAccount = dao.findById(Account.class, smsDto.getAccountId());
            if (loginedAccount != null) {
                if (StringUtils.isNotBlank(smsDto.getNewPhone())) {
                    loginedAccount.setPhone(smsDto.getNewPhone());
                    dao.update(loginedAccount);
                }
            }
            return new Result(Constants.RESULT_CODE.SUCCESS, "手机修改成功！");
        }
        return new Result(Constants.RESULT_CODE.ACCOUNT_EXCEPTION, "账号异常");
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#findAccountByWeChatArguments(com.kongque.dto.account.WeChatDto)
     */
    @Override
    public Result accountCheckingByWeChatArguments(String openid, String unionid) {
        logger.info("app端请求按以下微信平台参数查询平台账户信息：openid=" + openid + ", unionid=" + unionid);
        Result   result   = new Result();
        Criteria criteria = dao.createCriteria(Account.class);
        criteria.add(Restrictions.ne("status", "3"));
        if (StringUtils.isNotBlank(openid)) {
            criteria.add(Restrictions.eq("openid", openid));
        }
        if (StringUtils.isNotBlank(unionid)) {
            criteria.add(Restrictions.eq("unionid", unionid));
        }
        Account account = (Account) criteria.uniqueResult();
        if (account == null) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("当前孔雀云平台账户不存在!");
            return result;
        }
        result.setReturnData(account.getUsername());
        if (!"1".equals(account.getStatus())) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_EXCEPTION);
            result.setReturnMsg("孔雀云平台账户状态异常，请联系管理人员");
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#updateUserNameForChengYiAccount(java.lang.String)
     */
    @Override
    @TxTransaction
    public Result updateUserNameForChengYiAccount(String nickname) {
        Result  result         = new Result();
        Account chengYiAccount = findByToken(SysUtil.getToken());
        if (chengYiAccount == null) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("账户已登出或登录超时！");
            logger.error(SysUtil.getToken() + "所对应的账户已登出或登录超时");
            return result;
        }
        //如果要被修改的用户名不是已ChengYi开头的则不进行用户名的修改操作
        if (!chengYiAccount.getUsername().startsWith("ChengYi")) {
            return result;
        }
        String  newUsername = nickname + "_" + StringUtils.getUUCode(8);
        Account account     = findByLoginName(newUsername, "yipinyoudiao-applet");
        if (account != null) {
            logger.error("账号已存在:" + newUsername);
            return new Result(Constants.RESULT_CODE.ACCOUNT_ALREATY_EXIST, "账号已存在，请更换其他账号");
        }
        chengYiAccount.setUsername(newUsername);
        dao.update(chengYiAccount);
        return result;
    }

    @Override
    public Result getChengYiAccount(String accountId) {

        if (StringUtils.isBlank(accountId))
            accountId = SysUtil.getAccountId();
        Account account = dao.findUniqueByProperty(Account.class, "mappedId", accountId);
        if (account != null) {
            return new Result(JsonUtil.toJson2(account, new String[]{"id", "username"}));
        }

        return new Result();
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#findAccountListById(java.lang.String[])
     */
    @Override
    public Result<List<Account>> findAccountListById(String[] ids, boolean wholeAccount) {
        Result   result   = new Result();
        Criteria criteria = dao.createCriteria(Account.class);
        if (ids == null || ids.length == 0)
            return new Result();
        criteria.add(Restrictions.in("id", Arrays.asList(ids)));
        @SuppressWarnings("unchecked")
        List<Account> accountList = criteria.list();
        if (accountList == null || accountList.isEmpty()) {
            result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
            result.setReturnMsg("账户不存在！");
            logger.error("以下id列表所对应的账户信息不存在：" + Arrays.asList(ids));
            return result;
        }
        for (Account account : accountList) {
            account.setPassword(null);
            if (!wholeAccount) {
                account.setRoleSet(null);
                account.setSysList(null);
            }
        }
        result.setReturnData(accountList);
        return result;
    }

    @Override
    public Result getAccountByName(String accountName) {
        Result   result   = new Result();
        Criteria criteria = dao.createCriteria(Account.class);
        criteria.add(Restrictions.like("username", accountName, MatchMode.ANYWHERE));
        criteria.add(Restrictions.eq("status", "1"));
        @SuppressWarnings("unchecked")
        List<Account> list = criteria.list();
        if (null != list && list.size() > 0) {
            result.setReturnData(list);
            result.setTotal(dao.findTotalWithCriteria(criteria));
            return result;
        }
        return null;
    }

    @Override
    public Result getAccountByIds(String[] accountIds) {
        if (accountIds != null && accountIds.length > 0) {
            Criteria criteria = dao.createCriteria(Account.class);
            criteria.add(Restrictions.eq("status", "1"));
            List<String> list = new ArrayList<>();
            for (String accountId : accountIds) {
                list.add(accountId);
            }
            logger.info("需要查询的id集合为：" + JSONArray.fromObject(list));
            @SuppressWarnings("unchecked")
            List<Account> data = criteria.add(Restrictions.in("id", list)).list();
            return new Result(data);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getChengYiAccountIds(AccountQueryDto queryDto) {
        Criteria criteria = dao.createCriteria(Account.class);
        //依品有调用户
        criteria.add(Restrictions.eq("status", "1"));
        if (queryDto.getSysIds() == null || queryDto.getSysIds().length == 0)
            queryDto.setSysIds(new String[]{Constants.YIPINYOUDIAO_SYSID});
        criteria = buildRestrictions(criteria, queryDto);

        List<Account> list = new ArrayList<>();
        if (queryDto.getTotal() != 0)
            list = dao.findListWithPagebeanCriteria(criteria, new PageBean(queryDto.getTotal(), 1));
        else
            list = criteria.list();
        if (null != list && list.size() > 0) {
            List<String> accountIds = new ArrayList<>();
            for (Account acount : list) {
                accountIds.add(acount.getId());
            }
            return accountIds;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.kongque.service.account.IAccountService#queryAccountList(com.kongque.dto.account.AccountQueryDto)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Result queryAccountList(AccountQueryDto queryDto) {
        Result   result   = new Result();
        Criteria criteria = dao.createCriteria(Account.class);
        criteria.add(Restrictions.eq("status", "1"));
        criteria = buildRestrictions(criteria, queryDto);
        List<Account> accountList = null;
        if (queryDto.getPage() == null || queryDto.getRows() == null)
            accountList = criteria.list();
        else {
            PageBean pageBean = new PageBean();
            pageBean.setPage(queryDto.getPage());
            pageBean.setRows(queryDto.getRows());
            accountList = dao.findListWithPagebeanCriteria(criteria, pageBean);
            result.setTotal(dao.findTotalWithCriteria(criteria));
        }
        logger.info("本次共查出[" + accountList.size() + "]条符合以下查询条件的账户信息：" + queryDto);
        for (Account account : accountList)
            account.setPassword(null);
        result.setReturnData(JsonUtil.arrayToJson(accountList, new String[]{"sysList", "system","resourceSet"}, null));
        return result;
    }

    private Criteria buildRestrictions(Criteria criteria, AccountQueryDto queryDto) {

        if (StringUtils.isNotBlank(queryDto.getId())) {
            criteria.add(Restrictions.eq("id", queryDto.getId()));
            return criteria;
        }

        //对本次查询设置排序约束
        if (ArrayUtils.isNotEmpty(queryDto.getSortingProperties()) && ArrayUtils.isNotEmpty(queryDto.getSortingDirection()))
            for (int index = 0; index < queryDto.getSortingProperties().length; ++index)
                 criteria.addOrder(queryDto.getSortingDirection()[index] > 0 ? Order.asc(queryDto.getSortingProperties()[index]) : Order.desc(queryDto.getSortingProperties()[index]));

        if (queryDto.getIds() != null && queryDto.getIds().length > 0)
            criteria.add(Restrictions.in("id", Arrays.asList(queryDto.getIds())));
        if (StringUtils.isNotBlank(queryDto.getNames()))
            criteria.add(Restrictions.like("username", queryDto.getNames(), MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(queryDto.getPhone()))
            criteria.add(Restrictions.like("phone", queryDto.getPhone(), MatchMode.ANYWHERE));
        if (StringUtils.isNotBlank(queryDto.getAccountPhone()))
            criteria.add(Restrictions.eq("phone", queryDto.getAccountPhone()));
        if (queryDto.getSysIds() != null && queryDto.getSysIds().length > 0)
            criteria.add(Restrictions.in("sysId", queryDto.getSysIds()));
        if (queryDto.getSources() != null && queryDto.getSources().length > 0)
            criteria.add(Restrictions.in("source", queryDto.getSources()));
        return criteria;
    }


    //----------------新业务

    /**
     * zongt
     * 2019年5月31日10:29:12
     * 添加 企业账号 接口。
     *
     * @param paramsMap
     * @return
     */
    @Override
    @TxTransaction
    public Result registerCompany(RegisterCompanyDto rc) {

        Result guResult = getAccountByUsername(rc.getUsername(),Constants.KONGQUE_CLOUD_PLATFORM_SYSID);

        if (!Constants.RESULT_CODE.SUCCESS.equals(guResult.getReturnCode())) {
            logger.error("用户名:" + rc.getUsername());
            return guResult;
        }

        Result gpResult = getAccountByPhone(rc.getPhone(), Constants.KONGQUE_CLOUD_PLATFORM_SYSID);

        if (!Constants.RESULT_CODE.SUCCESS.equals(gpResult.getReturnCode())) {
            logger.error("账号已存在:" + rc.getPhone());
            return gpResult;
        }

        Account newAccount = new Account();

        newAccount.setCreateTime(new Date());
        newAccount.setUsername(rc.getUsername());
        newAccount.setPassword(CryptographyUtils.md5(rc.getPassword()));
        newAccount.setSysId(rc.getSysId());
        newAccount.setInvitationCode(StringUtils.getUUCode(8));
        newAccount.setPhone(rc.getPhone());
        newAccount.setSource(rc.getSource());
        newAccount.setStatus("1");
        newAccount.setNewFlag("0");
        newAccount.setMessageFlag("0");
        newAccount.setUpdateTime(new Date());
        newAccount.setLastLoginTime(new Date());

        dao.save(newAccount);

        sysRelate(newAccount, rc.getSysIds());

        return new Result(new JSONObject().element("accountId",newAccount.getId()));
    }

    /**
     * zongt
     * 2019年5月31日10:29:12
     * 添加 员工账号 接口。
     *
     * @param paramsMap
     * @return
     */
    @Override
    @TxTransaction
    public Result registerPerson(RegisterPersonDto rp) {

        Date date = new Date();

        if(StringUtils.isNotBlank(rp.getAccountId())){

            Account byId = dao.findById(Account.class, rp.getAccountId());

            byId.setPhone(rp.getPhone());
            byId.setUpdateTime(date);

            dao.update(byId);

            //添加角色
            sysRoleService.editAccountSysRole(rp.getAccountId(),Constants.KONGQUE_CLOUD_PLATFORM_SYSID,rp.getRoleIds());

            return new Result(new JSONObject().element("accountId",byId.getId()));
        }else {

            Account newAccount = new Account();
            //注册新账户
            Result<Account> guResult = getAccountByUsername(rp.getAccountName(),Constants.KONGQUE_CLOUD_PLATFORM_SYSID);

            if (!Constants.RESULT_CODE.SUCCESS.equals(guResult.getReturnCode())) {
                logger.error("更改商户关系:" + rp.getAccountName());
                newAccount= guResult.getReturnData();
            }

            Result<Account> gpResult = getAccountByPhone(rp.getPhone(), Constants.KONGQUE_CLOUD_PLATFORM_SYSID);

            if (!Constants.RESULT_CODE.SUCCESS.equals(gpResult.getReturnCode())) {
                logger.error("更改商户关系:" + rp.getPhone());
                newAccount= gpResult.getReturnData();
            }

            if(StringUtils.isBlank(newAccount.getId())){
                newAccount.setCreateTime(date);
                newAccount.setUsername(rp.getAccountName());
                newAccount.setPassword(CryptographyUtils.md5(rp.getPwd()));
                newAccount.setSysId(Constants.KONGQUE_CLOUD_PLATFORM_SYSID);
                newAccount.setInvitationCode(StringUtils.getUUCode(8));
                newAccount.setPhone(rp.getPhone());
                newAccount.setSource("0");
                newAccount.setStatus("1");
                newAccount.setMessageFlag("2");
                newAccount.setUpdateTime(date);
                newAccount.setLastLoginTime(date);

                dao.save(newAccount);

                sysRelate(newAccount, new String[]{Constants.KONGQUE_CLOUD_PLATFORM_SYSID});

            }else{
                newAccount.setUsername(rp.getAccountName());
                newAccount.setPassword(CryptographyUtils.md5(rp.getPwd()));
                newAccount.setPhone(rp.getPhone());
                dao.update(newAccount);
            }
            //添加角色
            sysRoleService.editAccountSysRole(newAccount.getId(),Constants.KONGQUE_CLOUD_PLATFORM_SYSID,rp.getRoleIds());

            return new Result(new JSONObject().element("accountId",newAccount.getId()));

        }
    }

    /**
     * zongt
     * 2019年5月31日10:29:12
     * 后台添加工厂账号。
     *
     * @param paramsMap
     * @return
     */
    @Override
    public Result registerFactory(RegisterFactoryDto rfd) {

        //注册新账户
        Result guResult = getAccountByUsername(rfd.getUsername(),rfd.getSysId());

        if (!Constants.RESULT_CODE.SUCCESS.equals(guResult.getReturnCode())) {
            logger.error("用户名:" + rfd.getUsername());
            return guResult;
        }

        Result gpResult = getAccountByPhone(rfd.getPhone(), rfd.getSysId());

        if (!Constants.RESULT_CODE.SUCCESS.equals(gpResult.getReturnCode())) {
            logger.error("账号已存在:" + rfd.getPhone());
            return gpResult;
        }

        Date date = new Date();

        Account newAccount = new Account();

        newAccount.setCreateTime(date);
        newAccount.setUsername(rfd.getUsername());
        newAccount.setPassword(CryptographyUtils.md5(rfd.getPassword()));
        newAccount.setSysId(Constants.KONGQUE_CLOUD_PLATFORM_SYSID);
        newAccount.setInvitationCode(StringUtils.getUUCode(8));
        newAccount.setPhone(rfd.getPhone());
        newAccount.setSource("0");
        newAccount.setStatus("1");
        newAccount.setMessageFlag("2");
        newAccount.setUpdateTime(date);
        newAccount.setLastLoginTime(date);

        dao.save(newAccount);

        sysRelate(newAccount, new String[]{Constants.KONGQUE_CLOUD_PLATFORM_SYSID});

        return new Result(new JSONObject().element("accountId",newAccount.getId()));
    }

    /**
     * 根据用户名和系统id查询用户
     * zongt
     * 2019年6月5日09:35:12
     *
     * @param username
     * @param sysId
     * @return
     */
    @Override
    public Result getAccountByUsername(String username, String sysId) {

        Criteria criteria = dao.createCriteria(Account.class);

        criteria.add(Restrictions.eq("username", username));
        criteria.add(Restrictions.eq("sysId", sysId));

        Account ac = (Account) criteria.uniqueResult();

        if (ac != null) return new Result(Constants.RESULT_CODE.ACCOUNT_ALREATY_EXIST, "用户名已存在",ac);

        return new Result();
    }

    /**
     * 根据用户名和系统id查询用户
     * zongt
     * 2019年6月5日09:35:12
     *
     * @param username
     * @param sysId
     * @return
     */
    @Override
    public Result getAccountByPhone(String phone, String sysId) {

        Criteria criteria = dao.createCriteria(Account.class);

        criteria.add(Restrictions.eq("phone", phone));
        criteria.add(Restrictions.eq("sysId", sysId));

        Account ac = (Account) criteria.uniqueResult();

        if (ac != null) return new Result(Constants.RESULT_CODE.POHONE_ALREATY_EXIST, "手机号已存在",ac);

        return new Result();
    }

    /**
     * 修改新手标识
     * zongt
     * 2019年6月5日09:35:12
     * @param username
     * @param sysId
     * @return
     */
    @Override
    public Result updateAccountForNewFlag(String accountId) {

        Account account = dao.findById(Account.class, accountId);

        account.setNewFlag("1");

        dao.update(account);

        return new Result();
    }


    /**
     * 修改企业完善信息标识
     * zongt
     * 2019年6月12日09:17:45
     * @param accountId
     * @return
     */
    @Override
    public Result updateAccountForMsgFlag(String accountId) {

        Account account = dao.findById(Account.class, accountId);

        account.setMessageFlag("1");

        dao.update(account);

        return new Result();
    }



    /**
     * 校验管理员
     * @param rp
     * @return
     */
    @Override
    public Result checkAdmin(RegisterPersonDto rp) {

        Account byId = dao.findById(Account.class, rp.getAccountId());

        return CryptographyUtils.md5(rp.getPwd()).equals(byId.getPassword()) ? new Result() : new Result(Constants.RESULT_CODE.ACCOUNT_EXCEPTION,"你输入的密码不正确");
    }



    /**
     * 修改密码
     * zongt
     * 2019年5月31日15:52:48
     */
    @Override
    public Result updatePwd(RegisterPersonDto rp) {

        Account byId = dao.findById(Account.class, rp.getAccountId());

        byId.setPassword(CryptographyUtils.md5(rp.getPwd()));

        dao.update(byId);

        return new Result();
    }

	@Override
	public Result getAccountListByPhone(PageBean page, String phone,String sysId) {
		Criteria criteria = dao.createCriteria(Account.class);
		criteria.add(Restrictions.ilike("phone", phone, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("status", "1"));
		criteria.add(Restrictions.eq("sysId", sysId));
		List<Account> findListWithPagebeanCriteria = dao.findListWithPagebeanCriteria(criteria, page);
		return new  Result(findListWithPagebeanCriteria, dao.findTotalWithCriteria(criteria));
	}

	@Override
	public Result bindPhone(SmsDto dto) {
        // 校验新手机号码的验证码
        SmsDto smsCheckDto = new SmsDto();
        smsCheckDto.setPhone(dto.getNewPhone());
        smsCheckDto.setNumber(dto.getNewNumber());
        Result rs2 = smsSendService.smsIstrue(smsCheckDto);
        if (!rs2.getReturnCode().equals("200")) {
            logger.error("手机验证码错误：[" + dto.getNewPhone() + ":" + dto.getNewNumber() + "]");
            return new Result(Constants.SMSCODE.NEW_PHONE_CODE_ERROR, rs2.getReturnMsg());
        }
        //校验重复
        Criteria criteria = dao.createCriteria(Account.class);
        criteria.add(Restrictions.eq("phone", dto.getNewPhone()));
        criteria.add(Restrictions.ne("status", "3"));
        criteria.add(Restrictions.eq("sysId", Constants.YIPINYOUDIAO_SYSID));
        Account account = (Account) criteria.uniqueResult();
        if(account !=null) {
        	return new Result<>(Constants.SMSCODE.PHONE_BIND_ERROR, "绑定失败,该手机号已被绑定过");
        }
        
        //修改账户中的手机号码
        Account loginedAccount = dao.findById(Account.class, dto.getAccountId());
        if (loginedAccount != null) {
            if (StringUtils.isNotBlank(dto.getNewPhone())) {
                loginedAccount.setPhone(dto.getNewPhone());
                dao.update(loginedAccount);
            }
        }
        return new Result(Constants.RESULT_CODE.SUCCESS, "手机修改成功！");
	}


}
