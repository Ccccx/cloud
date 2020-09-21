package com.cjz.webmvc.component;

import ch.qos.logback.core.util.TimeUtil;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-21 14:39
 */
@Component
@CacheConfig(cacheNames = "web-mvc", keyGenerator = "simpleKeyGenerator")
public class CacheComponent {



	@Cacheable
	public String t1() {
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "休眠5秒结束!";
	}
}
