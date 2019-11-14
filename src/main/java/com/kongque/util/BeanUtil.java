/**
 * 
 */
package com.kongque.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuehui
 *
 * @2017年11月24日
 */
public class BeanUtil {

	private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

	/**
	 * copy 从org到des
	 * 
	 * @param des
	 * @param org
	 */
	public static <T> void beanCopy(T des, T org,String ...ignor) {
		Field[] fields = org.getClass().getDeclaredFields();
		try {
			for (Field f : fields) {
				f.setAccessible(true);
				if (Stream.of(des.getClass().getDeclaredFields()).anyMatch(e -> e.getName().equals(f.getName()))) {
					if (f.get(org) != null) {
						// list类型
						if (f.getType().isAssignableFrom(List.class)) {
							Type fc = Stream.of(des.getClass().getDeclaredFields())
									.filter(e -> e.getName().equals(f.getName())).findFirst().get().getGenericType();
							if (fc == null)
								continue;
							if (fc instanceof ParameterizedType) {
								ParameterizedType pt = (ParameterizedType) fc;
								List<Object> l = new ArrayList<Object>();
								for (int i = 0; i < ((List<?>) f.get(org)).size(); i++) {
									Object o = ((Class<?>) pt.getActualTypeArguments()[0]).newInstance();
									beanCopy(o, ((List<?>) f.get(org)).get(i));
									l.add(o);
								}
								methodValue(des, f.getName(), f.getType(), l, "set");
							}

						} else if (f.getType().isAssignableFrom(Set.class)) {
							// DOTO
						} else {
							methodValue(des, f.getName(), f.getType(), f.get(org), "set");
						}
					}else if(Stream.of(ignor).anyMatch(s->s.equals(f.getName()))){
						methodValue(des, f.getName(), f.getType(), null, "set");
					}
				}
			}
		} catch (Exception e) {
			logger.error("类copy失败", e);
		}
	}

	/**
	 * 获取值
	 * 
	 * @param des
	 * @param name
	 * @param type
	 * @param value
	 * @param operation
	 * @return
	 */
	private static <T> Object methodValue(T des, String name, Class<?> type, Object value, String operation) {

		try {
			Method m = getMethod(des, operation, name, type);
			if (m != null)
				return m.invoke(des, value);
			else
				return null;
		} catch (Exception e) {
			logger.error("类赋值错误：", e);
			return null;
		}
	}

	/**
	 * 获取Method
	 * 
	 * @param des
	 * @param operation
	 * @param name
	 * @param type
	 * @return
	 */
	private static <T> Method getMethod(T des, String operation, String name, Class<?> type) {

		try {
			return des.getClass().getMethod(getMethodName(operation, name), type);
		} catch (Exception e) {
			logger.error("获取方法错误,属性:"+name);
			return null;
		}
	}

	/**
	 * 获取方法名
	 * 
	 * @param operation
	 * @param name
	 * @return
	 */
	private static String getMethodName(String operation, String name) {

		return operation + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

}
