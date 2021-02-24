package com.example.mybatis.rest.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    @Primary
    public MybatisPlusProperties mybatisPlusProperties() {
        final MybatisPlusProperties mybatisPlusProperties = new MybatisPlusProperties();
        mybatisPlusProperties.setConfiguration(dyMybatisConfiguration());
        return mybatisPlusProperties;
    }

    @Bean
    public DyMybatisConfiguration dyMybatisConfiguration() {
        return new DyMybatisConfiguration();
    }

}
