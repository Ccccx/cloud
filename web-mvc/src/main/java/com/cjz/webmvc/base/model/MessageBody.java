package com.cjz.webmvc.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 18:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageBody {
    private String userName;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 广播转发的目标地址（告知 STOMP 代理转发到哪个地方）
     */
    private String destination;
}
