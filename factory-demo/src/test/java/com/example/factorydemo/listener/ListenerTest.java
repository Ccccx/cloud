package com.example.factorydemo.listener;

import com.example.factorydemo.bean.Foo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.TimeUnit;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-08 19:05
 */
@Slf4j
@Configuration
class ListenerTest {

	@Test
	@SneakyThrows
	void t1() {
		final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ListenerTest.class);
		log.info("flag : true ----");
		final Foo foo = new Foo();
		foo.setFlag(true);
		applicationContext.publishEvent(foo);
		log.info("flag : false ----");
		foo.setFlag(false);
		applicationContext.publishEvent(foo);
		TimeUnit.SECONDS.sleep(3);
	}

	@EventListener(condition = "#foo.flag")
	public void handlerFooEvent(Foo foo) {
		log.info("{}", foo);

	}

}
