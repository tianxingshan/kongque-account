package com.kongque.component.impl;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * yuehui
 * 任务
 * 2018-7-11
 */
@Component
public class CustomTaskScheduler extends ThreadPoolTaskScheduler {


    private static final long serialVersionUID = -6333534561541076598L;

    /**
     *
     */
    public CustomTaskScheduler() {

        super();
        //线程池大小
        setPoolSize(30);
    }
}
