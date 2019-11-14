package com.kongque.util;

import com.codingapi.tx.aop.bean.TxTransactionLocal;
import com.kongque.component.ThreadCache;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Auther: yuehui
 * @Date: 2019/4/28 14:15
 * @Description:
 */
public class DataFunction<T,R> implements Supplier<R> {

    private String token;

    private T t;

    private Function<T,R> f;

    //异步线程事务
    private TxTransactionLocal txTransactionLocal;

    public DataFunction(){
        token=SysUtil.getToken();
        txTransactionLocal = TxTransactionLocal.current();
    }

    public DataFunction(T t, Function<T,R> f){
        this();
        this.t=t;this.f=f;
    }

    public DataFunction(String token, T t, Function<T,R>  f){
        this();
        this.token=token;
        this.t=t;
        this.f=f;
    }

    @Override
    public R get() {
        ThreadCache.setToken(this.token);
        TxTransactionLocal.setCurrent(this.txTransactionLocal);
        return f.apply(t);
    }
}
