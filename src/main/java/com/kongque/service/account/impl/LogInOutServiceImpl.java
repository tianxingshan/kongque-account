/**
* @author pengcheng
* @since 2017年10月18日
 */
package com.kongque.service.account.impl;

import com.kongque.component.IRedisClient;
import com.kongque.component.impl.CustomTaskScheduler;
import com.kongque.component.impl.JsonMapper;
import com.kongque.constants.Constants;
import com.kongque.dao.IDaoService;
import com.kongque.dto.account.AccountLoginDto;
import com.kongque.dto.account.LoginVerifyDto;
import com.kongque.dto.account.SmsDto;
import com.kongque.entity.Account;
import com.kongque.entity.AccountSys;
import com.kongque.model.ModelForLogin;
import com.kongque.service.account.IAccountService;
import com.kongque.service.account.ILogInOutService;
import com.kongque.service.account.ISmsSendService;
import com.kongque.service.oauth.OauthService;
import com.kongque.service.sysResource.ISysResourceService;
import com.kongque.service.sysRole.ISysRoleService;
import com.kongque.util.JsonUtil;
import com.kongque.util.Result;
import com.kongque.util.StringUtils;
import com.kongque.util.SysUtil;
import net.sf.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 账户系统登录管理模块业务层功能接口方法实现类
 * 
 * @author pengcheng
 * @since 2017年10月18日
 */
@Service
public class LogInOutServiceImpl implements ILogInOutService {

	@Resource
	private IRedisClient redisClient;
	
	@Resource
	private IDaoService dao;
	
	@Resource
	private IAccountService accountService;
	
	@Resource
	private OauthService oauthService;
	
	@Resource 
	private ISysRoleService roleService;
	
	@Resource
	private ISmsSendService smsSendService;

	@Resource
	private ISysResourceService sysResourceService;

	@Resource
    private CustomTaskScheduler scheduler;

	private static Logger logger=LoggerFactory.getLogger(LogInOutServiceImpl.class);

	/* 
	 * @see com.kongque.service.ILogInOutService#logIn(com.kongque.dto.DtoForLogin)
	 */
	@Override
	public Result logIn(AccountLoginDto loginDto) {
		Result result = null;
		if(loginDto.getExpire() == null){//如果登录有效期参数值为空
			loginDto.setExpire(Constants.SYSCONSTANTS.TOKEN_TIMEOUT);//从配置文件中获取defaultExpire属性的属性值来设置本次登录的有效期。
		}
		Account uniqueAccount = null;
		String token = SysUtil.getToken();
		//登录参数校验,验证码、token、openId、密码
		if(StringUtils.isBlank(loginDto.getCaptcha())&&StringUtils.isBlank(token)&&StringUtils.isBlank(loginDto.getOpenid())&&StringUtils.isBlank(loginDto.getPassword())){
			logger.error("登录参数缺失："+JsonMapper.toJson(loginDto));
			return new Result(Constants.RESULT_CODE.NOT_PARAMERT, "参数缺失");
		}
		
		//短信登录验证
		if(StringUtils.isNotBlank(loginDto.getCaptcha())){
			SmsDto smsDto = new SmsDto();
			smsDto.setPhone(loginDto.getUsername());
			smsDto.setNumber(loginDto.getCaptcha());
			result = smsSendService.smsIstrue(smsDto);
			if(!Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())){
				return result;
			}
		}
		//根据参数的不同通过两种方式获取系统账户信息
		//token登录验证
		if(StringUtils.isNotBlank(token)){//如果请求中带有token
			uniqueAccount = accountService.findByToken(token);//根据token获取对应的账户信息
			if(uniqueAccount == null){
				return new Result(Constants.RESULT_CODE.ACCOUNT_RELOGIN,"当前登录已过期，请重新登录");
			}
		}
		//获取账号
		uniqueAccount = accountService.findByLoginName(loginDto.getUsername(), loginDto.getRegisterSource());//根据用户名和注册业务来源获取对应的账户信息
		result = checkAccountExist(uniqueAccount,loginDto.getUsername(), loginDto.getRegisterSource(),loginDto.getLoginSource());//检验与当前登录用户名对应的账户信息在数据库中是否存在。
		if(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST.equals(result.getReturnCode())){//如果检验没有通过
			return result;
		}	
		//密码校验
		result = checkPassword(uniqueAccount,loginDto);//检验登录用户的登录密码是否正确
		if(!Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())){//如果检验没有通过
			return result;
		}
		//openId校验
		if(!Constants.RESULT_CODE.SUCCESS.equals(checkOpenId(uniqueAccount,loginDto).getReturnCode())){//如果检验没有通过
			return result;
		}
		//注释系统关联校验
