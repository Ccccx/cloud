package com.example.log4j2test.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        final Map<String, String> additionalFieldsMap = new LinkedHashMap<>();
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


}