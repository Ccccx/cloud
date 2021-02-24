package com.example.mybatis.rest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mybatis.rest.model.ObjectConstant.MAPPER;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 11:03
 */
@Data
public class BaseModel {

    @TableField(exist = false)
    @JsonIgnore
    @JsonAnySetter
    @Getter(onMethod_=@JsonIgnore)
    private Map<String, Object> paramMap = new HashMap<>();

    /**
     * 字表
     */
    @TableField(exist = false)
    private List<BaseModel> subTable;

    public Map<String, Object> toMap() {
        try {
            final String jsonStr = MAPPER.writeValueAsString(this);
            final BaseModel baseModel = MAPPER.readValue(jsonStr, BaseModel.class);
            return  baseModel.paramMap;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>(1);
        }
    }

}
