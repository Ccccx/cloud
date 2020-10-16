package com.cjz.webmvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 18:22
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * 配置 WebSocket 进入点，及开启使用 SockJS，这些配置主要用配置连接端点，用于 WebSocket 连接
	 *
	 * @param registry STOMP 端点
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/gs-guide-websocket").withSockJS();
	}

	/**
	 * 配置消息代理选项
	 *
	 * @param registry 消息代理注册配置
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 设置一个或者多个代理前缀，在 Controller 类中的方法里面发生的消息，会首先转发到代理从而发送到对应广播或者队列中。
		// 客户端订阅信息前缀
		registry.enableSimpleBroker("/topic");
		// 配置客户端发送请求消息的一个或多个前缀，该前缀会筛选消息目标转发到 Controller 类中注解对应的方法里
		// 服务端接收信息前缀
		registry.setApplicationDestinationPrefixes("/app");
		// 服务端通知特定用户客户端的前缀，可以不设置，默认为user
		// 通知用户前缀
		registry.setUserDestinationPrefix("/user");
	}


}
