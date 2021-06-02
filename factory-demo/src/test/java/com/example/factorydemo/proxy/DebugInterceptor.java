package com.example.factorydemo.proxy;


import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 11:34
 */
@Slf4j
public class DebugInterceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("Before: invocation=[" + invocation + "]");
        Object rval = invocation.proceed();
        log.info("Invocation returned");
        return rval;
    }
}