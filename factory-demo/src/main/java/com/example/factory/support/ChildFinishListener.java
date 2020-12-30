package com.example.factory.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 11:42
 */
@Slf4j
public class ChildFinishListener {
    @EventListener
    public void onApplicationEvent(ChildFinishEvent event) {
        log.debug("收到推送消息: {}  {}", event.getRequest(), event.getMetadata());
    }
}
