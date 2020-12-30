package com.example.factory.config;

import lombok.Data;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * 内置Bean
 *
 * @author chengjz
 * @version 1.0
 * @date 2020-01-11 9:45
 */
@Data
public class ContextInnerBean {

    @Resource
    private BeanFactory beanFactory;

    @Resource
    private ObjectFactory<ApplicationContext> objectFactory;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private Environment environment;
}
