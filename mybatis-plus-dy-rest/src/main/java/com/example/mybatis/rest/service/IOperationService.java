package com.example.mybatis.rest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.support.StringMap;

import java.util.Collections;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-05 18:18
 */
public interface IOperationService <T extends BaseModel>{

    /**
     * 分页擦洗
     * @param req 请求参数
     * @return 分页数据
     */
    default  IPage<T> pageQuery(StringMap req) {
        return new Page<>(1, 10, 0);
    }

    /**
     * 查询列表数据
     * @param req 查询参数
     * @return 列表数据
     */
    default List<T> list(StringMap req) {
        return Collections.emptyList();
    }

    /**
     * 批量保存
     * @param req 请求参数
     * @return  结果
     */
    default List<T> batchSave(List<StringMap> req) {
        return Collections.emptyList();
    }

    /**
     * 批量更新
     * @param req 请求参数
     * @return  结果
     */
    default List<T> batchUpdate(List<StringMap> req) {
        return Collections.emptyList();
    }

    /**
     * 批量删除
     * @param req 请求参数
     */
    default void batchDelete(List<String> req) {
        return;
    }
}
