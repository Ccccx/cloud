package com.cjz.webmvc.user.controller;


import com.cjz.webmvc.base.model.User;
import com.cjz.webmvc.user.service.UserComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author chengjz
 * @since 2020-10-21
 */
@RestController
@RequestMapping("/user")
@Api(tags = {"user"})
@Slf4j
public class UsersController {

    private final UserComponent userComponent;

    @Autowired
    private User user;

    public UsersController(UserComponent userComponent) {
        this.userComponent = userComponent;
    }

    @GetMapping
    public User get(Principal principal) {
        return new User(principal.getName());
    }

    @GetMapping("/request")
    @ApiOperation(value = "查询requestScope用户")
    public List<String> requestScope() {
        final String dateTime = DateFormatUtils.format(new Date(), "YYYY-MM-DD HH:mm:ss");
        log.info("Date Now: {}", dateTime);
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        // 用户名/账户
        String username = authentication.getName();
        // 当前用户，一般是UserDetail的一个实现
        Object principal = authentication.getPrincipal();
        // 密码信息，验证成功后会清除
        Object credentials = authentication.getCredentials();
        // 角色权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> list = new ArrayList<>();
        list.add(user.getUserName());
        list.add(getUser().getUserName());
        return list;
    }

    @GetMapping("listUser")
    public List<User> listUser() {
        return userComponent.getAllUsers();
    }

    @Lookup
    public User getUser() {
        return null;
    }
}
