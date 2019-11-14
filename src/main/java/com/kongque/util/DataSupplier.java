package com.kongque.util;

import java.util.function.Supplier;

/**
 * @Auther: yuehui
 * @Date: 2019/4/17 10:10
 * @Description:
 */
public class DataSupplier<T> implements Supplier {

    private T t;

    public DataSupplier(){}

    public DataSupplier(T t){this.t=t;}

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    @Override
    public T get() {
        return t;
    }
}
