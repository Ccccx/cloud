package com.example.factorydemo.jdbc;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.*;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-06 18:19
 */
 @Slf4j
  class JdbcTest {

    @Test
    @SneakyThrows
    void t1() {
        final HikariDataSource dataSource = getDataSource();
        // 低级用法
        final Connection connection = DataSourceUtils.getConnection(dataSource);
        connection.close();
        // Spring 默认事务代理包装类
        final TransactionAwareDataSourceProxy dataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
        final Connection connection1 = dataSourceProxy.getConnection();
        connection.close();
        // 测试工具类
        int count = JdbcTestUtils.countRowsInTable(new JdbcTemplate(dataSource), "T_USERS");
         log.info("count : {}", count);
    }

    @Test
    void t2() {
        final HikariDataSource dataSource = getDataSource();
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        final AnnotationTransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();

        TransactionInterceptor ti = new TransactionInterceptor();
        ti.setTransactionAttributeSource(transactionAttributeSource);

        final BeanFactoryTransactionAttributeSourceAdvisor transactionAttributeSourceAdvisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        transactionAttributeSourceAdvisor.setTransactionAttributeSource(transactionAttributeSource);
        transactionAttributeSourceAdvisor.setAdvice(ti);


        TransactionProxyFactoryBean proxyFactoryBean  = new TransactionProxyFactoryBean();
     }

    public static   HikariDataSource  getDataSource() {
        final DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl("jdbc:mysql://cx:13306/cloud?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8");
        dataSourceProperties.setUsername("root");
        dataSourceProperties.setPassword("meiyoumima.0");
        dataSourceProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
       return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
