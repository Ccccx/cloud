package com.example.log4j2test.logger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-23 17:49
 */
@Configuration
public class LogbackConfiguration {
    @Bean
    @ConditionalOnClass(name = {"org.springframework.stereotype.Controller", "org.aspectj.lang.JoinPoint"})
    @ConditionalOnMissingBean
    public ControllerLoggingAspect controllerLoggingAspect() {
        return new ControllerLoggingAspect();
    }
}
