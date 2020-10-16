package com.cjz.webmvc.model;

import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 18:21
 */
@Data
public class MessageBody {
	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * 广播转发的目标地址（告知 STOMP 代理转发到哪个地方）
	 */
	private String destination;
}
