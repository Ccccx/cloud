package com.example.factorydemo.life;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-28 14:48
 */
@Slf4j
@Component
public class BooBean {

    @PostConstruct
    public void init() {
        log.warn("BooBean ...");
    }
}
