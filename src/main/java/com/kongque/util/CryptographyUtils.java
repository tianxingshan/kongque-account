package com.kongque.util;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CryptographyUtils {
	/**
	 * base64加密
	 * 
	 * @param str
	 * @return
	 */
	public static String encBase64(String str) {
		return Base64.encodeBase64String(str.getBytes());
	}

	/**
	 * base64解密
	 * 
	 * @param str
	 * @return
	 */
	public static String decBase64(String str) {
		return new String(Base64.decodeBase64(str));
	}

	/**
	 * Md5加密
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		return DigestUtils.md5Hex(str);
	}

	/**
	 * 获取随机字符串 Nonce Str
	 *
	 * @return String 随机字符串
	 */
	public static String generateNonceStr() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
	}
}
