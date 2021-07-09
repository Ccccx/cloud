package com.example.mybatis.rest.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.example.mybatis.rest.service.IOperationService;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 17:11
 */
@Data
public class TableConfig {
    /**
     * 表基本信息
     */
    private final TableInfo tableInfo;
    /**
     * java字段映射Map
     */
    private final Map<String, TableField> fieldMap;
    /**
     * 数据库字段映射Map
     */
    private final Map<String, TableField> columnMap;
    /**
     * 实体类
     */
    private Class<?> modelClass;
    /**
     * 实体源码
     */
    private String modelCode;
    /**
     * 映射接口
     */
    private Class<?> mapperClass;
    /**
     * 映射源码
     */
    private String mapperCode;
    /**
     * serviceImpl接口
     */
    private Class<IOperationService> serviceImplClass;
    /**
     * serviceImpl源码
     */
    private String serviceImplCode;
    /**
     * mapper实例化对象
     */
    private BaseMapper<?> mapperInstance;
    /**
     * serviceImpl实例化对象
     */
    private IOperationService<BaseModel> serviceImplInstance;

    public TableConfig(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        fieldMap = tableInfo.getFields().stream().collect(Collectors.toMap(TableField::getPropertyName, v -> v, (v1, v2) -> v1));
        columnMap = tableInfo.getFields().stream().collect(Collectors.toMap(TableField::getColumnName, v -> v, (v1, v2) -> v1));
    }
}
