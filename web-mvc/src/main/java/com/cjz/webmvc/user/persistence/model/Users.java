package com.cjz.webmvc.user.persistence.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author chengjz
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("T_USERS")
@ApiModel(value = "Users对象", description = "用户信息表")
public class Users extends Model<Users> {


    @TableId("USERNAME")
    private String username;

    @ApiModelProperty(value = "用户密码")
    @TableField("PASSWORD")
    private String password;

    @ApiModelProperty(value = "用户昵称")
    @TableField("NICKNAME")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    @TableField("MOBILE")
    private String mobile;

    @ApiModelProperty(value = "电子邮箱")
    @TableField("EMAIL")
    private String email;

    @ApiModelProperty(value = "创建日期")
    @TableField("CREATE_DATE")
    private Date createDate;

    @ApiModelProperty(value = "是否可用")
    @TableField("ENABLED")
    private Integer enabled;


    @Override
    protected Serializable pkVal() {
        return this.username;
    }

}
