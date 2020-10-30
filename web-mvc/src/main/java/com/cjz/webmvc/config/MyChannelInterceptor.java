package com.cjz.webmvc.config;

import com.cjz.webmvc.user.service.UserComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 19:04
 */

@Slf4j
@Component
public class MyChannelInterceptor implements ChannelInterceptor {

	@Autowired
	@Lazy
	private UserComponent userComponent;

	/**
	 * 从 Header 中获取 Token 进行验证，根据不同的 Token 区别用户
	 *
	 * @param message 消息对象
	 * @param channel 通道对象
	 * @return 验证后的用户信息
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		log.info("preSend ...");
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor == null) {
			return message;
		}
		final Principal user = accessor.getUser();
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			log.info(" {} -> 初次建立连接 ", user.getName());
			userComponent.addUser(user);
		} else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
			log.info("{} <- 断开连接", user.getName());
			userComponent.removeUser(user);
		}
		return message;
	}
}
