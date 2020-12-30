package com.cjz.webmvc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

/**
 * WebSocketHandlerDecorator
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 18:22
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Autowired
    private MyChannelInterceptor myChannelInterceptor;

    /**
     * 配置 WebSocket 进入点，及开启使用 SockJS，这些配置主要用配置连接端点，用于 WebSocket 连接
     *
     * @param registry STOMP 端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket")
                .addInterceptors(new HttpHandshakeInterceptor())
                .setHandshakeHandler(new HttpHandshakeHandler())
                .withSockJS()
                .setStreamBytesLimit(524288)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30000)
                .setSessionCookieNeeded(false);
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
        // 很重要 , 比如给指定用户发信息,订阅地址为/user/queue/userMsg
        registry.enableSimpleBroker("/topic", "/queue");
        // 配置客户端发送请求消息的一个或多个前缀，该前缀会筛选消息目标转发到 Controller 类中注解对应的方法里
        // 服务端接收信息前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 服务端通知特定用户客户端的前缀，可以不设置，默认为user
        // 通知用户前缀
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(myChannelInterceptor);
    }


    /**
     * 握手拦截器
     */
    @Slf4j
    public static class HttpHandshakeInterceptor implements HandshakeInterceptor {
        /**
         * 握手前拦截，从 HTTP 中参数传入 WebSocket Attributes 方便后续取出相关参数
         *
         * @param request    请求对象
         * @param response   响应对象
         * @param wsHandler  WebSocket 处理器
         * @param attributes 从 HTTP 握手到与 WebSocket 会话关联的属性
         * @return 如果返回 true 则继续握手，返回 false 则终止握手
         */
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) {
            // 将 request 对象转换为 ServletServerHttpRequest 对象
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            // 获取 HTTP Session 对象
            HttpSession session = serverRequest.getServletRequest().getSession();
            if (session != null) {
                // 从 HTTP Security 中获取用户信息
                SecurityContext context = SecurityContextHolder.getContext();
                Authentication authentication = context.getAuthentication();
                // 用户名
                String username = authentication.getName();
                if (StringUtils.isNotEmpty(username)) {
                    log.info("{} 准备建立Websocket链接", username);
                    // 将从 HTTP Session 中获取的用户信息存入 WebSocket 的 Attributes 对象中
                    attributes.put("user", authentication);
                    // 继续握手
                    return true;
                }

            }
            // 终止握手
            return false;
        }

        /**
         * 握手完成后调用
         *
         * @param request   请求对象
         * @param response  响应对象
         * @param wsHandler WebSocket 处理器
         * @param ex        异常信息
         */
        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception ex) {
            log.info("afterHandshake ...");
        }
    }

    /**
     * 握手处理处理器
     */
    static class HttpHandshakeHandler extends DefaultHandshakeHandler {
        /**
         * 用于与正在建立会话过程中的 WebSocket 的用户相关联的方法，可以在此处配置进行关联的用户信息。
         *
         * @param request    握手请求对象
         * @param wsHandler  WebSocket 处理器
         * @param attributes 从 HTTP 握手到与 WebSocket 会话关联的属性
         * @return 对于 WebSocket 的会话的用户或者 null
         */
        @Override
        protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
            // 获取握手时保存的信息
            Authentication user = (Authentication) attributes.get("user");
            return user::getName;
        }


    }
}
