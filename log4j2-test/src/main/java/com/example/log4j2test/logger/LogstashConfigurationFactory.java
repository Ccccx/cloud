package com.example.log4j2test.logger;

import org.apache.commons.lang.ObjectUtils;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.SocketAppender;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.yaml.YamlConfiguration;
import org.apache.logging.log4j.core.filter.ThreadContextMapFilter;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-06 17:04
 */
@Order(20)
@Plugin(name = "LogstashConfigurationFactory", category = "ConfigurationFactory")
public class LogstashConfigurationFactory extends ConfigurationFactory {

    /**
     * Valid file extensions for XML files.
     */
    protected static final String[] SUFFIXES = new String[]{".yml"};

    @Override
    protected String[] getSupportedTypes() {
        return SUFFIXES;
    }

    protected static final List<PropertySource<?>> SOURCES = new ArrayList<>();

    static {
        final ClassPathResource classPathResource = new ClassPathResource("application.yml");
        if (classPathResource.isReadable()) {
            final YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();

            try {
                SOURCES.addAll(sourceLoader.load("application", classPathResource));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        final Object val = ObjectUtils.defaultIfNull(findVal(LoggerConfig.ENABLE.getKey()), false);
        if (Boolean.TRUE.equals(val)) {
            return new LogstashYamlConfiguration(loggerContext, source);
        }
        final ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        builder.setLoggerContext(loggerContext);
        builder.setConfigurationSource(source);
        return builder.build();
    }

    public static class LogstashYamlConfiguration extends YamlConfiguration {

        public LogstashYamlConfiguration(LoggerContext loggerContext, ConfigurationSource configSource) {
            super(loggerContext, configSource);
        }

        @Override
        protected void doConfigure() {
            super.doConfigure();
            // 添加过滤器
            final HashMap<String, List<String>> map = new HashMap<>(2);
            map.put("source", Collections.singletonList("fx"));
            final ThreadContextMapFilter fxFilter = new ThreadContextMapFilter(map, true, Result.DENY, Result.ACCEPT);
            getAppenders().values().stream().filter(i -> i instanceof ConsoleAppender).forEach(appender -> ((ConsoleAppender) appender).addFilter(fxFilter));

            // 添加SocketAppender
            List<KeyValuePair> list = new ArrayList<>();
            list.add(new KeyValuePair("host", "${sys:logstash.app.ip:-unknown_app_ip}"));
            list.add(new KeyValuePair("trace_id", "${ctx:trace_id:-unknown_trace_id}"));
            list.add(new KeyValuePair("request_id", "${ctx:request_id:-unknown_request_id}"));
            list.add(new KeyValuePair("request_uri", "${ctx:request_uri:-unknown_request_uri}"));
            list.add(new KeyValuePair("request_method", "${ctx:request_method:-unknown_request_method}"));

            // 添加自定义layout
            final CustomJsonPatternLayout jsonPatternLayout = CustomJsonPatternLayout.createLayout(null, null, new DefaultConfiguration(), null, StandardCharsets.UTF_8, true, false,
                    "[", "]", "${sys:logstash.app.name:-unknown_app_name}", "${sys:logstash.app.version:-unknown_app_version}", list.toArray(new KeyValuePair[0]));

            Object host = ObjectUtils.defaultIfNull(findVal(LoggerConfig.LOGGER_HOST.getKey()), "192.168.250.80");
            Object port = ObjectUtils.defaultIfNull(findVal(LoggerConfig.LOGGER_PORT.getKey()), "5045");
            final SocketAppender socketAppender = new SocketAppender.Builder().setName("Logstash-Appender").setLayout(jsonPatternLayout).withHost(((String) host)).withPort(((int) port)).withProtocol(Protocol.TCP).withConnectTimeoutMillis(5 * 60 * 1000).withReconnectDelayMillis(5 * 1000).withBufferSize(8192).build();
            socketAppender.start();
            this.addAppender(socketAppender);
            this.getRootLogger().addAppender(socketAppender, null, null);
        }
    }

    public static Object findVal(String key) {
        for (PropertySource<?> source : SOURCES) {
            if (source.getProperty(key) != null) {
                return source.getProperty(key);
            }
        }
        return null;
    }

}
