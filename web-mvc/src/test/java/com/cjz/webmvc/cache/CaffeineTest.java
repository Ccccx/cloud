package com.cjz.webmvc.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine是一个基于Java 8的高性能、近乎最优的缓存库。要了解更多细节，请参阅我们的用户指南，并浏览最新版本的API文档。
 * https://github.com/ben-manes/caffeine
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-09-21 16:41
 */
public class CaffeineTest {
    @Test
    public void t1() throws InterruptedException {
        CacheLoader<String, String> loader = key -> key.toUpperCase();
        LoadingCache<String, String> caches = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                // .refreshAfterWrite(5, TimeUnit.SECONDS)
                .build(loader);
        // 值为null
        Assert.assertNull(caches.getIfPresent("hello"));
        caches.put("hello", "java");
        // 获取值
        Assert.assertEquals(caches.getIfPresent("hello"), "java");
        caches.put("hello", "C++");
        Assert.assertEquals(caches.getIfPresent("hello"), "C++");
        // 测试自动失效
        Thread.sleep(6 * 1000);
        Assert.assertNull(caches.getIfPresent("hello"));
    }
}
