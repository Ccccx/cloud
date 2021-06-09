package com.cjz.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-16 19:20
 */
@RestController
@RefreshScope
public class CloudController {

    @Value("${useLocalCache:false}")
    private boolean useLocalCache;

    @Value("${test.message:nothing}")
    private String testCommonConfig;

    @RequestMapping()
    public String  get() {
        return "useLocalCache: " + useLocalCache + "\t testCommonConfig: " + testCommonConfig;
    }
}
