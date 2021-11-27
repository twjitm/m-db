package com.mdb.base.potiner;

import com.mdb.entity.MongoPo;
import com.mdb.utils.ZClassUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

public class MongoPointer {
    public static <T extends MongoPo> T getProxy(T t) {
        Enhancer en = new Enhancer();
        //进行代理
        en.setSuperclass(t.getClass());
        en.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                String methodName = method.getName();
                Object result = null;
                try {

                    if (methodName.startsWith("set")) {
                        String name = methodName.substring(3);
                        if (ZClassUtils.isField(t, name)) {
                        }
                    }
                    result = methodProxy.invokeSuper(o, args);
                } catch (Exception e) {
                    e.printStackTrace();
                    //异常通知, 可以访问到方法出现的异常
                }
                return result;
            }
        });
        Object result = en.create();
        return (T) result;
    }
}
