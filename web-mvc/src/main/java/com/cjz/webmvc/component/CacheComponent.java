package com.cjz.webmvc.component;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

	private final CacheManager cacheManager;

	public CacheComponent(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Cacheable
	public String t1() {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "休眠5秒结束!";
	}

	/**
	 * 手动使用缓存方式
	 *
	 * @return 结果
	 */
	public String t2() {
		String key = "t2";
		final Cache cache = cacheManager.getCache("web-mvc");
		final Cache.ValueWrapper t2 = cache.get(key);
		String result;
		if (t2 == null || t2.get() == null) {
			result = "t2休眠5秒结束";
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cache.put(key, result);
		} else {
			result = ((String) t2.get());
		}
		return result;
	}

}
