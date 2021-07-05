package com.example.factorydemo.yml;

import com.baomidou.mybatisplus.extension.api.R;
import com.tiamaes.cloud.logger.OperationLoggerProperties.Kafka;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import sun.net.util.URLUtil;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-06-28 15:03
 */
@Slf4j
class YmlTest {

    @Test
    void t1() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl("jdbc:mysql://192.168.231.11:3306/nvs?serverTimezone=GMT%2B8&autoReconnect=true");
        dataSourceProperties.setUsername("root");
        dataSourceProperties.setPassword("root");

//        KafkaProperties kafkaProperties = new KafkaProperties();
//        List<String> brokers = new ArrayList<>();
//        brokers.add("127.0.0.1:8080");
//        brokers.add("127.0.0.1:8081");
//        brokers.add("127.0.0.1:8082");
//        kafkaProperties.setBootstrapServers(brokers);

        RedisProperties   redisProperties = new RedisProperties();
        redisProperties.setHost("127.0.0.1");
        redisProperties.setPort(6339);

        Map<String, Object> mc = new LinkedHashMap<>();
        Map<String, Object> component = new LinkedHashMap<>();
        mc.put("m1", component);
        component.put("mysql1", dataSourceProperties);
        // component.put("kafka1", kafkaProperties);
        component.put("redis1", redisProperties);

        SkipNullRepresenter representer = new SkipNullRepresenter();
        representer.addClassTag(DataSourceProperties.class, Tag.MAP);
        representer.addClassTag(KafkaProperties.class, Tag.MAP);
        representer.addClassTag(RedisProperties.class, Tag.MAP);

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        final Yaml yaml = new Yaml(representer, options);
        String content =  yaml.dump(mc);
        log.info("\n{}", content);
    }

    @Test
    void t2() {
        URI uri = URI.create("http://127.0.0.1");
        log.info("{} {}", uri.getHost(), uri.getPort());
    }

    public static class SkipNullRepresenter extends Representer {

        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                      Object propertyValue, Tag customTag) {
            if (propertyValue == null) {
                return null;
            } else {
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    }
}
