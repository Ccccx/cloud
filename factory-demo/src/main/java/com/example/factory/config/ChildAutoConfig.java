package com.example.factory.config;

import com.example.factory.project.model.ChildConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:06
 */
@Slf4j
public class ChildAutoConfig {

	@PostConstruct
	public void init() {
		log.debug("ChildAutoConfig init ...");
	}

	@Bean
	public ChildConfig childConfig() {
		log.debug("ChildConfig ...");
		return new ChildConfig();
	}

}
