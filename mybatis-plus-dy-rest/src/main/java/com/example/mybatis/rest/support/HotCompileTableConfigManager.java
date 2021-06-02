package com.example.mybatis.rest.support;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.example.mybatis.rest.model.TableConfig;
import com.example.mybatis.rest.utils.DynamicCompilerUtils;
import com.example.mybatis.rest.utils.CodeGenerator;
import com.example.mybatis.rest.utils.CodeGenerator.CodeResult;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;

import static com.example.mybatis.rest.utils.CodeGenerator.ENTITY_CANONICAL_NAME;
import static com.example.mybatis.rest.utils.CodeGenerator.MAPPER_CANONICAL_NAME;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-07 11:35
 */
public class HotCompileTableConfigManager extends AbstractTableConfigManager{

    @Resource
    private DataSourceConfig dataSourceConfig;

    @Override
    public TableConfig update(String tableName) {
        return null;
    }

    @Override
    @SneakyThrows
    public TableConfig load(String tableName) {

        final CodeResult codeResult = CodeGenerator.run(tableName, dataSourceConfig);
        final TableInfo tableInfo = codeResult.getTableInfo();
        TableConfig tableConfig = new TableConfig(tableInfo);
        Assert.isTrue(
                StringUtils.isNotEmpty(codeResult.getEntityCode()) &&
                StringUtils.isNotEmpty(codeResult.getMapperCode()), "源码不存在");
        tableConfig.setModelCode(codeResult.getEntityCode());
        tableConfig.setMapperCode(codeResult.getMapperCode());
        tableConfig.setModelClass(DynamicCompilerUtils.compile(ENTITY_CANONICAL_NAME, codeResult.getEntityName(), codeResult.getEntityCode()));
        tableConfig.setMapperClass(DynamicCompilerUtils.compile(MAPPER_CANONICAL_NAME, codeResult.getMapperName(), codeResult.getMapperCode()));
        return tableConfig;
    }
}
