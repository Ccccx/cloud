package com.cjz.webmvc.config;

import com.cjz.webmvc.base.contoiller.CacheController;
import com.cjz.webmvc.base.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 18:13
 */
@Configuration
@EnableScheduling
public class AppConfig {

    /**
     * 手动方式注入WEB 请求
     *
     * @param mapping 映射器
     * @param handler 已注册的处理器
     * @throws NoSuchMethodException
     */
    @Autowired
    public void setHandlerMapping(RequestMappingHandlerMapping mapping, CacheController handler)
            throws NoSuchMethodException {
        RequestMappingInfo info = RequestMappingInfo.paths("/t3").methods(RequestMethod.GET).build();
        Method method = CacheController.class.getMethod("t3");
        mapping.registerMapping(info, handler, method);
    }

    @Bean
    @RequestScope
    public User user() {
        return new User(UUID.randomUUID().toString());
    }

}
