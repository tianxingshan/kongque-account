/**
 * 
 */
package com.kongque.component.oauth;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.kongque.util.CryptographyUtils;

/**
 * @author yuehui
 * 自定义密码验证
 * @2017年12月8日
 */
public class PasswordUtil implements PasswordEncoder{

	/* (non-Javadoc)
	 * @see org.springframework.security.crypto.password.PasswordEncoder#encode(java.lang.CharSequence)
	 */
	@Override
	public String encode(CharSequence rawPassword) {
		
		return CryptographyUtils.md5(rawPassword.toString());
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.crypto.password.PasswordEncoder#matches(java.lang.CharSequence, java.lang.String)
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		//.replaceAll("\\{noop\\}", "")
		return rawPassword.equals(encodedPassword);
	}

}
