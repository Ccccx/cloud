package com.cjz.webmvc.user.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjz.webmvc.user.persistence.model.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author chengjz
 * @since 2020-10-21
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}
