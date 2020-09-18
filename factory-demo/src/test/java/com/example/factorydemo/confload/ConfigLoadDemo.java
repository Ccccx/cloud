package com.example.factorydemo.confload;

import com.example.factorydemo.bean.Foo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-07-16 9:58
 */
@Slf4j
@Configuration
public class ConfigLoadDemo {

	@Autowired
	private Foo foo;

	public static void main(String[] args) {
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(ConfigLoadDemo.class);
		context.refresh();
	}

	@PostConstruct
	public void init() {
		log.info("foo: {}", foo);
	}

	@Configuration
	@Slf4j
	public static class FooConfig {
		@Bean
		@Scope("prototype")
		public Foo foo() {
			log.info("foo init ...");
			return new Foo("cjz", true, 18);
		}
	}
}
