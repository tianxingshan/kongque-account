/**
 * 
 */
package com.kongque.component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuehui
 *
 * @2018年1月17日
 */
public class ThreadCache {
	// ThreadLocal里只存储了简单的String对象，也可以自己定义对象，存储更加复杂的参数
    private static ThreadLocal<Map<String,String>> threadLocal = new ThreadLocal<Map<String,String>>();

    public static String getPostRequestParams(){
    	return threadLocal.get().get("post");
    }
    
    public static void setPostRequestParams(String postRequestParams){

        Map<String,String> map=threadLocal.get();
        if(map==null)
            map=new HashMap<>();

        map.put("post",postRequestParams);
        threadLocal.set(map);
    }

    public static void removePostRequestParams(){
        Map<String,String> map=threadLocal.get();
        if(map!=null)
            map.remove("post");
    }


    public static String getToken(){
        return threadLocal.get().get("token");
    }

    public static void setToken(String token){

        Map<String,String> map=threadLocal.get();
        if(map==null)
            map=new HashMap<>();

        map.put("token",token);
        threadLocal.set(map);
    }

    public static void removeToken(){
        Map<String,String> map=threadLocal.get();
        if(map!=null)
            map.remove("token");
    }

}
