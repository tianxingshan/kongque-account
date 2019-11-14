/**
 * 
 */
package com.kongque.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

/**
 * @author cary time:2017年6月15日
 */
public class JsonUtil {

	public static JSONArray arrayToJson(Object obj) {

		return JSONArray.fromObject(obj, getConfig());
	}
	
	
	/**
	 * list转josn
	 * @param obj
	 * @param s 忽略的key
	 * @param c
	 * @param ignore
	 * @return
	 */
	public static <T> JSONArray arrayToJson(Object obj,String [] s,Class<T>[]c,String []...ignore) {
		if(obj==null)
			return new JSONArray();
		return JSONArray.fromObject(obj, getConfig(s, c, ignore));
	}
	/**
	 * object转json
	 * @param obj
	 * @return
	 */
	public static JSONObject objToJson(Object obj) {
		if(obj==null)
			return new JSONObject();

		return JSONObject.fromObject(obj, getConfig());
	}
	/**
	 * object 转json
	 * @param obj
	 * @param s 忽略的key
	 * @return
	 */
	public static <T> JSONObject toJson(Object obj,String [] s){
		if(obj==null)
			return new JSONObject();
		return JSONObject.fromObject(obj,getConfig(s, null));
	}
	
	/**
	 * 只返回s中的key
	 * @param obj
	 * @param s
	 * @return
	 */
	public static <T> JSONObject toJson2(Object obj,String [] s){
		if(obj==null)
			return new JSONObject();
		JsonConfig j=getConfig();
		j.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				return !Arrays.asList(s).contains(name)||value == null;
			}
		});
		return JSONObject.fromObject(obj,j);
	}
	/**
	 * 只返回s中的key
	 * @param obj
	 * @param s
	 * @return
	 */
	public static <T> JSONArray arrayToJson2(Object obj,String [] s) {
		JsonConfig j=getConfig();
		j.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				return !Arrays.asList(s).contains(name)||value == null;
			}
		});
		return JSONArray.fromObject(obj, j);
	}
	
	public static <T> JSONObject toJson(Object obj,String [] s,Class<T>[]c,String []...ignore){
		return JSONObject.fromObject(obj,getConfig(s, c, ignore));
	}

	public static <T>JsonConfig getConfig(String [] s,Class<T>[]c,String []...ignore) {
		
		JsonConfig config = getConfig();
		String g[]=new String [s.length+config.getExcludes().length];
		for(int i=0;i<config.getExcludes().length;i++){
			g[i]=config.getExcludes()[i];
		}
		int l=config.getExcludes().length;
		if(s!=null)
			for(int i=0;i<s.length;i++){
				g[i+l]=s[i];
			}
		config.setExcludes(g);
		if(c!=null)
			for(int i=0;i<c.length;i++){
				config.registerPropertyExclusions(c[i], ignore[i]);
			}
		return config;
		
	}
	public static JsonConfig getConfig() {

		JsonConfig config = new JsonConfig();
		config.setExcludes(new String[] { "handler", "hibernateLazyInitializer" });
		config.registerJsonValueProcessor(Date.class,new JsonValueProcessor(){

			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			@Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				// TODO Auto-generated method stub
				return dateFormat.format(arg0);
			}

			@Override
			public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {
				// TODO Auto-generated method stub
				return dateFormat.format(arg1);
			}
		});
		config.setJsonPropertyFilter(new PropertyFilter() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean apply(Object source, String name, Object value) {
				if(value instanceof Set)
					return ((Set)value).size()==0;
				if(value instanceof List)
					return ((List)value).size()==0;
				else
					return value == null;
			}
		});
		config.setCycleDetectionStrategy(CycleDetectionStrategy.NOPROP);
		
		return config;
	}
}
