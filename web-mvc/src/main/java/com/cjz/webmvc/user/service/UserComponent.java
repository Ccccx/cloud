package com.cjz.webmvc.user.service;

import com.cjz.webmvc.base.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-20 11:51
 */
@Slf4j
@Component
public class UserComponent {
	public static final Map<String, User> ONL_USER = new ConcurrentHashMap<>();

	private final SimpMessageSendingOperations simpMessageSendingOperations;

	public UserComponent(SimpMessageSendingOperations simpMessageSendingOperations) {
		this.simpMessageSendingOperations = simpMessageSendingOperations;
	}


	public void addUser(Principal user) {
		ONL_USER.put(user.getName(), new User(user.getName()));
		simpMessageSendingOperations.convertAndSend("/topic/users", getAllUsers());
	}

	public void removeUser(Principal user) {
		ONL_USER.remove(user.getName());
		// simpMessageSendingOperations.convertAndSend("/topic/users", getAllUsers());
	}

	public void pushListUser() {
		log.info("pushListUser ...");
		simpMessageSendingOperations.convertAndSend("/topic/users", getAllUsers());
	}

	public List<User> getAllUsers() {
		return new LinkedList<>(ONL_USER.values());
	}
}
