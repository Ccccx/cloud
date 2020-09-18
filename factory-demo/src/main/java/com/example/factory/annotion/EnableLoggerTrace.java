package com.example.factory.annotion;

import com.example.factory.config.LoggerTraceConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-05-13 18:23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoggerTraceConfig.class)
public @interface EnableLoggerTrace {
}
