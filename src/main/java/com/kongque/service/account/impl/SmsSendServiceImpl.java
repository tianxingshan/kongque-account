package com.kongque.service.account.impl;

import com.alibaba.fastjson.JSON;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.codingapi.tx.annotation.TxTransaction;
import com.kongque.constants.Constants;
import com.kongque.dao.IDaoService;
import com.kongque.dto.account.SmsDto;
import com.kongque.entity.Account;
import com.kongque.entity.SmsVerification;
import com.kongque.service.account.ISmsSendService;
import com.kongque.service.liaision.IMemebershipFeignService;
import com.kongque.service.liaision.ITenantFeignService;
import com.kongque.util.*;
import net.sf.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SmsSendServiceImpl implements ISmsSendService {

	private static Logger logger = LoggerFactory.getLogger(SmsSendServiceImpl.class);

	//测试手机号
	private static String[] test_p={"18254261561","18813149633"};

	@Resource
	private IDaoService dao;
	
	@Resource
	private IMemebershipFeignService membershipFeignService;
	
	@Resource
	private ITenantFeignService tenantFeignService;

	@SuppressWarnings("unused")
	@Override
	public Result smsSend(SmsDto smsDto) {

		HashMap<String, Object> result = new HashMap<>();
		// 初始化SDK
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		restAPI.init("app.cloopen.com", "8883");
		// 参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。
		restAPI.setAccount("8a216da85fe1c856016001e5f4ed10bd", "0eafc96be0664097a4d678886d500be3");
		// 应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID
		restAPI.setAppId("8a216da85fe1c856016001e5f54210c4");

		// 生成4位随机验证码
		int a = (int) ((Math.random() * 9 + 1) * 1000);
		String number = String.valueOf(a);
		// 调用短信接口
		//http://doc.yuntongxun.com/p/5a533de33b8496dd00dce07c

		//测试短信
		if((!Constants.SYSCONSTANTS.IS_PRODUCTION)){//测试环境不发短信
			result.put("statusCode","000000");
			HashMap<String, Object> data=new HashMap<>();
			data.put("rs","OK");
			number = "000000";
			result.put("data",data);
			//result = restAPI.sendTemplateSMS(smsDto.getPhone(), "1", new String[] { number, "1分钟" });
		}else{
			result = restAPI.sendTemplateSMS(smsDto.getPhone(), "223397", new String[] { number, "1分钟" });
		}

		if ("000000".equals(result.get("statusCode"))) {
			// 发送成功存入验证表中
			SmsVerification smsVerification = new SmsVerification();
			smsVerification.setPhone(smsDto.getPhone());
			smsVerification.setVerification(number);
			smsVerification.setCreateTime(new Date());
			dao.save(smsVerification);
			// 正常返回输出data包体信息（map）
			@SuppressWarnings("unchecked")
			HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				Object object = data.get(key);
			}
			return new Result(result);
		} else {
			logger.error("验证码发送失败,手机号:"+smsDto.getPhone()+"错误信息："+ JsonUtil.objToJson(result));
			// 异常返回输出错误码和错误信息
			return new Result(Constants.SMSCODE.ERROR_CODE,"短信发送失败。", result.get("statusMsg"));
		}
	}

	@Override
	public Result smsIstrue(SmsDto smsDto) {
		Result result = new Result();
		Long expires = smsDto.getExpires() == null ? SmsDto.DEFUAULT_EXPIRES : smsDto.getExpires();//获取本次验证码校验应该使用的验证码有效期
		Criteria criteria = dao.createCriteria(SmsVerification.class);
		criteria.add(Restrictions.eq("phone", smsDto.getPhone()));
		criteria.addOrder(Order.desc("createTime"));
		criteria.setMaxResults(1);
		List<SmsVerification> smsList = criteria.list();
		SmsVerification smsVerification = smsList.get(0);		
		Long duration = new Date().getTime() - smsVerification.getCreateTime().getTime();//计算当前验证码的有效持续时间
		if (smsVerification.getPhone().equals(smsDto.getPhone()) && smsVerification.getVerification().equals(smsDto.getNumber()) && duration < expires) {
			if(smsDto.isForcedExpiration()){
				smsVerification.setVerification(smsVerification.getVerification()+"[expired]");
			}
			result .setReturnMsg("验证成功！");
		} 
		else if(duration >= expires && smsVerification.getVerification().indexOf("[expired]") < 0){
			smsVerification.setVerification(smsVerification.getVerification()+"[expired]");
			dao.update(smsVerification);
			result.setReturnCode(Constants.SMSCODE.PHONE_CODE_EXPIRED);
			result.setReturnMsg("验证码已过期！");
			logger.error("以下验证码已过期:"+smsVerification.getVerification());
		}
		else{
			result.setReturnCode(Constants.SMSCODE.OLD_PHONE_CODE_ERROR);
			result.setReturnMsg("验证码错误！");
			logger.error("以下验证码或手机号错误：["+smsDto.getPhone()+":"+smsDto.getNumber()+"]");
		}
		return result;
	}

	@Override
	public Result editPassword(SmsDto smsDto) {
		Account account = dao.findById(Account.class, smsDto.getAccountId());
		account.setPassword(CryptographyUtils.md5(smsDto.getNewPassWord()));
		dao.update(account);
		return new Result(Constants.RESULT_CODE.SUCCESS, "密码修改成功！");
	}

	@Override
	@TxTransaction(isStart=true)
	public Result editPhone(SmsDto smsDto) {
		if(StringUtils.isBlank(smsDto.getPhone()) && StringUtils.isNotBlank(smsDto.getNewPhone())) smsDto.setPhone(smsDto.getNewPhone());
		if(StringUtils.isBlank(smsDto.getNumber()) && StringUtils.isNotBlank(smsDto.getNewNumber())) smsDto.setNumber(smsDto.getNewNumber());
		Result checkResult = smsIstrue(smsDto);
		if(!"200".equals(checkResult.getReturnCode())){
			return checkResult;
		}
		Criteria criteria = dao.createCriteria(Account.class);
		criteria.add(Restrictions.eq("phone", smsDto.getNewPhone()));
		criteria.add(Restrictions.ne("status", "3"));
		String currentSysId = StringUtils.isBlank(smsDto.getSysId()) ? Constants.KONGQUE_CLOUD_PLATFORM_SYSID : smsDto.getSysId();
		criteria.add(Restrictions.eq("sysId", currentSysId));
		if(criteria.uniqueResult() != null){
			logger.error("账号异常:手机号[" + smsDto.getNewPhone() + "]在当前业务系统["+currentSysId+"]中已经存在");
			return new Result(Constants.RESULT_CODE.POHONE_ALREATY_EXIST, "该手机号码已经被占用，请换一个号码再试！");
		}		
		Account account = dao.findById(Account.class, SysUtil.getAccountId());
		if (account != null) {
			if (StringUtils.isNotBlank(smsDto.getNewPhone())) {
				account.setPhone(smsDto.getNewPhone());
			}
		}
//		Map<String, String> map = new HashMap<>();
//		map.put("kongqueAccountId", smsDto.getAccountId());
//		// 根据账号id获取会员详情
//		String ss = HttpClientUtils.doGet(Constants.API.KONGQUE_MEMBERSHIP_DETAIL, map);
		Result ss = membershipFeignService.getMembershipInfo(smsDto.getAccountId());
		JSONObject json = JSONObject.fromObject(ss);
		String code = json.get("returnCode").toString();
		if (Constants.RESULT_CODE.SUCCESS.equals(code)) {
			String de = json.get("returnData").toString();
			JSONObject js = JSONObject.fromObject(de);
			String id = js.get("id").toString();
			String fsd = js.get("detail").toString();
			// 使用fastjson来处理json里面的值
			com.alibaba.fastjson.JSONObject json1 = JSON.parseObject(fsd);
			json1.put("phone", smsDto.getNewPhone());
			// 根据详情id修改会员详情信息
			Map<String, Object> mapp = new HashMap<>();
			mapp.put("id", id);
			mapp.put("detail", json1);
//			String sa = HttpClientUtils.doPostJson(Constants.API.KONGQUE_MEMBERSHIP_DETAIL_EDIT,
//					JsonUtil.objToJson(mapp).toString());
			Result sa = membershipFeignService.editMembershipDetail(mapp);
			JSONObject jsons = JSONObject.fromObject(sa);
			String codes = jsons.get("returnCode").toString();
			if (Constants.RESULT_CODE.SUCCESS.equals(codes)) {
				return new Result(Constants.RESULT_CODE.SUCCESS, "手机修改成功！");
			} else {
				Result result = new Result();
				result.setReturnCode(jsons.get("returnCode").toString());
				result.setReturnMsg(jsons.get("returnMsg").toString());
				return result;
			}
		} else {
			Result result = new Result();
			result.setReturnCode(json.get("returnCode").toString());
			result.setReturnMsg(json.get("returnMsg").toString());
			return result;
		}
	}

	@Override
	public Result accountOnly(SmsDto smsDto) {
		if (findByUsername(smsDto.getAccountName()) != null) {
			return new Result(Constants.RESULT_CODE.ACCOUNT_ALREATY_EXIST, "账号已存在，请更换其他账号");
		}
		Criteria cri=dao.createCriteria(Account.class);
		cri.add(Restrictions.eq("phone",smsDto.getPhone()));
		cri.add(Restrictions.eq("sysId",smsDto.getSysId()));
		List<Account> accountList=(List<Account>)cri.list();
		if (accountList!=null&&accountList.size()>0) {
			return new Result(Constants.RESULT_CODE.ACCOUNT_ALREATY_EXIST, "该手机号已注册，请更换其他手机号");
		}
		Criteria criteria = dao.createCriteria(SmsVerification.class);
		criteria.add(Restrictions.eq("phone", smsDto.getPhone()));
		criteria.addOrder(Order.desc("createTime"));
		criteria.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<SmsVerification> smsList = criteria.list();
		String phone = smsList.get(0).getPhone();
		String number = smsList.get(0).getVerification();
		if (phone.equals(smsDto.getPhone()) && number.equals(smsDto.getNumber())) {
			return new Result(Constants.RESULT_CODE.SUCCESS, "验证成功！");
		} else {
			return new Result(Constants.SMSCODE.OLD_PHONE_CODE_ERROR, "验证码错误！");
		}
	}

	public Account findByUsername(String username) {
		Account account = dao.findUniqueByProperty(Account.class, "username", username);
		if (account != null) {
			Hibernate.initialize(account.getSysList());
			Hibernate.initialize(account.getRoleSet());
			dao.evict(account);
		}
		return account;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result editAppPassword(SmsDto smsDto) {
		// 验证再次校验
		Result rs = smsIstrue(smsDto);
		if (!rs.getReturnCode().equals("200")) {
			return new Result(Constants.RESULT_CODE.TIMED_OUT, "操作超时");
		}
		// 未删除的账号
		Criteria cri = dao.createCriteria(Account.class);
		cri.add(Restrictions.eq("phone", smsDto.getPhone()));
		cri.add(Restrictions.ne("status", "3"));
		List<Account> accounts = cri.list();
		if (accounts.size() > 0) {
			Account account =null;
			if(accounts.size()==1){
				account=accounts.get(0);
			}else if(StringUtils.isBlank(smsDto.getSysId())){
				return new Result(Constants.RESULT_CODE.ACCOUNT_MULTIPLE_EXISTENCE,"手机号多个账号");
			}else {
				for(Account a:accounts ){
					if(a.getSysId().equals(smsDto.getSysId())){
						account=a;
						break;
					}
				}
			}
			if(account==null){
				return new Result(Constants.RESULT_CODE.ACCOUNT_NOT_EXIST, "账号未注册");
			}
			
			if (!account.getStatus().equals("1")) {
				return new Result(Constants.RESULT_CODE.ACCOUNT_EXCEPTION, "账户状态异常请联系管理员");
			}
			account.setPassword(CryptographyUtils.md5(smsDto.getNewPassWord()));
			dao.update(account);
		} else {
			logger.error("无此手机号信息:"+smsDto.getPhone());
			return new Result(Constants.SMSCODE.NO_PHONE_ERROR, "无此手机号信息");
		}
		return new Result(Constants.RESULT_CODE.SUCCESS, "密码修改成功！");
	}

}
