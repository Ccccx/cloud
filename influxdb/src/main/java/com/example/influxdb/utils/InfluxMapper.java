package com.example.influxdb.utils;

import com.google.common.collect.Maps;
import com.influxdb.annotations.Column;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-30 14:15
 */
@Slf4j
public class InfluxMapper {
    private static final ConcurrentHashMap<String, BeanDefinition> CACHE = new ConcurrentHashMap<>();
    private static final String FIELD = "_field";
    private static final String VALUE = "_value";

    private InfluxMapper() {
    }

    @Nonnull
    public static <T> List<T> toPojo(List<FluxTable> list, @Nonnull Class<T> clazz) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        final BeanDefinition definition = resolveBeanDefinition(clazz);
        Map<String, T> instances = new LinkedHashMap<>();
        for (FluxTable fluxTable : list) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                getInstanceByRecord(definition, clazz, instances, fluxRecord);
            }
        }
        return new LinkedList<>(instances.values());
    }

    private static <T> void getInstanceByRecord(@Nonnull BeanDefinition definition,
                                                @Nonnull Class<T> clazz,
                                                @Nonnull Map<String, T> instances,
                                                @Nonnull FluxRecord fluxRecord) {
        final Instant time = fluxRecord.getTime();
        final Map<String, Object> values = fluxRecord.getValues();
        T bean = instances.computeIfAbsent(buildKey(definition, fluxRecord), key -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        if (bean != null) {
            BeanMap beanMap = definition.getBeanMap();
            beanMap.setBean(bean);
            if (beanMap.get(definition.getTime()) == null) {
                beanMap.put(definition.getTime(), time);
                definition.getTags().forEach((key, value) -> {
                    if (values.containsKey(key)) {
                        Class<?> fieldType = beanMap.getPropertyType(value);
                        beanMap.put(key, TypeConverter.convert(values.get(key), fieldType));
                    }
                });
            }

            Object actual = fluxRecord.getValueByKey(FIELD);
            if (actual != null) {
                Map<String, String> fields = definition.getFields();
                String key = (String) actual;
                if (fields.containsKey(key)) {
                    String field = fields.get(key);
                    Class<?> fieldType = beanMap.getPropertyType(field);
                    beanMap.put(field, TypeConverter.convert(values.get(VALUE), fieldType));
                }
            }
        }
    }

    private static String buildKey(BeanDefinition definition, FluxRecord fluxRecord) {
        final StringBuilder sb = new StringBuilder(fluxRecord.getTime().toString());
        final Map<String, Object> values = fluxRecord.getValues();
        definition.getTags().forEach((key, value) -> {
            if (values.containsKey(key)) {
                sb.append("_").append(values.get(key));
            }
        });
        return sb.toString();
    }

    private static <T> BeanDefinition resolveBeanDefinition(Class<T> clazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final String qualifyName = clazz.getName();
        if (CACHE.containsKey(qualifyName)) {
            return CACHE.get(qualifyName);
        }
        final Field[] declaredFields = clazz.getDeclaredFields();
        String time = null;
        Map<String, String> tags1 = new LinkedHashMap<>(16);
        Map<String, String> fields1 = new LinkedHashMap<>(16);
        for (Field field : declaredFields) {
            Column annotation = AnnotationUtils.getAnnotation(field, Column.class);
            String name = field.getName();
            String value = field.getName();
            if (annotation != null) {
                if (StringUtils.isNotBlank(annotation.name())) {
                    name = annotation.name();
                }
                if (annotation.timestamp()) {
                    time = name;
                } else if (annotation.tag()) {
                    tags1.put(name, value);
                }
            }
            fields1.put(name, value);
        }

        if (StringUtils.isBlank(time)) {
            throw new UnsupportedOperationException("time not yet defined. class: " + qualifyName);
        }
        Map<String, String> tags = Maps.newHashMapWithExpectedSize(tags1.size());
        Map<String, String> fields = Maps.newHashMapWithExpectedSize(fields1.size());
        tags.putAll(tags1);
        fields.putAll(fields1);
        BeanDefinition definition = new BeanDefinition();
        definition.setTime(time);
        definition.setTags(tags);
        definition.setFields(fields);
        Object bean = clazz.getDeclaredConstructor().newInstance();
        definition.setBeanMap(BeanMap.create(bean));
        CACHE.put(qualifyName, definition);
        return definition;
    }


    @Getter
    @Setter
    private static class BeanDefinition {
        private Map<String, String> tags;
        private Map<String, String> fields;
        private String time;
        private BeanMap beanMap;
    }
}
