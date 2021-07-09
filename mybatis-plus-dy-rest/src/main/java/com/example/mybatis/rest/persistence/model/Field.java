package com.example.mybatis.rest.persistence.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mybatis.rest.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 字段信息
 * </p>
 *
 * @author chengjz
 * @since 2021-07-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("onl_field")
@ApiModel(value="Field对象", description="字段信息")
public class Field extends BaseModel {


    @ApiModelProperty(value = "id")
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "表ID")
    @TableField("table_id")
    private Long tableId;

    @ApiModelProperty(value = "字段名")
    @TableField("db_field_name")
    private String dbFieldName;

    @ApiModelProperty(value = "数据库字段注释")
    @TableField("db_field_comment")
    private String dbFieldComment;

    @ApiModelProperty(value = "JAVA对象属性名")
    @TableField("java_filed_name")
    private String javaFiledName;

    @ApiModelProperty(value = "JAVA对象类型")
    @TableField("java_field_type")
    private String javaFieldType;

    @ApiModelProperty(value = "表单展示名称")
    @TableField("alais")
    private String alais;

    @ApiModelProperty(value = "字段类型")
    @TableField("db_type")
    private String dbType;

    @ApiModelProperty(value = "数据库字段长度")
    @TableField("db_length")
    private Integer dbLength;

    @ApiModelProperty(value = "是否主键")
    @TableField("db_is_pk")
    private Boolean dbIsPk;

    @ApiModelProperty(value = "新增能否为空")
    @TableField("save_nullable")
    private Boolean saveNullable;

    @ApiModelProperty(value = "更新能否为空")
    @TableField("update_nullable")
    private Boolean updateNullable;

    @ApiModelProperty(value = "是否查询条件")
    @TableField("is_query")
    private Boolean isQuery;

    @ApiModelProperty(value = "能否读取")
    @TableField("is_show_list")
    private Boolean isShowList;

    @ApiModelProperty(value = "能否写入")
    @TableField("is_show_form")
    private Boolean isShowForm;

    @ApiModelProperty(value = "组件类型")
    @TableField("component_type")
    private Integer componentType;

    @ApiModelProperty(value = "组件校验规则")
    @TableField("component_valid_rule")
    private Boolean componentValidRule;

    @ApiModelProperty(value = "组件默认值")
    @TableField("component_default_val")
    private String componentDefaultVal;

    @ApiModelProperty(value = "组件属性")
    @TableField("componetn_extend_attribute")
    private String componetnExtendAttribute;

    @ApiModelProperty(value = "序号")
    @TableField("order_no")
    private Integer orderNo;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;


}
