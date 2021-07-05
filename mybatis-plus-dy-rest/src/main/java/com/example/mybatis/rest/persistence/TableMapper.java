package com.example.mybatis.rest.persistence;

import com.example.mybatis.rest.persistence.model.Table;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 表元信息 Mapper 接口
 * </p>
 *
 * @author chengjz
 * @since 2021-07-05
 */
@Mapper
public interface TableMapper extends BaseMapper<Table> {

}
