package com.example.mybatis.rest.controller;

import com.example.mybatis.rest.model.DynamicCaptchaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.core.env.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-25 10:38
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

    @Resource
    private Environment environment;

    @Resource
    private DynamicCaptchaWrapper dynamicCaptchaWrapper;

    @GetMapping
    public String environment() {
        final StandardEnvironment environment = (StandardEnvironment) this.environment;
        MutablePropertySources propertySources = environment.getPropertySources();

        String envKey = "cjz";
        Map<String, Object> map = new HashMap<>();
        final long random = RandomUtils.nextLong(9999, 99999999);
        map.put("test.str", random);
        final MapPropertySource cjzMap = new MapPropertySource(envKey, map);
        if (propertySources.contains(envKey)) {
            propertySources.replace(envKey, cjzMap);
        } else {
            propertySources.addFirst(cjzMap);
        }
        log.info("random: {}", random);
        return dynamicCaptchaWrapper.getStr();
    }

}
