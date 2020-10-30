package com.cjz.webmvc.user.controller;


import com.cjz.webmvc.base.model.User;
import com.cjz.webmvc.user.service.UserComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
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
