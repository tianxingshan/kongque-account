package com.kongque.component.impl;

import com.codingapi.tx.listener.service.InitService;
import com.codingapi.tx.netty.service.NettyService;
import com.codingapi.tx.springcloud.listener.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class NettyListener implements ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(NettyListener.class);
    @Autowired
    private InitService initService;
    
    @Autowired
    private NettyService nettyService;

    @Autowired
    private ServerListener serverListener;
    @Override
    public void setApplicationContext( ApplicationContext applicationContext) throws BeansException {
        logger.info("nettyListener Startup!");
        try{
            Field f= ServerListener.class.getDeclaredField("serverPort");
            f.setAccessible(true);
            f.set(serverListener,9999);
        }catch (Exception e){
            logger.error("netty启动错误",e);
        }
       if(!nettyService.checkState()){
    	   logger.info("nettyListener 启动!");
    	   initService.start();
       }
    }

}

