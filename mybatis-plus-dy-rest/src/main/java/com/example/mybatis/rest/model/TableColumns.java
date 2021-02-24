package com.example.mybatis.rest.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 16:17
 */
@Data
@ApiModel("表字段描述")
public class TableColumns {
    @ApiModelProperty("类字段名称")
    private String fieldName;
    @ApiModelProperty("表字段名称")
    private String columnName;
    @ApiModelProperty("字段标题")
    private String title;
    @ApiModelProperty("字段描述")
    private String desc;
    @ApiModelProperty("类字段类型")
    private Class<?> clz;
}
