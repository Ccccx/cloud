package com.example.factorydemo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-04-07 19:14
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foo {
	private String name;
	private boolean flag;
	private Integer age;

	@PostConstruct
	public void init() {
		log.info("前置方法调用 ...");
	}

	@PreDestroy
	public void destroy() {
		log.info("销毁方法调用 ...");
	}
}
