package com.example.factory.config;

import com.example.factory.project.ChildGenerationConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:06
 */
@ChildGenerationConfiguration
@Slf4j
public class ChildCustomConfig {

	@PostConstruct
	public void init() {
		log.debug("ChildCustomConfig init ...");
	}

}
