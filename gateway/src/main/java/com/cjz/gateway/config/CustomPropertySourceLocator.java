package com.cjz.gateway.config;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Collections;

/**
 * 引导过程添加的外部配置的默认属性源是Config Server，
 * 但您可以通过将PropertySourceLocator类型的bean添加到引导上下文（通过spring.factories）添加其他源。
 * 您可以使用此方法从其他服务器或数据库中插入其他属性。
 * <a href='https://springcloud.cc/spring-cloud-dalston.html'>点击详情</a>
 *
 * @author chengjz
 * @version 1.0
 * @date 2019-02-28 10:02
 */
@Configuration
public class CustomPropertySourceLocator implements PropertySourceLocator {
    @Override
    public PropertySource<?> locate(Environment environment) {
        return new MapPropertySource("customProperty",
                Collections.singletonMap("property.from.sample.custom.source", "worked as intended"));
    }
}
