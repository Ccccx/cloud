package com.example.mybatis.rest.extend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-22 17:21
 */
public interface CommonMapper<T> extends BaseMapper<T> {
    /**
     * 插入全部
     * @param lists 结果
     * @return  ig
     */
    int insertAll(List<T>lists);

    /**
     * mysql 形式的全字段批量新增
     *
     * @param entity 数据
     * @return  ig
     */
    int mysqlInsertAllBatch(List<T> entity);


    /**
     * oracle 形式的全字段批量新增
     *
     * @param entity 数据
     * @return  ig
     */
    int oracleInsertAllBatch(List<T> entity);
}
