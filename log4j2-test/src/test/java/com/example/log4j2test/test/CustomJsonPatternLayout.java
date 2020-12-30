package com.example.log4j2test.test;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.layout.PatternSelector;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.core.util.KeyValuePair;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-24 10:55
 */
@Plugin(name = "CustomJsonPatternLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public class CustomJsonPatternLayout extends AbstractStringLayout {

    protected static ObjectMapper jsonMapper = new ObjectMapper();
    final ResolvableKeyValuePair[] additionalFields;
    private final PatternLayout patternLayout;
    private final String appName;
    private final String version;

    private CustomJsonPatternLayout(Configuration config, RegexReplacement replace, String eventPattern,
                                    PatternSelector patternSelector, Charset charset, boolean alwaysWriteExceptions,
                                    boolean noConsoleNoAnsi, String headerPattern, String footerPattern, String appName, String version, KeyValuePair[] additionalFields) {
        super(config, charset,
                PatternLayout.newSerializerBuilder().setConfiguration(config).setReplace(replace).setDefaultPattern(headerPattern).setPatternSelector(patternSelector).setAlwaysWriteExceptions(alwaysWriteExceptions).setNoConsoleNoAnsi(noConsoleNoAnsi).build(),
                PatternLayout.newSerializerBuilder().setConfiguration(config).setReplace(replace).setDefaultPattern(footerPattern).setPatternSelector(patternSelector).setAlwaysWriteExceptions(alwaysWriteExceptions).setNoConsoleNoAnsi(noConsoleNoAnsi).build());

        this.appName = appName;
        this.version = version;
        this.additionalFields = prepareAdditionalFields(config, additionalFields);
        this.patternLayout = PatternLayout.newBuilder()
                .withPattern(eventPattern)
                .withPatternSelector(patternSelector)
                .withConfiguration(config)
                .withRegexReplacement(replace)
                .withCharset(charset)
                .withAlwaysWriteExceptions(alwaysWriteExceptions)
                .withNoConsoleNoAnsi(noConsoleNoAnsi)
                .withHeader(headerPattern)
                .withFooter(footerPattern)
                .build();
    }

    private static ResolvableKeyValuePair[] prepareAdditionalFields(final Configuration config, final KeyValuePair[] additionalFields) {
        if (additionalFields == null || additionalFields.length == 0) {
            // No fields set
            return new ResolvableKeyValuePair[0];
        }

        // Convert to specific class which already determines whether values needs lookup during serialization
        final ResolvableKeyValuePair[] resolvableFields = new ResolvableKeyValuePair[additionalFields.length];

        for (int i = 0; i < additionalFields.length; i++) {
            final ResolvableKeyValuePair resolvable = resolvableFields[i] = new ResolvableKeyValuePair(additionalFields[i]);

            // Validate
            if (config == null && resolvable.valueNeedsLookup) {
                throw new IllegalArgumentException("configuration needs to be set when there are additional fields with variables");
            }
        }

        return resolvableFields;
    }

    @PluginFactory
    public static CustomJsonPatternLayout createLayout(
            @PluginAttribute(value = "pattern", defaultString = PatternLayout.DEFAULT_CONVERSION_PATTERN) final String pattern,
            @PluginElement("PatternSelector") final PatternSelector patternSelector,
            @PluginConfiguration final Configuration config,
            @PluginElement("Replace") final RegexReplacement replace,
            @PluginAttribute(value = "charset") final Charset charset,
            @PluginAttribute(value = "alwaysWriteExceptions", defaultBoolean = true) final boolean alwaysWriteExceptions,
            @PluginAttribute(value = "noConsoleNoAnsi", defaultBoolean = false) final boolean noConsoleNoAnsi,
            @PluginAttribute("header") final String headerPattern,
            @PluginAttribute("footer") final String footerPattern,
            @PluginAttribute("appName") final String appName,
            @PluginAttribute("version") final String version,
            @PluginElement("KeyValuePair") final KeyValuePair[] additionalFields) {
        return new CustomJsonPatternLayout(config, replace, pattern, patternSelector, charset,
                alwaysWriteExceptions, noConsoleNoAnsi, headerPattern, footerPattern, appName, version, additionalFields);
    }

    protected static boolean valueNeedsLookup(final String value) {
        return value != null && value.contains("${");
    }

    @Override
    public String toSerializable(LogEvent event) {
        final StrSubstitutor strSubstitutor = configuration.getStrSubstitutor();
        final Map<String, String> additionalFieldsMap = new LinkedHashMap<>(additionalFields.length);
        for (final ResolvableKeyValuePair pair : additionalFields) {
            if (pair.valueNeedsLookup) {
                // Resolve value
                additionalFieldsMap.put(pair.key, strSubstitutor.replace(event, pair.value));
            } else {
                // Plain text value
                additionalFieldsMap.put(pair.key, pair.value);
            }
        }
        final ReadOnlyLogEventWrapper loggerInfo = new ReadOnlyLogEventWrapper(strSubstitutor.replace(event, appName), strSubstitutor.replace(event, version), patternLayout.toSerializable(event), event, additionalFieldsMap);
        String jsonStr = loggerInfo.toString();
        return jsonStr + "\n";
    }

    protected static class ResolvableKeyValuePair {

        final String key;
        final String value;
        final boolean valueNeedsLookup;

        ResolvableKeyValuePair(final KeyValuePair pair) {
            this.key = pair.getKey();
            this.value = pair.getValue();
            this.valueNeedsLookup = valueNeedsLookup(this.value);
        }
    }

    /**
     * 输出格式
     */
    @Getter
    public static class ReadOnlyLogEventWrapper {
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
                return jsonMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}