package com.example.log4j2test.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * -Dorg.apache.logging.log4j.simplelog.StatusLogger.level=TRACE  开启日志调试
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-12-24 10:55
 */
class Log4j2Test {

    protected final static Logger logger = StatusLogger.getLogger();

    private static void setSystemProperty(String name, String value) {
        if (System.getProperty(name) == null && value != null) {
            System.setProperty(name, value);
        }
    }

    @Test
    void t1() throws InterruptedException {

        final ClassPathResource classPathResource = new ClassPathResource("META-INF/app.properties");
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

        MDC.put("trace_id", "l11111");
        MDC.put("request_id", "R2323");
        MDC.put("request_uri", "/TEST");
        MDC.put("method", "GET");
        MyApp.test();
    }

    @Test
    void t2() throws JsonProcessingException {
        System.out.println(new Date());
        final TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        System.out.println(timeZone.getID());
        System.out.println(LocalDateTime.now(ZoneOffset.of("+8")));
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.of("+8"));
        System.out.println(localDateTime.toString());

        final ObjectMapper objectMapper = new ObjectMapper();
    }

    @Test
    void t3() throws Exception {
        final Map<String, String> env = System.getenv();
        final Properties properties = System.getProperties();
        System.out.println("----------");

    }

    @Test
    void t4() {

    }

    public static class MyApp {

        // Define a static logger variable so that it references the
        // Logger instance named "MyApp".
        private static final Logger logger = LogManager.getLogger(MyApp.class);

        public static void test() throws InterruptedException {
            // Set up a simple configuration that logs on the console.
            logger.trace("Entering application.");
            Bar bar = new Bar();
            if (!bar.doIt()) {
                logger.error("Didn't do it.");
            }
            logger.trace("Exiting application.");
        }
    }

    public static class Bar {
        static final Logger logger = LogManager.getLogger(Bar.class.getName());

        public boolean doIt() throws InterruptedException {

            logger.entry();
            for (int i = 0; i < 10; i++) {
                logger.error("Did it again!");
                TimeUnit.SECONDS.sleep(1);
            }
            return logger.exit(false);
        }
    }
}
