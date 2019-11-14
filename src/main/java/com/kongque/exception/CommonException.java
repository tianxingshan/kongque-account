package com.kongque.exception;

import java.io.Serializable;

/**
 * @Auther: yuehui
 * @Date: 5/29 2019 10:34
 * @Description:通用异常类,不用手动事务回滚
 */
public class CommonException extends RuntimeException implements Serializable{

    private static final long serialVersionUID = 6232178208491319674L;
    /**
     * 错误码
     */
    private String code;

    private CommonException(){}

    public CommonException(String code, String msg){
        super(msg);
        this.code=code;
    }

    public CommonException(String code, String msg,Throwable e){
        super(msg,e);
        this.code=code;
    }
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return getString(code,super.getMessage());
    }

    private static String getString(String c, String m){
        return "{\"code\":"+c+",\"message\":\""+m+"\"}";
    }
}
