package com.example.factory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * @author chengjz
 * @version 1.0
 * @see org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration.EmbeddedTomcat
 * @see org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration.TomcatWebServerFactoryCustomizerConfiguration
 * @since 2021-03-15 10:01
 */
@Slf4j
public class TomcatConfig {

    @Bean
    public TomcatContextCustomizer docBase() {
        return (context -> {
            //设置JSP文件目录
            String webappDir = "E:\\tomcat\\jsp";
            context.setDocBase(webappDir);
        });
    }
}
