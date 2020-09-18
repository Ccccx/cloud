package com.example.factorydemo.life;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-04-09 17:51
 */
@Slf4j
public class DemoFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		log.info("BeanFactoryPostProcessor#postProcessBeanFactory");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		log.info("BeanFactoryPostProcessor#postProcessBeanDefinitionRegistry: {}", registry.getBeanDefinitionCount());
	}
}
