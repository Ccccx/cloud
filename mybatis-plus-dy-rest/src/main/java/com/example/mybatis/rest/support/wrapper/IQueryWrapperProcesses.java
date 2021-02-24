package com.example.mybatis.rest.support.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.TableMetaConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-24 9:57
 */
public interface IQueryWrapperProcesses {

    /**
     * 判断是否能处理改参数
     * @param requestKey 请求参数的key
     * @return   true | false
     */
    default boolean isSupport(String requestKey) {
        return false;
    }

    /**
     * 处理请求
     * @param tableMetaConfig    表元信息
     * @param wrapper    查询wrapper
     * @param requestKey 请求key
     * @param requestVal 请求值
     */
   void doProcess(TableMetaConfig tableMetaConfig, QueryWrapper<BaseModel> wrapper, String requestKey, String requestVal);
}
