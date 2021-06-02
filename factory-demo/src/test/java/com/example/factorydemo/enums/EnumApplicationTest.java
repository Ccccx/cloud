package com.example.factorydemo.enums;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.*;

import static com.example.factorydemo.enums.DictHolders.MAPPER_HOLDERS;


/**
 * 枚举类转换成实际数据
 * @author chengjz
 * @version 1.0
 * @since 2021-04-30 11:20
 */
@Slf4j
@Configuration
public class EnumApplicationTest {
    public static void main(String[] args) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(EnumApplicationTest.class);
        context.register(DictRegistryPostProcessor.class);
        context.register(TestEnumDictBeanFactory.class);
        context.refresh();
        log.info("{}", MAPPER_HOLDERS);
    }

}
