package com.example.factory.project;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface ChildGenerationConfiguration {
}
