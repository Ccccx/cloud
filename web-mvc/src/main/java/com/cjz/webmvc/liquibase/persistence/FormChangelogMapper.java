package com.cjz.webmvc.liquibase.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjz.webmvc.liquibase.persistence.model.FormChangelog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author chengjz
 * @since 2020-09-09
 */
@Mapper
public interface FormChangelogMapper extends BaseMapper<FormChangelog> {

}
