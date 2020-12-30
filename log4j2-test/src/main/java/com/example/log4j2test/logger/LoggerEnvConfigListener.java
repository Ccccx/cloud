package com.example.log4j2test.logger;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-24 19:17
 */
public class LoggerEnvConfigListener implements GenericApplicationListener, Ordered {

    public static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 50;
    private static final Class<?> CLAZ = ApplicationEnvironmentPreparedEvent.class;

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
            PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(environment.getPropertySources());
            resolver.setIgnoreUnresolvableNestedPlaceholders(true);

            final ClassPathResource classPathResource = new ClassPathResource("META-INF/app.properties");
            if (classPathResource.isReadable()) {
                final PropertiesPropertySourceLoader sourceLoader = new PropertiesPropertySourceLoader();
                final List<PropertySource<?>> sources;
                try {
                    sources = sourceLoader.load("app", classPathResource);
                    for (PropertySource<?> source : sources) {
                        final OriginTrackedMapPropertySource propertySource = (OriginTrackedMapPropertySource) source;
                        setSystemProperty("logstash.app.name", (String) propertySource.getProperty("app.name"));
                        setSystemProperty("logstash.app.ip", (String) propertySource.getProperty("app.ip"));
                        setSystemProperty("logstash.app.version", (String) propertySource.getProperty("app.version"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                final String appName = resolver.resolvePlaceholders("${spring.application.name}");
                final String ip = resolver.resolvePlaceholders("${info.app.ip}");
                final String version = resolver.resolvePlaceholders("${info.app.version}");
                final String author = resolver.resolvePlaceholders("${info.app.author}");
                setSystemProperty("logstash.app.name", appName);
                setSystemProperty("logstash.app.ip", ip);
                setSystemProperty("logstash.app.version", version);
                setSystemProperty("logstash.app.author", author);
            }

        }
    }


    private void setSystemProperty(String name, String value) {
        if (System.getProperty(name) == null && value != null) {
            System.setProperty(name, value);
        }
    }
}
