package com.cjz.webmvc.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjz.webmvc.user.persistence.UsersMapper;
import com.cjz.webmvc.user.persistence.model.Users;
import com.cjz.webmvc.user.service.IUsersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author chengjz
 * @since 2020-10-21
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

}
