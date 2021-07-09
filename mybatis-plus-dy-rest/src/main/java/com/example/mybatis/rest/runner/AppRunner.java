package com.example.mybatis.rest.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-06 15:57
 */
@Slf4j
@Component
public class AppRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("AppRunner start ...");
    }
}
