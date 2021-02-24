package com.example.mybatis.rest.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 16:14
 */
@Data
@ApiModel("表元信息")
public class TableMetadata {
    @ApiModelProperty("类名称")
    private String className;
    @ApiModelProperty("表名")
    private String tableName;
    @ApiModelProperty("描述")
    private String desc;
    @ApiModelProperty("动态字段信息")
    private List<TableColumns> columns;
}
