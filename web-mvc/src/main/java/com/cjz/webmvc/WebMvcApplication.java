package com.cjz.webmvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author chengjz
 */
@SpringBootApplication
@Slf4j
public class WebMvcApplication {

    @Resource
    private ListableBeanFactory beanFactory;

    @PostConstruct
    public void init() {
        log.info("beanFactory : {}", beanFactory);
    }

    public static void main(String[] args) {
        SpringApplication.run(WebMvcApplication.class, args);
    }

}
