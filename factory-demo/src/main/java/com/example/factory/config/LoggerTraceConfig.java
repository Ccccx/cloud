package com.example.factory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-05-13 18:25
 */
@Slf4j
@Configuration
public class LoggerTraceConfig {
    @PostConstruct
    public void init() {
        log.debug("LoggerTraceConfig ... ");
    }
}
