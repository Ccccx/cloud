package com.cjz.webmvc.base.contoiller;

import com.cjz.webmvc.base.model.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 18:23
 */
@Controller
@Slf4j
public class MessageController {


    /**
     * 消息发送工具对象
     */
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Principal : 这里能获取在上面拦截器中我们自己setUser的对象
     * SimpMessageHeaderAccessor : 更详情的一些信息，包括 sessionId,消息id等
     * Payload 注解表示你要用来接收消息内容的对象是哪一个
     */
    @MessageMapping("/test")
    /**
     * 给用户发信息,默认前缀为/user
     */
    public MessageBody sendTopicMessage(Principal principal, @Payload MessageBody messageBody, SimpMessageHeaderAccessor accessor) {
        log.info("Principal: {}", principal);
        log.info("MessageBody: {}", messageBody);
        // 将消息发送到 WebSocket 配置类中配置的代理中（/topic）进行消息转发
        simpMessagingTemplate.convertAndSend(messageBody.getDestination(),
                new MessageBody(principal.getName(), messageBody.getContent(), "*"));
        // 给指定用户发信息
        simpMessagingTemplate.convertAndSendToUser(messageBody.getUserName(), "/queue/userMsg", new MessageBody(principal.getName(), messageBody.getContent(), "*"));
        return messageBody;
    }

}
