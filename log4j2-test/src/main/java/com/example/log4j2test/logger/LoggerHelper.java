package com.example.log4j2test.logger;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-23 15:21
 */
public class LoggerHelper {

    public static final int KV_FLAG = 2;

    private LoggerHelper() {
    }

    /**
     * 添加自定义字段
     *
     * @param key   字段
     * @param value 值
     */
    public static void put(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        MDC.put(key, value);
    }

    /**
     * 变长键值对，必须是偶数个参数，否则最后一个参数会被丢弃
     *
     * @param kvPairs k v 数组
     */
    public static void put(String... kvPairs) {
        if (kvPairs == null || kvPairs.length < KV_FLAG) {
            return;
        }
        for (int i = 0; i < kvPairs.length; i += KV_FLAG) {
            if (i == kvPairs.length - 1) {
                break;
            }
            put(kvPairs[i], kvPairs[i + 1]);
        }
    }

    /**
     * 添加自定义字段
     *
     * @param data mp形式
     */
    public static void put(Map<String, String> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        data.forEach(LoggerHelper::put);
    }

    /**
     * 读取自定义字段
     *
     * @param key key
     * @return 字符串
     */
    public static String get(String key) {
        if (key == null) {
            return null;
        }
        return MDC.get(key);
    }

    /**
     * 删除自定义字段
     *
     * @param keys key 列表
     */
    public static void remove(String... keys) {
        if (keys == null) {
            return;
        }
        Stream.of(keys).forEach(MDC::remove);
    }

    /**
     * 向MDC中添加字段（不存在才添加）
     *
     * @param key   字段
     * @param value 值
     */
    public static void putIfAbsent(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        if (MDC.get(key) == null) {
            MDC.put(key, value);
        }
    }

    /**
     * 判断类型是否基本类型，含：空值、包装类型、字符串
     *
     * @param type 类型
     * @return ig
     */
    public static boolean isPrimitive(Class<?> type) {
        return type == null || type.isPrimitive() || type.equals(String.class);
    }

}
