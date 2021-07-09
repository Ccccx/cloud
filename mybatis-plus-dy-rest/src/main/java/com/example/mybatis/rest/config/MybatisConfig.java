package com.example.mybatis.rest.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.example.mybatis.rest.extend.MyLogicSqlInjector;
import com.example.mybatis.rest.model.DynamicCaptchaWrapper;
import com.example.mybatis.rest.support.HotCompileTableConfigManager;
import com.example.mybatis.rest.support.ITableConfigManager;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Properties;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-19 17:32
 */
@Configuration
public class MybatisConfig {

    @Bean
    public DataSourceConfig dataSourceConfig(DataSourceProperties dataSourceProperties) {
        final DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriverName(dataSourceProperties.getDriverClassName());
        dataSourceConfig.setUrl(dataSourceProperties.getUrl());
        dataSourceConfig.setUsername(dataSourceProperties.getUsername());
        dataSourceConfig.setPassword(dataSourceProperties.getPassword());
        return dataSourceConfig;
    }

    /**
     * 自定义 SqlInjector
     * 里面包含自定义的全局方法
     */
    @Bean
    public MyLogicSqlInjector myLogicSqlInjector() {
        return new MyLogicSqlInjector();
    }

    @Bean
    public ITableConfigManager tableConfigManager() {
        return new HotCompileTableConfigManager();
    }

    @Bean
    @RequestScope
    public DynamicCaptchaWrapper dynamicCaptchaWrapper() {
        return new DynamicCaptchaWrapper();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

//    @Bean
//    @Order(30)
//    public InsertBatchInterceptor insertBatchInterceptor(){
//        return new InsertBatchInterceptor();
//    }

    @Bean
    @Primary
    public MybatisPlusProperties mybatisPlusProperties() {
        final MybatisPlusProperties mybatisPlusProperties = new MybatisPlusProperties();
        final Properties properties = new Properties();
        properties.setProperty("jdbcTypeForNull", "NULL");
        mybatisPlusProperties.setConfigurationProperties(properties);
        mybatisPlusProperties.setConfiguration(dyMybatisConfiguration());
        return mybatisPlusProperties;
    }

    @Bean
    public DyMybatisConfiguration dyMybatisConfiguration() {
        final DyMybatisConfiguration dyMybatisConfiguration = new DyMybatisConfiguration();
        // dyMybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);
        return dyMybatisConfiguration;
    }

}
