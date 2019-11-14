package com.kongque.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: yuehui
 * @Date: 2019/4/25 17:24
 * @Description:
 */
public class MyLogger {

    private Logger logger;

    private MyLogger(){}

    private MyLogger(Class c){logger=LoggerFactory.getLogger(c);}

    public static MyLogger getinstance(){
        return new MyLogger(new Exception().getStackTrace()[1].getClass());
    }
    public void info(String mes){
        logger.info("account:"+ SysUtil.getAccount()+"。"+mes);
    }
    public void error(String mes,Exception e){
        logger.error("account:"+ SysUtil.getAccount()+"。"+mes,e);
    }
    public void error(String mes){
        logger.error("account:"+ SysUtil.getAccount()+"。"+mes);
    }

}
