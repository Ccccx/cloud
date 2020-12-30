package com.cjz.webmvc.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cjz.webmvc.user.persistence.model.Users;
import com.cjz.webmvc.user.service.IUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-26 15:46
 */
@Slf4j
@Component
public class AppRunner implements ApplicationRunner {

    @Resource
    private IUsersService usersService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final Users users = new Users();
        users.setUsername(UUID.randomUUID().toString().replace("-", ""));
        users.setCreateDate(new Date());
        users.setPassword("123456");
        users.setNickname(users.getUsername());
        users.insert();
        log.info("User insert : {}", users);

        final LambdaQueryWrapper<Users> queryWrapper = Wrappers.lambdaQuery(Users.class);
        queryWrapper.eq(Users::getUsername, users.getUsername());

        final List<Users> list = usersService.list(queryWrapper);
        list.forEach(System.out::println);
    }
}
