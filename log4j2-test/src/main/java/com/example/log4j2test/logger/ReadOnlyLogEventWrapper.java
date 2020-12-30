package com.example.log4j2test.logger;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-23 17:49
 */
@Getter
public class ReadOnlyLogEventWrapper {
    /**
     * 项目名
     */
    private final String appName;

    /**
     * 时间
     */
    @JsonProperty("@timestamp")
    private final String timestamp;

    /**
     * 版本号
     */
    @JsonProperty("@version")
    private final String version;

    /**
     * 消息
     */
    private final String message;

    /**
     * 扩展字段
     */
    @JsonIgnore
    private final Map<String, String> additionalFieldsMap;

    @JsonIgnore
    private final LogEvent event;

    public ReadOnlyLogEventWrapper(String appName, String version, String message, LogEvent event, Map<String, String> additionalFieldsMap) {
        this.appName = appName;
        this.version = version;
        this.message = message;
        this.event = event;
        this.timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimeMillis()), ZoneOffset.of("+8")).toString();
        this.additionalFieldsMap = additionalFieldsMap;
    }

    public LogEvent toImmutable() {
        return event.toImmutable();
    }

    public Map<String, String> getContextData() {
        return event.getContextData().toMap();
    }

    public ThreadContext.ContextStack getContextStack() {
        return event.getContextStack();
    }

    public Level getLevel() {
        return event.getLevel();
    }

    public String getLoggerName() {
        return event.getLoggerName();
    }

    public Marker getMarker() {
        return event.getMarker();
    }

    public String getThreadName() {
        return event.getThreadName();
    }

    public long getThreadId() {
        return event.getThreadId();
    }

    public int getThreadPriority() {
        return event.getThreadPriority();
    }

    public Throwable getThrown() {
        return event.getThrown();
    }

    @JsonAnyGetter
    public Map<String, String> getAdditionalFieldsMap() {
        return additionalFieldsMap;
    }

    @Override
    public String toString() {
        try {
            return CustomJsonPatternLayout.jsonMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
