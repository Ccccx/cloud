package com.example.mybatis.rest.runner;

import com.example.mybatis.rest.model.DyRest;
import com.example.mybatis.rest.persistence.DyRestMapper;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-06 15:57
 */
@Slf4j
@Component
public class AppRunner implements ApplicationRunner {

    @Resource
    private SpringLiquibase liquibase;

    @Resource
    private DyRestMapper restMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("AppRunner start ...");
//        liquibase.setChangeLog("file:/E:/IDEA/cloud/mybatis-plus-dy-rest/src/main/resources/db/m1-onlineform-changelog.xml");
//        liquibase.afterPropertiesSet();
        log.info("AppRunner end ...");
          DyRest dyRest = new DyRest();
        dyRest.setId("t1");

        DyRest dyRest1 = new DyRest();
        dyRest1.setId("t2");

        List<DyRest> list = new ArrayList<>();
        list.add(dyRest);
        list.add(dyRest1);
        restMapper.insertAll(list);
    }
}
