package com.example.log4j2test.logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtils.HostInfo;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import java.util.Arrays;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-24 19:17
 */
public class LoggerEnvConfigListener implements GenericApplicationListener, Ordered {

	private static final Class<?> CLAZ = ApplicationEnvironmentPreparedEvent.class;
	public static final String BOOTSTRAP_PROPERTY_SOURCE_NAME = "bootstrap";
	public static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 50;

	@Override
	public int getOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	public boolean supportsEventType(ResolvableType eventType) {
		return CLAZ.isAssignableFrom(eventType.getRawClass());
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApplicationEnvironmentPreparedEvent) {
			final ApplicationEnvironmentPreparedEvent environmentPreparedEvent = (ApplicationEnvironmentPreparedEvent) event;
			final ConfigurableEnvironment environment = environmentPreparedEvent.getEnvironment();
			// don't listen to events in a bootstrap context
			if (environment.getPropertySources().contains(BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
				return;
			}
			PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(environment.getPropertySources());
			resolver.setIgnoreUnresolvableNestedPlaceholders(true);

			final String appName = resolver.resolvePlaceholders(LoggerConfig.NAME.el());
			final String version = resolver.resolvePlaceholders(LoggerConfig.VERSION.elWithDefault());
			final String author = resolver.resolvePlaceholders(LoggerConfig.AUTHOR.elWithDefault());
			setSystemProperty("logstash.app.name", appName);
			setSystemProperty("logstash.app.version", version);
			setSystemProperty("logstash.app.author", author);
			String ignoredInterfaces = resolver.resolvePlaceholders(LoggerConfig.IGNORED_INTERFACES.el());
			final InetUtilsProperties properties = new InetUtilsProperties();
			if (StringUtils.isNotEmpty(ignoredInterfaces)) {
				properties.setIgnoredInterfaces(Arrays.asList(ignoredInterfaces.split(",")));
			}
			try (InetUtils inetUtils = new InetUtils(properties)) {
				HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo();
				setSystemProperty("logstash.app.ip", hostInfo.getIpAddress());
			}
		}
	}


	private void setSystemProperty(String name, String value) {
		if (StringUtils.isEmpty(System.getProperty(name)) && StringUtils.isNotEmpty(value)) {
			System.setProperty(name, value);
		}
	}
}
