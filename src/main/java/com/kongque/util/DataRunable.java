package com.kongque.util;

import com.codingapi.tx.aop.bean.TxTransactionLocal;
import com.kongque.component.ThreadCache;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @Auther: yuehui
 * @Date: 2019/4/17 10:44
 * @Description:
 */
public class DataRunable<T> implements  Runnable {

    private T t;

    private Consumer<T> consumer;

    //多系统访问时的token
    private String token;

    //异步线程事务
    private TxTransactionLocal txTransactionLocal;

    public DataRunable(){
        token=SysUtil.getToken();
        txTransactionLocal = TxTransactionLocal.current();
    }

    public DataRunable(T t,Consumer<T> c){
        this();
        this.t=t;this.consumer=c;
    }

    public DataRunable(String token,T t,Consumer<T> c){
        this();
        this.t=t;
        this.token=token;
        this.consumer=c;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    @Override
    public void run() {
        ThreadCache.setToken(this.token);
        TxTransactionLocal.setCurrent(this.txTransactionLocal);
        consumer.accept(t);
    }
}
