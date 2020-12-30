package com.cjz.webmvc.liquibase.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-08 11:14
 */
@Data
@ApiModel("字段类型")
public class ColumnType {

    @ApiModelProperty("数据库类型")
    private String dbType;
    @ApiModelProperty("长度")
    private Integer length;
    @ApiModelProperty("精度(小数点位数)")
    private Integer precision;

    public ColumnType(String dbType, Integer length, Integer precision) {
        this.dbType = dbType;
        this.length = length;
        this.precision = precision;
    }

    /**
     * 字符类型
     */
    public static ColumnType stringType() {
        return new ColumnType("VARCHAR", 50, null);
    }

    /**
     * 时间类型
     */
    public static ColumnType datetimeType() {
        return new ColumnType("DATETIME", null, null);
    }

    /**
     * 数字类型
     */
    public static ColumnType intType() {
        return new ColumnType("INT", 10, null);
    }

    /**
     * 浮点数
     */
    public static ColumnType doubleType() {
        return new ColumnType("DOUBLE", 10, null);
    }


    public ColumnType length(int length) {
        this.length = length;
        return this;
    }

    public ColumnType precision(int precision) {
        this.precision = precision;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(dbType);
        if (length != null && precision != null) {
            sb.append("(").append(length).append(",").append(precision).append(")");
        } else if (length != null) {
            sb.append("(").append(length).append(")");
        }
        return sb.toString();
    }
}
