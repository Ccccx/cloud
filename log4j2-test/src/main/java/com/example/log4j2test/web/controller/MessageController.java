package com.example.log4j2test.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-28 16:51
 */
@Slf4j
@RestController
public class MessageController {

    @GetMapping("/")
    public String index() {
        return dateNow();
    }

    @GetMapping("/dateNow")
    public String dateNow() {
        final String dateTime = DateFormatUtils.format(new Date(), "YYYY-MM-DD HH:mm:ss");
        log.info("Date Now: {}", dateTime);
        return dateTime;
    }
}
