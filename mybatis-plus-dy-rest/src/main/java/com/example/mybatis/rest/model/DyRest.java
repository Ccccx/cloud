package com.example.mybatis.rest.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-22 17:31
 */
@Data
@TableName("DY_REST")
public class DyRest {
    @TableId
    private String id;
    private String name;
    private int age;
}
