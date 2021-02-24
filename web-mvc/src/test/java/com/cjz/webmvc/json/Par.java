package com.cjz.webmvc.json;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-19 11:37
 */
 @Data
  class Par {
    /**
     * 项目名
     */
    private  String appName;

    /**
     * 时间
     */
    @JsonProperty("@timestamp")
    // @JsonSerialize
    // @JsonDeserialize
    private  String timestamp;

    /**
     * 版本号
     */
    @JsonProperty("@version")
    private  String version;

    /**
     * 消息
     */
    private  String message;
    /**
     * 扩展字段
     */
   //  @JsonIgnore
   @JsonAnySetter
    private  Map<String, String> extendMap = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> getExtendMap() {
        return extendMap;
    }
}