//			result = checkAccountSys(uniqueAccount, loginDto);//检验登录用户的账户是否已经与要登录的系统建立关联
//			if(Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT.equals(result.getReturnCode())){//如果检验没有通过
//				return result;
//			}			
		
		result = checkAccountStatus(uniqueAccount);//检验登录用户的账户信息状态是否正常
		if(Constants.RESULT_CODE.ACCOUNT_EXCEPTION.equals(result.getReturnCode())){//如果检验没有通过
			return result;
		}
		
		//更新通过校验的登录用户账户信息中的相关信息，并把与登录相关的信息保存到redis中
		uniqueAccount.setLastLoginTime(new Date());//修改当前登录用户账户信息中的最新登录时间
		uniqueAccount.setSessionKey(loginDto.getSessionKey());//保存当前登录用户登录微信平台后所含session_key参数信息
		ModelForLogin loginModel = saveLoginInfo(uniqueAccount, loginDto.getExpire());//把登录用户账户信息中与登录管理相关的信息保存到Redis中。
		if(loginModel == null){//如果登录用户账户信息的登录管理数据在Redis中保存失败
			result.setReturnCode(Constants.REDIS_OPERATION_ERROR.SET_ERROR);
			result.setReturnMsg("登录信息处理系统故障，登入失败，请联系管理员。");
			logger.info("前端用户[登录账号："+uniqueAccount.getUsername()+"]登入系统失败：Redis系统保存登录信息失败。[错误码："+Constants.REDIS_OPERATION_ERROR.SET_ERROR+"]");//把本次操作计入系统日志
			return result;
		}		
		uniqueAccount.setToken(loginModel.getToken());//修改当前登录用户账户信息中的登录token		
		//当被登录账号中用户的unionid属性不存在，且登录信息中包含用户登录微信平台所获的unionid信息时
		if(StringUtils.isBlank(uniqueAccount.getUnionid()) && StringUtils.isNotBlank(loginDto.getUnionid())){
			uniqueAccount.setUnionid(loginDto.getUnionid());
		}
		if(!accountService.updateAccount(uniqueAccount)){//如果数据库中登录用户账户信息的登录管理数据更新失败
			result.setReturnCode(Constants.RESULT_CODE.DB_UPDATE_ERROR);
			result.setReturnMsg("登录信息处理系统故障，登入失败，请联系管理员。");
			logger.info("前端用户[登录账号："+uniqueAccount.getUsername()+"]登入系统失败：数据库账户登录相关信息更新失败。[错误码："+Constants.RESULT_CODE.DB_UPDATE_ERROR+"]");//把本次操作计入系统日志
			return result;
		}
		result.setReturnData(loginModel);//把登录用户账户信息的登录数据信息设置到对前端请求的响应数据中。
		logger.info("前端用户[登录账号："+uniqueAccount.getUsername()+"]登入系统执行成功。");//把本次操作计入系统日志		
		return result;
	}

	@Override
	public Result loginForPermission(AccountLoginDto dto) {
		//登录
		Result loginRs=logIn(dto);
		if(loginRs.getReturnCode().equals(Constants.RESULT_CODE.SUCCESS)){
			JSONObject  rs=JSONObject.fromObject(loginRs.getReturnData());
			//获取权限
			Result menuRs=sysResourceService.getSysResourceMenuByToken(rs.getString("token"),dto.getSysId());
			if(menuRs.getReturnCode().equals(Constants.RESULT_CODE.SUCCESS)){
				rs.put("accountPermissions",JsonUtil.arrayToJson2(menuRs.getReturnData(),new String[]{"urlMatch"}));
				return new Result(rs);
			}else{
				return menuRs;
			}
		}else {
			return loginRs;
		}
	}



	/**
	 * 校验OpenId
	 * @author yuehui 
	 * @date: 2018年9月3日下午5:24:17
	 */
	private Result checkOpenId(Account account,AccountLoginDto loginDto){
		Result result=new Result();
		if(StringUtils.isNotBlank(loginDto.getOpenid()) ){
			if(!account.getOpenid().equals(loginDto.getOpenid())){
				result.setReturnCode(Constants.RESULT_CODE.OPENID_ERROR);
				result.setReturnMsg("登录孔雀云平台失败：用户openid有误。");
				logger.info("前端用户[登录账号："+JsonMapper.toJson(loginDto)+"]登入系统失败：openid错误。[错误码："+Constants.RESULT_CODE.OPENID_ERROR+"]");//把本次操作计入系统日志
			}
		}
		return result;
	}
	
	/* 
	 * @see com.kongque.service.ILogInOutService#logOut(java.lang.String)
	 */
	@Override
	public Result logOut(String token) {
		Result result = new Result();
		Account account = null;
		ModelForLogin loginModel = fetchLoginInfo(token);//尝试从Redis中获取要登出用户的登录信息数据		
		if(loginModel != null){//如果该用户的登录信息数据在Redis中对应的token没有过期
			redisClient.remove(loginModel.getToken());//删除Redis中保存的该登出用户的登录信息数据
		}
		account = accountService.findByToken(token);//根据前端提交的token参数从数据库中查出登出用户的账户信息
		if(account != null){//如果登出用户的账户信息在数据库中存在
			account.setLastLogoutTime(new Date());//设置账户信息中的最新登出时间
			account.setToken(null);//把该账户信息中保存的当前登录数据对应的token置空
			dao.update(account);
		}		
		return result;
	}

	/* 
	 * @see com.kongque.service.ILogInOutService#verify(java.lang.String)
	 */
	@Override
	public Result verify(LoginVerifyDto dto) {
		Result result = new Result();
		result.setReturnCode(checkToken(dto.getToken(),dto.getSysId()));
		switch (result.getReturnCode()) {
		case Constants.RESULT_CODE.NOT_LOG_IN:
			result.setReturnMsg("账户未登录。");
			logger.info("业务系统[sysId:"+dto.getSysId()+"]功能接口调用登录验证结果："+result.getReturnMsg());
			break;
		case Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT:
			result.setReturnMsg("账户与系统未建立关联。");
			logger.info("业务系统[sysId:"+dto.getSysId()+"]功能接口调用登录验证结果："+result.getReturnMsg());
			break;
		default:result.setReturnMsg("账户登录验证通过");
		}		
		return result;
	}
	
	/* 
	 * @see com.kongque.service.ILogInOutService#getLoginInfo(java.lang.String)
	 */
	@Override
	public Result getLoginInfo(String token) {
		Result result = new Result();
		ModelForLogin model = fetchLoginInfo(token);
		if(model == null){
			result.setReturnCode(Constants.RESULT_CODE.NOT_LOG_IN);
			result.setReturnMsg("登录超时，请重新登录。");
			logger.info("登录凭证["+token+"]对应的登录信息失效，本次登录信息获取失败。");
		}
		else{
			result.setReturnData(model);
		}
		return result;
	}

	/* 
	 * @see com.kongque.service.ILogInOutService#checkToken(java.lang.String,java.lang.String)
	 */
	@Override
	public String checkToken(String token,String sysId){
		ModelForLogin loginModel  = fetchLoginInfo(token);
		if(loginModel == null){
			return Constants.RESULT_CODE.NOT_LOG_IN;
		}
		if(!"1".equals(loginModel.getStatus())){
			return Constants.RESULT_CODE.ACCOUNT_EXCEPTION;
		}
//取消对系统关联的校验
//		if(!StringUtils.isBlank(sysId) && !loginModel.getSysSet().contains(sysId)){
//			 return Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT;
//		}
		return Constants.RESULT_CODE.SUCCESS;
	}
	
	/*
	 * @see com.kongque.service.ILogInOutService#getAccountInfo(java.lang.String)
	 */
	@Override
	public ModelForLogin fetchLoginInfo(String token) {
		
		if(StringUtils.isBlank(token)){
			logger.error("token null");
			return null;
		}
		
		String loginInfo = redisClient.get(token);

		if(StringUtils.isBlank(loginInfo)){
			Account account = accountService.findByToken(token);
			if(account != null){
				account.setToken(null);
				accountService.updateAccount(account);
			}
			logger.error("保存在Redis服务器中的账户登录数据信息[key:"+token+"]失效！");
			return null;
		}	
		//oauth 验证
		try{
			/*Map<String, ?>rsmap=oauthService.checkToken(token, null);
			if(rsmap==null||rsmap.containsKey("error")){
				logger.error("oauth 验证,保存在Redis服务器中的账户登录数据信息失效！");
				Account account = accountService.findByToken(token);
				if(account != null){
					account.setToken(null);
					accountService.updateAccount(account);
				}
				return null;
			}*/
		}catch(Exception e){
			return null;
		}
		return (ModelForLogin)JSONObject.toBean(JSONObject.fromObject(loginInfo), ModelForLogin.class);
	}

	private String getToken(String username,String password,String sys){
		Map<String, String> m=new HashMap<String, String>();
		m.put("username", username+":"+sys);
		m.put("password", password);
		OAuth2AccessToken token=oauthService.accessToken(m);
		return token.getValue();
	}
	
	/* 
	 * @see com.kongque.service.ILogInOutService#saveLoginInfo(com.kongque.entity.Account)
	 */
	@Override
	public ModelForLogin saveLoginInfo(Account account, Integer expire) {
		ModelForLogin model = null;
		//如果数据库中账户信息是未登录状态或着是已登录状态但Redis中保存的登录信息已经过期失效，则重新执行创建并保存登录信息逻辑
		if(account.getToken() == null || (model = fetchLoginInfo(account.getToken())) == null){
			//CryptographyUtils.generateNonceStr();改为使用spring security access_token
			
			String token = getToken(account.getUsername(), account.getPassword(),account.getSysId());
			//旧token 处理
			Account dbaccount=accountService.findByToken(token);
			if(dbaccount!=null){
				dbaccount.setToken(null);
				dao.update(dbaccount);
			}
			model = new ModelForLogin();
			model.setToken(token);
			String name=account.getUsername();
			if("3".equals(account.getSource()) && !account.getUsername().startsWith("ChengYi")){
				name = account.getUsername().substring(0,account.getUsername().length() - 9);
			}
			model.setUserName(name);
			model.setAccountId(account.getId());
			model.setInvitationCode(account.getInvitationCode());
			model.setStatus(account.getStatus());
			model.setSessionKey(account.getSessionKey());
			model.setSysSet(new HashSet<>());
			model.setNewFlag(account.getNewFlag());
			model.setPhone(account.getPhone());
			model.setMsgFlag(account.getMessageFlag());
			List<AccountSys> sysList = account.getSysList();
			Set<String> sysIdSet = model.getSysSet();
			if(sysList != null && !sysList.isEmpty()){
				for(AccountSys sys : sysList){
					sysIdSet.add(sys.getSysId());
				}
			}

          //  scheduler.execute(new DataRunable(JsonUtil.objToJson(model).toString(), new Consumer<String>() {
              //  @Override
              //  public void accept(String data) {
                    if (!"OK".equals(redisClient.set(token, expire,JsonUtil.objToJson(model).toString() ))) {
                        logger.info("用户[username:" + account.getUsername() + "]的账户信息在Redis服务器中保存失败！[错误码：" + Constants.REDIS_OPERATION_ERROR.SET_ERROR + "]");
                    }
              //  }
          //  }));
		}
		else if(StringUtils.isNotBlank(account.getSessionKey()) && !account.getSessionKey().equals(model.getSessionKey())){
			model.setSessionKey(account.getSessionKey());
			model.setMsgFlag(account.getMessageFlag());

           // scheduler.execute(new DataRunable<>(new String[]{account.getId(), JsonUtil.objToJson(model).toString()}, new Consumer<String[]>() {
               // @Override
              //  public void accept(String[] data) {
                    if(!"OK".equals(redisClient.set(account.getId(),expire,JsonUtil.objToJson(model).toString()))){
                        logger.info("用户[username:"+account.getUsername()+"]的登录微信平台所获新session_key在Redis服务器中保存失败！[错误码："+Constants.REDIS_OPERATION_ERROR.SET_ERROR+"]");
                    }
             //   }
          //  }));
		}
		//新登录信息创建完成，并成功保存到Redis服务器中，或者数据库中账户信息是已登录状态，且Redis中保存的登录信息依然有效，则初始化保存在Redis中的登录信息的有效期
		//if(redisClient.expire(model.getToken(), expire) != 1){
		//	logger.info("用户[username:"+account.getUsername()+"]的账户保存在Redis服务器中的登录数据的有效期设置失败！[错误码："+Constants.REDIS_OPERATION_ERROR.EXPIRE_ERROR+"]");
		//	return null;
	//	}
       // scheduler.execute(new DataRunable<>(new String[]{account.getId(), model.getToken()}, new Consumer<String[]>() {
         //   @Override
          //  public void accept(String[] data) {
                //权限redis
                if(!"OK".equals(roleService.redisTokenHandle(account.getId(),model.getToken()))){
                    logger.error("用户[username:"+account.getUsername()+"]的账户权限信息在Redis服务器中保存失败！[错误码："+Constants.REDIS_OPERATION_ERROR.SET_ERROR+"]");
                }
         //   }
      //  }));

        model.setMsgFlag(account.getMessageFlag());
		return model;
	}
	
	/* (non-Javadoc)
	 * @see com.kongque.service.ILogInOutService#synchronizeRedis(java.lang.String, java.lang.String[], int)
	 */
	@Override
	public Boolean synchronizeRedis(ModelForLogin updatingModel, int operationCode) {
		Boolean result = true;
		Account account = accountService.findById(updatingModel.getAccountId());
		if(account.getToken() != null){
			ModelForLogin savedModel = fetchLoginInfo(account.getToken());
			if(savedModel != null){		
				switch(operationCode){
					case ILogInOutService.REDIS_APPEND_SYSIDS: 
						if(savedModel.getSysSet()==null)
							savedModel.setSysSet(new HashSet<String>());
						savedModel.getSysSet().addAll(updatingModel.getSysSet());
						break;
					case ILogInOutService.REDIS_REMOVE_SYSIDS: 
						if(savedModel.getSysSet()!=null)
							savedModel.getSysSet().removeAll(updatingModel.getSysSet());
						break;
					case ILogInOutService.REDIS_UPDATE_ACCOUNT_STATUS:savedModel.setStatus(updatingModel.getStatus());break;
					default:result = false;
				}			
				result = result && "OK".equals(redisClient.set(savedModel.getToken(), JsonUtil.objToJson(savedModel).toString()));
				if(!result){
					logger.info("保存Redis系统中账号[token:"+savedModel.getToken()+"]的登录信息同步失败！[错误码："+Constants.REDIS_OPERATION_ERROR.SYNCHRONIZE_ERROR+"]");
				}
			}
		}				
		return result;
	}

	/* (non-Javadoc)
	 * @see com.kongque.service.ILogInOutService#addAccountSysToRedis(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean addAccountSysToRedis(String token, String[] sysIdList) {
		Boolean result = true;
		ModelForLogin loginModel = fetchLoginInfo(token);
		if(loginModel != null){
			Set<String> sysIdSet = loginModel.getSysSet();
			sysIdSet.addAll(Arrays.asList(sysIdList));
			result = "OK".equals(redisClient.set(token, JsonUtil.objToJson(loginModel).toString()));
			if(!result){
				logger.info("Redis系统中新增的账号[token:"+token+"]和系统["+sysIdList+"]关联关系保存失败！");
			}
		}		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.kongque.service.ILogInOutService#removeAccountSysFromRedis(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean removeAccountSysFromRedis(String token, String[] sysIdList) {
		Boolean result = true;
		ModelForLogin loginModel = fetchLoginInfo(token);
		if(loginModel != null){
			Set<String> sysIdSet = loginModel.getSysSet();
			result = sysIdSet.removeAll(Arrays.asList(sysIdList)) && "OK".equals(redisClient.set(token, JsonUtil.objToJson(loginModel).toString()));
			if(!result){
				logger.info("前端提交的的账号[token:"+token+"]和系统["+sysIdList+"]关联关系在Redis系统中删除失败！");
			}
		}		
		return result;
	}

	@Override
	public Result accountLogInCheckUser(AccountLoginDto loginDto) {
		Result result = null;
		if(loginDto.getExpire() == null){//如果登录有效期参数值为空
			loginDto.setExpire(Constants.SYSCONSTANTS.TOKEN_TIMEOUT);//从配置文件中获取defaultExpire属性的属性值来设置本次登录的有效期。
		}
		Account uniqueAccount = null;
		String token = SysUtil.getToken();
		//登录参数校验,验证码、token、openId、密码
		if(StringUtils.isBlank(loginDto.getCaptcha())&&StringUtils.isBlank(token)&&StringUtils.isBlank(loginDto.getOpenid())&&StringUtils.isBlank(loginDto.getPassword())){
			logger.error("登录参数缺失："+JsonMapper.toJson(loginDto));
			return new Result(Constants.RESULT_CODE.NOT_PARAMERT, "参数缺失");
		}

		//短信登录验证
		if(StringUtils.isNotBlank(loginDto.getCaptcha())){
			SmsDto smsDto = new SmsDto();
			smsDto.setPhone(loginDto.getUsername());
			smsDto.setNumber(loginDto.getCaptcha());
			result = smsSendService.smsIstrue(smsDto);
			if(!Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())){
				return result;
			}
		}
		//根据参数的不同通过两种方式获取系统账户信息
		//token登录验证
		if(StringUtils.isNotBlank(token)){//如果请求中带有token
			uniqueAccount = accountService.findByToken(token);//根据token获取对应的账户信息
			if(uniqueAccount == null){
				return new Result(Constants.RESULT_CODE.ACCOUNT_RELOGIN,"当前登录已过期，请重新登录");
			}
		}
		//获取账号
		uniqueAccount = accountService.findByLoginName(loginDto.getUsername(), loginDto.getRegisterSource());//根据用户名和注册业务来源获取对应的账户信息
		result = checkAccountExist(uniqueAccount,loginDto.getUsername(), loginDto.getRegisterSource(),loginDto.getLoginSource());//检验与当前登录用户名对应的账户信息在数据库中是否存在。
		if(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST.equals(result.getReturnCode())){//如果检验没有通过
			return result;
		}
		//密码校验
		result = checkPassword(uniqueAccount,loginDto);//检验登录用户的登录密码是否正确
		if(!Constants.RESULT_CODE.SUCCESS.equals(result.getReturnCode())){//如果检验没有通过
			return result;
		}
		//系统权限校验
		if (false==checkLoginUser(uniqueAccount,loginDto.getSysId())){
			logger.info("当前用户没有登陆该系统的权限！");
			return new Result(Constants.RESULT_CODE.ACCOUNT_ACCESS_BARRED,"对不起，您没有访问权限，请切换账号试试！");
		}
		//openId校验
		if(!Constants.RESULT_CODE.SUCCESS.equals(checkOpenId(uniqueAccount,loginDto).getReturnCode())){//如果检验没有通过
			return result;
		}
		//注释系统关联校验
//			result = checkAccountSys(uniqueAccount, loginDto);//检验登录用户的账户是否已经与要登录的系统建立关联
//			if(Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT.equals(result.getReturnCode())){//如果检验没有通过
//				return result;
//			}

		result = checkAccountStatus(uniqueAccount);//检验登录用户的账户信息状态是否正常
		if(Constants.RESULT_CODE.ACCOUNT_EXCEPTION.equals(result.getReturnCode())){//如果检验没有通过
			return result;
		}

		//更新通过校验的登录用户账户信息中的相关信息，并把与登录相关的信息保存到redis中
		uniqueAccount.setLastLoginTime(new Date());//修改当前登录用户账户信息中的最新登录时间
		uniqueAccount.setSessionKey(loginDto.getSessionKey());//保存当前登录用户登录微信平台后所含session_key参数信息
		ModelForLogin loginModel = saveLoginInfo(uniqueAccount, loginDto.getExpire());//把登录用户账户信息中与登录管理相关的信息保存到Redis中。
		if(loginModel == null){//如果登录用户账户信息的登录管理数据在Redis中保存失败
			result.setReturnCode(Constants.REDIS_OPERATION_ERROR.SET_ERROR);
			result.setReturnMsg("登录信息处理系统故障，登入失败，请联系管理员。");
			logger.info("前端用户[登录账号："+uniqueAccount.getUsername()+"]登入系统失败：Redis系统保存登录信息失败。[错误码："+Constants.REDIS_OPERATION_ERROR.SET_ERROR+"]");//把本次操作计入系统日志
			return result;
		}
		uniqueAccount.setToken(loginModel.getToken());//修改当前登录用户账户信息中的登录token
		//当被登录账号中用户的unionid属性不存在，且登录信息中包含用户登录微信平台所获的unionid信息时
		if(StringUtils.isBlank(uniqueAccount.getUnionid()) && StringUtils.isNotBlank(loginDto.getUnionid())){
			uniqueAccount.setUnionid(loginDto.getUnionid());
		}
		if(!accountService.updateAccount(uniqueAccount)){//如果数据库中登录用户账户信息的登录管理数据更新失败
			result.setReturnCode(Constants.RESULT_CODE.DB_UPDATE_ERROR);
			result.setReturnMsg("登录信息处理系统故障，登入失败，请联系管理员。");
			logger.info("前端用户[登录账号："+uniqueAccount.getUsername()+"]登入系统失败：数据库账户登录相关信息更新失败。[错误码："+Constants.RESULT_CODE.DB_UPDATE_ERROR+"]");//把本次操作计入系统日志
			return result;
		}
		result.setReturnData(loginModel);//把登录用户账户信息的登录数据信息设置到对前端请求的响应数据中。
		logger.info("前端用户[登录账号："+uniqueAccount.getUsername()+"]登入系统执行成功。");//把本次操作计入系统日志
		return result;
	}

	private Result checkAccountExist(Account account,String userName,String registerSource,String loginSource){
		Result result = new Result();
		if(account == null){			
			result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST);
			result.setReturnMsg("登录失败：您还没有在当前系统中注册账户，请先注册再登录！");
			logger.info("前端用户[登录账号："+userName+"]登入系统失败：登录账号在["+registerSource+"]系统中不存在。[错误码："+Constants.RESULT_CODE.ACCOUNT_NOT_EXIST+"]");//把本次操作计入系统日志
		}else if(Constants.YIPINYOUDIAO_SYSID.equals(registerSource)&&Constants.LOGIN_SOURCE_APP.equals(loginSource)){
			if(StringUtils.isBlank(account.getPhone())){
				logger.error("app登录用户必须有手机号:"+userName+","+registerSource+","+loginSource);
				result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_NO_PHONE);
				result.setReturnMsg("用户无手机号");
			}
		}
		return result;
	}
	
	private Result checkAccountStatus(Account account){
		Result result = new Result();
		if(!"1".equals(account.getStatus())){			
			result.setReturnCode(Constants.RESULT_CODE.ACCOUNT_EXCEPTION);
			result.setReturnMsg("登录失败：您的账号已经被冻结或被删除，请联系管理员！");
			logger.info("前端用户[登录账号："+account.getUsername()+"]登入系统失败：登录账号已经冻结或被删除。[错误码："+Constants.RESULT_CODE.ACCOUNT_EXCEPTION+"]");//把本次操作计入系统日志
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	private Result checkAccountSys(Account account,AccountLoginDto loginDto){
		Result result = new Result();
		List<AccountSys> sysList = account.getSysList();
		if(sysList != null && !sysList.isEmpty()){
			boolean isContained = false;
			for(AccountSys sys : sysList){
				if(sys.getSysId().equals(loginDto.getSysId())){
					isContained = true;
					break;
				}
			}
			if(!isContained){				
				result.setReturnCode(Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT);
				result.setReturnMsg("登录失败：您的账号不允许登录当前业务系统，请联系管理员！");
				logger.info("前端用户[登录账号："+loginDto.getUsername()+"]登入系统失败：登录账号不属于将要登录的业务系统。[错误码："+Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT+"]");//把本次操作计入系统日志
			}
		}
		else{			
			result.setReturnCode(Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT);
			result.setReturnMsg("登录失败：您的账号没有任何业务系统的登录授权，请联系管理员！");
			logger.info("前端用户[登录账号："+loginDto.getUsername()+"]登入系统失败：登录账号没有关联任何业务系统。[错误码："+Constants.RESULT_CODE.SYS_INDEPENDENCE_ACCOUNT+"]");//把本次操作计入系统日志
		}
		return result;
	}
	
	/**
	 * 校验密码
	 * @author yuehui 
	 * @date: 2018年8月2日下午4:39:30
	 */
	private Result checkPassword(Account account,AccountLoginDto loginDto){
		Result result = new Result();
		if(StringUtils.isNotBlank(loginDto.getPassword())&&!account.getPassword().equals(loginDto.getPassword())){
			result.setReturnCode(Constants.RESULT_CODE.PWD_ERROR);
			result.setReturnMsg("登录失败：密码错误。");
			logger.info("前端用户[登录账号："+JsonMapper.toJson(loginDto)+"]登入系统失败：密码错误。[错误码："+Constants.RESULT_CODE.PWD_ERROR+"]");//把本次操作计入系统日志
		}
		return result;
	}

	/**
	 * 用于登录校验用户访问权限
	 * @param account 用户
	 * @param sysId   请求访问的项目
	 * @return
	 */
	private boolean checkLoginUser(Account account,String sysId){

		boolean f=false;
		if (account==null){
			return f;
		}
		Criteria criteria = dao.createCriteria(AccountSys.class);
		criteria.add(Restrictions.eq("accountId",account.getId()));
		List<AccountSys> list=criteria.list();
		List<String> sysIds=new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (AccountSys a:list) {
				sysIds.add(StringUtils.isNotBlank(a.getSysId())?a.getSysId():"");
			}
			f=sysIds.contains(sysId);
			return f;
		}
		return f;
	}


}
