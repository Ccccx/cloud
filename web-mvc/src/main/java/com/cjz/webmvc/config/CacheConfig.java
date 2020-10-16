package com.cjz.webmvc.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 开发环境不启用缓存，方便测试
 * @author chengjz
 * @version 1.0
 * @since 2020-04-21 9:14
 */
@Slf4j
@Configuration
@Profile({"default"})
@EnableCaching(proxyTargetClass = true)
public class CacheConfig {

	/**
	 * 构建spring boot cache 管理类
	 *  <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-caching>Spring  Caching</a>
	 * @return ig
	 */
	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		ArrayList<CaffeineCache> caches = Lists.newArrayList();
		caches.add(buildCaffeineCache("web-mvc"));
		cacheManager.setCaches(caches);
		return cacheManager;
	}


	@Bean
	public KeyGenerator simpleKeyGenerator() {
		return (target,  method,  params)  -> {
			StringBuilder sb = new StringBuilder();
			sb.append(target.getClass().getSimpleName())
					.append("_")
					.append(method.getName())
					.append("_");
			for (Object param : params) {
				if (param == null) {
					sb.append("null");
				} else {
					sb.append(param.toString());
				}
			}
			final String cacheKey = sb.toString();
			log.debug("cache key : {}", cacheKey);
			return cacheKey;
		};
	}

	private CaffeineCache buildCaffeineCache(String cacheName) {
		return new CaffeineCache(cacheName,
				Caffeine.newBuilder().recordStats()
						.expireAfterWrite(1, TimeUnit.MINUTES)
						.maximumSize(1000)
						.build());
	}
}
