package com.example.mybatis.rest.persistence.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 表元信息
 * </p>
 *
 * @author chengjz
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("onl_table")
@ApiModel(value="Table对象", description="表元信息")
public class Table implements Serializable {


    @ApiModelProperty(value = "id")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "表名")
    @TableField("table_name")
    private String tableName;

    @ApiModelProperty(value = "表注释")
    @TableField("table_comment")
    private String tableComment;

    @ApiModelProperty(value = "表单展示名称")
    @TableField("form_name")
    private String formName;

    @ApiModelProperty(value = "JAVA实体类名")
    @TableField("java_name")
    private String javaName;

    @ApiModelProperty(value = "表类型 1 单表 2 附表 3 树表 4 左树")
    @TableField("table_type")
    private Integer tableType;

    @ApiModelProperty(value = "数据源")
    @TableField("database_conf")
    private String databaseConf;

    @ApiModelProperty(value = "描述")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "版本号")
    @TableField("version_code")
    private Integer versionCode;

    @ApiModelProperty(value = "Model全类名")
    @TableField("model_class")
    private String modelClass;

    @ApiModelProperty(value = "Model源码")
    @TableField("model_code")
    private String modelCode;

    @ApiModelProperty(value = "Mapper全类名")
    @TableField("mapper_class")
    private String mapperClass;

    @ApiModelProperty(value = "Mapper源码")
    @TableField("mapper_code")
    private String mapperCode;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;


}
