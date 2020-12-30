package com.example.factorydemo.life;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-04-09 15:30
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LifeDemo implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware, EmbeddedValueResolverAware, ResourceLoaderAware, ApplicationEventPublisherAware,
        MessageSourceAware, ApplicationContextAware, ServletContextAware, BeanPostProcessor, InitializingBean, DestructionAwareBeanPostProcessor {

    private String name;

    private String desc;

    @Override
    public void setBeanName(String name) {
        log.info("BeanNameAware#setBeanName: {}", name);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        log.info("BeanClassLoaderAware#setBeanClassLoader: {}", classLoader);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info("BeanFactoryAware#setBeanFactory: {}", beanFactory);
    }

    @Override
    public void setEnvironment(Environment environment) {
        log.info("EnvironmentAware#setEnvironment: {}", environment);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        log.info("EmbeddedValueResolverAware#setEmbeddedValueResolver: {}", resolver);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        log.info("ResourceLoaderAware#setResourceLoader: {}", resourceLoader);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        log.info("ApplicationEventPublisherAware#setApplicationEventPublisher: {}", applicationEventPublisher);
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        log.info("MessageSourceAware#setMessageSource: {}", messageSource);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("ApplicationContextAware#setApplicationContext: {}", applicationContext);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        log.info("ServletContextAware#setServletContext: {}", servletContext);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.info("BeanPostProcessor#postProcessBeforeInitialization: {}", bean);
        return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("InitializingBean#afterPropertiesSet()");
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("BeanPostProcessor#postProcessAfterInitialization: {}", bean);
        return bean;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("@PostConstruct ...");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("@PreDestroy ...");
    }

    public void initMethod() {
        log.info("initMethod ...");
    }


    public void destroyMethod() {
        log.info("destroyMethod ...");
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        log.info("DestructionAwareBeanPostProcessor#postProcessBeforeDestruction:{}", bean);
    }

}
