package com.example.mybatis.rest.component;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.example.mybatis.rest.model.TableConfig;
import com.example.mybatis.rest.model.TableConfigVo;
import com.example.mybatis.rest.persistence.model.Field;
import com.example.mybatis.rest.persistence.model.Table;
import com.example.mybatis.rest.service.IFieldService;
import com.example.mybatis.rest.support.ITableConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-02 17:14
 */
@Slf4j
@Component
public class TableManagerComponent {

    @Resource
    private ITableConfigManager tableConfigManager;

//    @Resource
//    private ITableService tableService;

    @Resource
    private IFieldService fieldService;

    public TableConfigVo getConfigByTableName(String tableName) {
        try {
            return tableConfig2Vo(tableConfigManager.loadTableConfig(tableName));
        } catch (Exception e) {
            log.info("获取配置 发生异常",  e);
            return null;
        }
    }


    protected TableConfigVo tableConfig2Vo(TableConfig tableConfig) {
        TableConfigVo vo = new TableConfigVo();
        final TableInfo tableInfo = tableConfig.getTableInfo();
        final Table table = new Table();
        table.setTableName(tableInfo.getName());
        table.setTableComment(tableInfo.getComment());
        table.setJavaName(tableInfo.getEntityName());
        table.setModelClass(tableConfig.getModelClass().getCanonicalName());
        table.setModelCode(tableConfig.getModelCode());
        table.setMapperClass(tableConfig.getMapperClass().getCanonicalName());
        table.setMapperCode(tableConfig.getMapperCode());
        table.setServiceImplClass(tableConfig.getServiceImplClass().getCanonicalName());
        table.setServiceImplCode(tableConfig.getServiceImplCode());
        final List<TableField> fields = tableInfo.getFields();
        final List<Field> fieldList = fields.stream().map(v -> {
            final Field field = new Field();
            field.setTableId(table.getId());
            field.setDbFieldName(v.getName());
            field.setDbType(v.getType());
            field.setDbFieldComment(v.getComment());
            field.setJavaFiledName(v.getPropertyName());
            field.setJavaFieldType(v.getPropertyType());
            field.setDbIsPk(field.getDbIsPk());
            return field;
        }).collect(Collectors.toList());

        vo.setTable(table);
        vo.setFields(fieldList);
        return vo;
    }

    public TableConfigVo save(TableConfigVo req) {
        return null;
    }


    public void sync(String  tableName) {
        tableConfigManager.clearByTableName(tableName);
        tableConfigManager.loadTableConfig(tableName);
    }
}
