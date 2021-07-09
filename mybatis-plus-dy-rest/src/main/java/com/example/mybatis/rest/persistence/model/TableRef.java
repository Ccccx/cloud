package com.example.mybatis.rest.persistence.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mybatis.rest.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 表字段关系
 * </p>
 *
 * @author chengjz
 * @since 2021-07-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("onl_table_ref")
@ApiModel(value="TableRef对象", description="表字段关系")
public class TableRef extends BaseModel {


    @ApiModelProperty(value = "id")
    @TableId("id")
    private String id;

    @ApiModelProperty(value = "主表ID")
    @TableField("table_id")
    private String tableId;

    @ApiModelProperty(value = "附表ID")
    @TableField("sub_table_id")
    private String subTableId;

    @ApiModelProperty(value = "主表关联字段")
    @TableField("table_key")
    private String tableKey;

    @ApiModelProperty(value = "附表关联字段")
    @TableField("sub_table_key")
    private String subTableKey;


}
