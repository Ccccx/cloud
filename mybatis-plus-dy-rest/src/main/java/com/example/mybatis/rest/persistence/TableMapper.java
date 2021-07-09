package com.example.mybatis.rest.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatis.rest.persistence.model.Table;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 表元信息 Mapper 接口
 * </p>
 *
 * @author chengjz
 * @since 2021-07-07
 */
@Mapper
public interface TableMapper extends BaseMapper<Table> {

}
