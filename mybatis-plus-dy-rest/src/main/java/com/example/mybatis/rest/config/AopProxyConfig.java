package com.example.mybatis.rest.config;

import com.example.mybatis.rest.utils.DynamicCompilerUtils;
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import static org.springframework.aop.config.AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-07 15:23
 */
public class AopProxyConfig implements ApplicationListener<ApplicationPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        final ConfigurableApplicationContext context = event.getApplicationContext();
        if (context.getBeanFactory() instanceof BeanDefinitionRegistry) {
            final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getBeanFactory();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(InfrastructureAdvisorAutoProxyCreator.class);
            beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
            beanDefinition.getPropertyValues().add("proxyClassLoader", DynamicCompilerUtils.CLASSLOADER);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME, beanDefinition);
        }
    }
}
