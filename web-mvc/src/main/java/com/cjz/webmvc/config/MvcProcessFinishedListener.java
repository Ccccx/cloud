package com.cjz.webmvc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-26 11:12
 */
@Slf4j
@Component
public class MvcProcessFinishedListener implements ApplicationListener<ServletRequestHandledEvent> {
    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        final String requestUrl = event.getRequestUrl();
        log.info("请求处理完成 : {}", requestUrl);
    }
}
