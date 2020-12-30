package com.example.factorydemo;

import com.example.factorydemo.bean.Foo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.ClassPathResource;

import java.beans.Introspector;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-28 16:50
 */
@Slf4j
public class ConfigDemo {
    @Test
    void t1() {
        Foo from = new Foo();
        from.setName("cjz");
        final PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Foo to = new Foo();
        mapper.from(from::getName).to(to::setName);
        mapper.from(from::isFlag).to(to::setFlag);
        mapper.from(from::getAge).to(to::setAge);
        System.out.println(to);
    }

    @Test
    void t2() {
        log.info(Introspector.decapitalize(ConfigDemo.class.getSimpleName()));
    }

    @Test
    void t3() throws Exception {
        final ClassPathResource prd = new ClassPathResource("prd");
        log.info("{}", prd.getFile().getAbsolutePath());
    }
}
