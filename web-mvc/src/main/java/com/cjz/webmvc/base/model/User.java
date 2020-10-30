package com.cjz.webmvc.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 19:03
 */
@Data
@AllArgsConstructor
@Slf4j
public class User {
	private String userName;

	@PostConstruct
	public void init() {
		log.info("PostConstruct ...");
	}

	@PreDestroy
	public void destroy() {
		log.info("PreDestroy ...");
	}
}