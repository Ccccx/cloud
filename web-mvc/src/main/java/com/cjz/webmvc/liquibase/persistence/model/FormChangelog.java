package com.cjz.webmvc.liquibase.persistence.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author chengjz
 * @since 2020-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ONL_FORM_CHANGELOG")
@ApiModel(value = "FormChangelog对象", description = "")
public class FormChangelog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String tableName;

    @ApiModelProperty(value = "状态 1 正常 0 删除")
    private Integer status;

    private String changelog;


}
