package com.example.mybatis.rest.model;

import com.example.mybatis.rest.persistence.model.Field;
import com.example.mybatis.rest.persistence.model.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-05 16:04
 */
@Data
@ApiModel("表配置信息")
public class TableConfigVo {
    @ApiModelProperty("表信息")
    private Table table;
    @ApiModelProperty("字段信息")
    private List<Field> fields;
}
