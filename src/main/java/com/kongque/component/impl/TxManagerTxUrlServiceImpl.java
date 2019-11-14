package com.kongque.component.impl;

import com.codingapi.tx.config.service.TxManagerTxUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * create by lorne on 2017/11/18
 */
@Service
public class TxManagerTxUrlServiceImpl implements TxManagerTxUrlService{

    private static final Logger logger = LoggerFactory.getLogger(TxManagerTxUrlServiceImpl.class);

    @Value("${tm.manager.url}")
    private String url;

    @Override
    public String getTxUrl() {
       logger.info("加载事务管理："+url);
        return url;
    }
}
