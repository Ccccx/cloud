package com.example.factory.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-03-19 16:14
 */
@Slf4j
public class RunningListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("系统启动完成!");
    }
}
