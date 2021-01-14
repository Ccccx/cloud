package com.example.log4j2test.logger;

import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.SocketAppender;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.yaml.YamlConfiguration;
import org.apache.logging.log4j.core.filter.ThreadContextMapFilter;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.core.util.KeyValuePair;

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
@Plugin(name = "Log4j2ConfigurationFactory", category = "ConfigurationFactory")
public class Log4j2ConfigurationFactory extends ConfigurationFactory {

    /**
     * Valid file extensions for XML files.
     */
    public static final String[] SUFFIXES = new String[]{".yml"};

    @Override
    protected String[] getSupportedTypes() {
        return SUFFIXES;
    }


    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        return new LogstashYamlConfiguration(loggerContext, source);
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
            list.add(new KeyValuePair("host", "${sys:logstash.app.ip:-unknow_app_ip}"));
            list.add(new KeyValuePair("trace_id", "${ctx:trace_id:-unknow_trace_id}"));
            list.add(new KeyValuePair("author", "${sys:logstash.app.author:-unknow_app_author}"));
            list.add(new KeyValuePair("request_id", "${ctx:request_id:-unknow_request_id}"));
            list.add(new KeyValuePair("request_uri", "${ctx:request_uri:-unknow_request_uri}"));
            list.add(new KeyValuePair("request_method", "${ctx:request_method:-unknow_request_method}"));
            final CustomJsonPatternLayout jsonPatternLayout = CustomJsonPatternLayout.createLayout(null, null, new DefaultConfiguration(), null, StandardCharsets.UTF_8, true, false,
                    "[", "]", "${sys:logstash.app.name:-unknow_app_name}", "${sys:logstash.app.version:-unknow_app_version}", list.toArray(new KeyValuePair[0]));

            final SocketAppender socketAppender = new SocketAppender.Builder().setName("Logstash-Appender").setLayout(jsonPatternLayout).withHost("192.168.250.80").withPort(5045).withProtocol(Protocol.TCP).withConnectTimeoutMillis(5 * 60 * 1000).withReconnectDelayMillis(5 * 1000).withBufferSize(8192).build();
            socketAppender.start();
            addAppender(socketAppender);
            getRootLogger().addAppender(socketAppender, null, null);
        }
    }


}
