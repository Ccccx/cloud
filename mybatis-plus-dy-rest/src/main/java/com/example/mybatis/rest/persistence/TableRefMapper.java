package com.example.mybatis.rest.persistence;

import com.example.mybatis.rest.persistence.model.TableRef;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 表字段关系 Mapper 接口
 * </p>
 *
 * @author chengjz
 * @since 2021-07-05
 */
@Mapper
public interface TableRefMapper extends BaseMapper<TableRef> {

}
