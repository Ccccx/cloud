package com.example.mybatis.rest.support;

import com.example.mybatis.rest.model.TableConfig;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-07 18:41
 */
public interface ITableConfigManager {
    /**
     * 表配置信息
     * @param tableName 表名
     * @return 表配置信息
     */
    TableConfig loadTableConfig(String tableName);

    /**
     * 根据表名清除信息
     * @param tableName 表名
     */
    void clearByTableName(String tableName);

    /**
     * 更新配置信息
     * @param tableName 表名
     * @return  配置信息
     */
    TableConfig updateTableConfig(String tableName);

    /**
     * 注册为bean
     * @param tableConfig 表配置信息
     * @return 实例化信息
     */
    TableConfig registerBeanDefinition(TableConfig tableConfig);
}
