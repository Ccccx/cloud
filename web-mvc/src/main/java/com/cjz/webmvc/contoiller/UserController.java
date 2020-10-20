package com.cjz.webmvc.contoiller;

import com.cjz.webmvc.component.UserComponent;
import com.cjz.webmvc.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-20 11:29
 */
@RestController
@RequestMapping("/user")
public class UserController {

	private final UserComponent userComponent;

	public UserController(UserComponent userComponent) {
		this.userComponent = userComponent;
	}

	@GetMapping
	public User get(Principal principal) {
		return new User(principal.getName());
	}

	@GetMapping("listUser")
	public List<User> listUser() {
		return userComponent.getAllUsers();
	}
}
