package com.example.log4j2test.logger;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-09 9:25
 */
public enum LoggerConfig {
    /**
     * 应用名
     */
    NAME("logstash.app.name:" + elWithDefault("spring.application.name")),
    /**
     * 应用版本
     */
    ENABLE("logstash.enable"),
    /**
     * 应用版本
     */
    VERSION("logstash.app.version"),
    /**
     * 开发人员
     */
    AUTHOR("logstash.app.author"),
    /**
     * logstah地址
     */
    LOGGER_HOST("logstash.logger.host"),
    /**
     * logstash端口
     */
    LOGGER_PORT("logstash.logger.port"),
    /**
     * 忽略网卡名
     */
    IGNORED_INTERFACES("logstash.inetutils.ignoredInterfaces:" + el("spring.cloud.inetutils.ignoredInterfaces"));

    private final String key;

    LoggerConfig(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String el() {
        return "${" + key + "}";
    }

    public static String el(String key) {
        return "${" + key + "}";
    }

    public String elWithDefault() {
        return elWith("default");
    }

    public String elWith(String defaultKey) {
        return "${" + key + ":" + defaultKey + "}";
    }

    public static String elWithDefault(String key) {
        return elWith(key, "default");
    }

    public static String elWith(String key, String defaultKey) {
        return "${" + key + ":" + defaultKey + "}";
    }
}
