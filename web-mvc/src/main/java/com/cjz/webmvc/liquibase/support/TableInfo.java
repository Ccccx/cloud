package com.cjz.webmvc.liquibase.support;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-08 10:58
 */
@Data
@ApiModel("表信息")
public class TableInfo {
    @ApiModelProperty(value = "表名", required = true)
    private String tableName;
    @ApiModelProperty(value = "创建用户", required = true)
    private String createUser;
    @ApiModelProperty(value = "版本号, 不传会自动生成, 一般命名为: 表名-当前时间yyyyMMddHHmmss格式", example = "tablename-202009081154")
    private String version;
    @ApiModelProperty("新增表字段集合")
    private LinkedHashSet<ColumnInfo> columns;
    @ApiModelProperty("需要删除的字段集合")
    private List<String> dropColumns;
    @ApiModelProperty("自定义changeset片段, 会原样添加至changelog中, 注意id不能重复")
    private List<String> changesets;
    @ApiModelProperty("数据库配置参数, 如果不传入则从应用类根目录的generator/liquibase.properties中读取")
    private DbConf dbConf;
}
