package com.cjz.webmvc.config;

import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @see org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration.EmbeddedTomcat
 * @see org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration.TomcatWebServerFactoryCustomizerConfiguration
 * @author chengjz
 * @version 1.0
 * @since 2021-03-15 10:01
 */
@Configuration
public class TomcatConfig {

//    @Bean
//    public TomcatContextCustomizer docBase() {
//        return (context -> {
//            //设置JSP文件目录
//            String webappDir = "E:\\jsp";
//            context.setDocBase(webappDir);
//        });
//    }
}
