package com.example.mybatis.rest.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 17:11
 */
@Data
public class TableMetaConfig {
    private final TableInfo tableInfo;
    private Class<?> modelClass;
    private Class<?> mapperClass;

    private final Map<String, TableField> fieldMap;
    private final Map<String, TableField> columnMap;

    public TableMetaConfig(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        fieldMap = tableInfo.getFields().stream().collect(Collectors.toMap(TableField::getPropertyName, v -> v, (v1, v2) -> v1));
        columnMap = tableInfo.getFields().stream().collect(Collectors.toMap(TableField::getColumnName, v -> v, (v1, v2) -> v1));
    }
}
