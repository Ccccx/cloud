package com.example.factorydemo.aop;

import com.example.factorydemo.bean.Foo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-04-07 19:13
 */
@Slf4j
@AopDemoApplication.CustomerConf
public class AopDemoApplication {
    public static void main(String[] args) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AopDemoApplication.class);
        context.refresh();
        final Foo foo = context.getBean(Foo.class);
        log.info("Foo : {}", foo);
        context.close();
    }

    @Bean
    public Foo foo() {
        log.info("foo init ...");
        return new Foo("cjz", true, 18);
    }


    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Configuration
    public @interface CustomerConf {

    }
}
