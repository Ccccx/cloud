package com.example.factorydemo.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 11:55
 */
@Slf4j
public class TestPointcut extends StaticMethodMatcherPointcut {
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        log.info("Method: {}", method.getName());
        log.info("Class: {}",targetClass);
        return true;
    }
}
