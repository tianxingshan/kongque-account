package com.kongque.constants;

import java.io.File;

import com.kongque.util.PropertiesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class Constants {
	
	public static  PropertiesUtils propertiesUtils=new PropertiesUtils(File.separator+"bootstrap.properties");
	
	/**
	 * 当前系统标识id
	 * */
	public static final String KONGQUE_CLOUD_PLATFORM_SYSID = "kongque-cloud-platform";
	/**
	 * 衣品有调系统标识id
	 */
	public static final String YIPINYOUDIAO_SYSID = "yipinyoudiao-applet";
	
	/**
	 * app 登录
	 */
	public static final String LOGIN_SOURCE_APP="app";

	/**
	 * 微信小程序登录
	 */
	public static final String LOGIN_SOURCE_MINI="mini";
	/**
	 * pc端登陆
	 */
	public static final String LOGIN_SOURCE_PC="pc";
	
	/**
	 * 
	 * @author yuehui
	 *
	 * @date: 2018年9月19日 下午2:08:44
	 */
	public static class SYS{
		
		/**
		 * app注册
		 */
		public static  final String REGISTER_SOURCE_APP="31";
		/**
		 * 小程序注册
		 */
		public static  final String REGISTER_SOURCE_WX="3";
		
	}
	/**
	 * 错误类型
	 */
	public static class ERROR_TYPE {
		// redis错误
		public static final String REDIS_ERROR = "REDIS_ERROR";

	}

	/**
	 * 接口执行结果状态码
	 */
	public static class RESULT_CODE {
		/**
		 *  成功
		 */
		public static final String SUCCESS = "200";
		
		/**
		 *  失败
		 */
		public static final String SYS_ERROR = "500";
		
		/**
		 * 请求中没有登录凭证参数token
		 */
		public static final String UN_AUTHORIZED = "403";
		
		/**
		 * 参数缺失
		 */
		public static final String NOT_PARAMERT="406";
		
		/**
		 * 操作超时
		 */
		public static final String  TIMED_OUT="409";
		
		/**
		 * 系统中无此账号
		 */
		public static final String ACCOUNT_NOT_EXIST = "010001";
		/**
		 * 登录密码错误
		 */
		public static final String PWD_ERROR = "010002";
		/**
		 * 账户状态异常
		 */
		public static  final String ACCOUNT_EXCEPTION = "010003";
		/**
		 * 用户名在系统中已经存在
		 */
		public static final String ACCOUNT_ALREATY_EXIST = "010004";
		/**
		 * 系统和账号未关联
		 */
		public static final String SYS_INDEPENDENCE_ACCOUNT = "010005";		
		/**
		 * 账户登录信息更新数据库失败
		 */
		public static final String DB_UPDATE_ERROR = "010006";
		/**
		 * 账户未登录
		 */
		public static final String NOT_LOG_IN = "010007";
		/**
		 * 账号和系统关联创建失败
		 */
		public static final String ASSOCIATION_ESTABLISHMENT_FAILURE = "010008";
		
		/**
		 * 账号和系统关联关系删除失败
		 */
		public static final String ASSOCIATION_DELETE_FAILURE = "010009";
		
		/**
		 * 账号权限不足
		 */
		public static final String NOT_PERMISSION = "010010";
		
		/**
		 * 手机号在系统中已存在
		 */
		public static final String POHONE_ALREATY_EXIST = "010011";
		/**
		 * openid错误
		 */
		public static final String OPENID_ERROR= "010012";
		
		/**
		 * 账户绑定失败
		 */
		public static final String ACCOUNT_BINDING_FAILURE = "010013";
		/**
		 * 没有相关的邀请关系
		 */
		public static final String INVITATION_NOT_EXIST="010014";
		
		/**
		 * 具有相同手机号或用户名的多个账户存在
		 */
		public static final String ACCOUNT_MULTIPLE_EXISTENCE="010015";
		
		/**
		 * 当前账户需重新登录系统
		 */
		public static final String ACCOUNT_RELOGIN = "010016";
		
		/**
		 * 账号未绑定手机号
		 */
		public static final String ACCOUNT_NO_PHONE="010017";

		/**
		 * 账号不能访问该系统
		 */
		public static final String ACCOUNT_ACCESS_BARRED="010018";
	}
	
	/**
	 * 短信验证错误码
	 * 0102***
	 * @author yuehui
	 *
	 * @date: 2018年3月29日 下午1:36:54
	 */
	public static class SMSCODE{
		
		/**
		 * 系统没有该手机号
		 */
		public static final String NO_PHONE_ERROR="010203";
		
		/**
		 * 原手机验证码错误
		 */
		public static final String OLD_PHONE_CODE_ERROR="010204";
		
		/**
		 * 新手机验证码错误
		 */
		public static final String NEW_PHONE_CODE_ERROR="010205";
		
		/**
		 * 手机验证码过期
		 */
		public static final String PHONE_CODE_EXPIRED="010206";
		
		/**
		 * 手机号绑定失败
		 */
		public static final String PHONE_BIND_ERROR="010207";
		/**
		 * 手机验证码错误
		 */
		public static final String ERROR_CODE="999";
	}
	
	public static class REDIS_OPERATION_ERROR{
		/**
		 * Redis系统设置信息失败
		 */
		public static final String SET_ERROR = "010101";
		
		/**
		 * Redis的token有效期设置失败
		 */
		public static final String EXPIRE_ERROR = "010102";
		
		/**
		 * 账户和系统关联关系的变化在Redis中同步失败
		 */
		public static final String SYNCHRONIZE_ERROR = "010103";
		
		public static final String CONNECTION_ERROR = "010104";
	}
	
	/**
	 * 
	 * @author yuehui
	 *
	 * @2017年12月12日
	 */
	@Component
	public static class SYSCONSTANTS{
		
		/**
		 * token有效时间 单位s=propertiesUtils.getInt("defaultExpire")
		 */
		public static int TOKEN_TIMEOUT;
		@Value("${defaultExpire}")
		public void setTOKEN_TIMEOUT(String TOKEN_TIMEOUT) {
			this.TOKEN_TIMEOUT =Integer.parseInt(TOKEN_TIMEOUT);
		}

		
		/**
		 * 开发运行环境
		 */
		public static boolean IS_DEV = ("dev").equals(propertiesUtils.getString("env"));

		/**
		 * 测试运行环境
		 */
		public static boolean IS_TEST = ("test").equals(propertiesUtils.getString("env"));

		/**
		 * 正式运行环境
		 */
		public static boolean IS_PRODUCTION = ("production").equals(propertiesUtils.getString("env"));
		
	}
	
	/**
	 * redis 中 key前缀
	 * @author yuehui
	 *
	 * @2017年12月12日
	 */
	public static class REDIS_HASH_KEY{
		
		/**
		 * 权限key_token：value
		 */
		public static final String SECURITY_KEY="token_security_";
		
		/**
		 * 所有权限key
		 */
		public static final String SECURITY_ALL_KEY="security_all";
		
	}
	
	/**
	 * 部门管理
	 * */
	public static class DEPARTMENT {
		//部门已存在
		public static final String DEPARTMENT_ALREADY_EXIST = "010301";
		//部门保存失败
		public static final String DEPARTMENT_SAVE_ERROR = "010302";
		//缺少传入参数
		public static final String DEPARTMENT_LACK_PARAMS = "010303";
		//部门不存在
		public static final String DEPARTMENT_NOT_EXIST = "010304";
		//删除错误
		public static final String DEPARTMENT_DEL_ERROR = "010305";
	}

	public static class INVITATION{
		//指定推荐人时没有传推荐人id
		public static final String INVITATION_INVITERID_NULL = "010401";

		//指定推荐人时没有传被推荐会员id
		public static final String INVITATION_INVITEE_NULL = "010402";

		//会员已经被推荐
		public static final String INVITATION_INVITEE_EXSIT = "010403";

		//不能指定自己为推荐人
		public static final String INVITATION_NOT_INVITATION_MYSELF= "010404";

		//会员没有推荐人不能评价
		public static final String NO_INVATER= "010405";
	}
}
