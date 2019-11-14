package com.kongque.util;

import java.util.Arrays;
import java.util.Date;
import java.util.Stack;
import java.util.UUID;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	private static char[] charSet = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
	private static char[] charSet2 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	/**
	 * 将10进制转化为62进制
	 *
	 * @param number
	 * @param length
	 *            转化成的62进制长度，不足length长度的话高位补0，否则不改变什么
	 * @return
	 */
	public static String _10_to_62(long number, int length) {

		Long rest = number;
		Stack<Character> stack = new Stack<Character>();
		StringBuilder result = new StringBuilder(0);
		while (rest != 0) {
			stack.add(charSet2[new Long((rest - (rest / 62) * 62)).intValue()]);
			rest = rest / 62;
		}
		for (; !stack.isEmpty();) {
			result.append(stack.pop());
		}
		int result_length = result.length();
		StringBuilder temp0 = new StringBuilder();
		for (int i = 0; i < length - result_length; i++) {
			temp0.append('0');
		}

		return temp0.toString() + result.toString();

	}
	/**
	 * 将10进制转化为32进制
	 *
	 * @param number
	 * @param length
	 *            转化成的32进制长度，不足length长度的话高位补0，否则不改变什么
	 * @return
	 */
	public static String _10_to_32(long number, int length) {

		Long rest = number;
		Stack<Character> stack = new Stack<Character>();
		StringBuilder result = new StringBuilder(0);
		while (rest != 0) {
			stack.add(charSet[new Long((rest - (rest / 32) * 32)).intValue()]);
			rest = rest / 32;
		}
		for (; !stack.isEmpty();) {
			result.append(stack.pop());
		}
		int result_length = result.length();
		StringBuilder temp0 = new StringBuilder();
		for (int i = 0; i < length - result_length; i++) {
			temp0.append('0');
		}

		return temp0.toString() + result.toString();

	}

	/**
	 * 将62进制转换成10进制数
	 *
	 * @param ident62
	 * @return
	 */
	public static String convertBase62ToDecimal(String ident62) {

		long decimal = 0;
		long base = 62;
		long keisu = 0;
		long cnt = 0;

		byte ident[] = ident62.getBytes();
		for (int i = ident.length - 1; i >= 0; i--) {
			int num = 0;
			if (ident[i] > 48 && ident[i] <= 57) {
				num = ident[i] - 48;
			} else if (ident[i] >= 65 && ident[i] <= 90) {
				num = ident[i] - 65 + 10;
			} else if (ident[i] >= 97 && ident[i] <= 122) {
				num = ident[i] - 97 + 10 + 26;
			}
			keisu = (long) java.lang.Math.pow((double) base, (double) cnt);
			decimal += num * keisu;
			cnt++;
		}
		return String.format("%08d", decimal);
	}
	/**
	 * 获取唯一码
	 * 最短长度7
	 */
	public static String getUUCode(int length) {

		if(length<7)
			length=7;
		return _10_to_62(System.currentTimeMillis() - 1433000000000L, length);

	}

	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };

	public static String generateShortUuid() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		shortBuffer.append( new Date().getTime()) ;
		return shortBuffer.toString();

	}
	/**
	 * 将32进制转换成10进制数
	 *
	 * @param ident62
	 * @return
	 */
	public static String convertBase32ToDecimal(String ident32) {

		long decimal = 0;
		long base = 32;
		long keisu = 0;
		long cnt = 0;
		byte baseChar[]="23456789ABCDEFGHJKLMNPQRSTUVWXYZ".getBytes();
		byte ident[] = ident32.getBytes();
		for (int i = ident.length - 1; i >= 0; i--) {
			int num = 0;
			for(int t=0;t<baseChar.length;t++)
				if(baseChar[t]==ident[i])
					num=t;
			keisu = (long) java.lang.Math.pow((double) base, (double) cnt);
			decimal += num * keisu;
			cnt++;
		}
		return String.format("%08d", decimal);
	}

	/**
	 * zongt
	 * 字符串判断
	 * 2019年5月31日09:19:26
	 * @param strs
	 * @return
	 */
	public static boolean isAnyBlank(String... strs){
		return Arrays.stream(strs).anyMatch(StringUtils::isBlank);
	}
}
