package com.example.mybatis.rest.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-25 10:41
 */
@Data
public class DynamicCaptchaWrapper {
    @Value("${test.str:default}")
    private String str;

    @PostConstruct
    public void init() {
        System.out.println(str);
    }
}
