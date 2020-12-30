package com.cjz.webmvc.liquibase.support;

import com.cjz.webmvc.liquibase.constant.ColumnType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-08 11:12
 */
@Data
@Accessors(chain = true)
@ApiModel("列信息")
public class ColumnInfo {
    @ApiModelProperty("列名")
    private String name;
    @ApiModelProperty("列类型")
    private ColumnType type;
    @ApiModelProperty("是否主键")
    private boolean pk;
    @ApiModelProperty("是否非空")
    private boolean nonNull;
    @ApiModelProperty("注释")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ColumnInfo that = (ColumnInfo) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
