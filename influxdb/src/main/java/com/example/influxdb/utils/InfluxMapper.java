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
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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
    private static final String ERROR = "Cannot cast %s [%s] to %s.";
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
        Map<Instant, T> instances = new LinkedHashMap<>();
        for (FluxTable fluxTable : list) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                final Instant time = fluxRecord.getTime();
                final Map<String, Object> values = fluxRecord.getValues();
                T bean = getInstanceByRecord(definition, clazz, time, instances, values);
                fillFieldsOfRecord(fluxRecord, bean, definition, values);
                instances.put(time, bean);
            }
        }
        return new LinkedList<>(instances.values());
    }

    private static <T> void fillFieldsOfRecord(FluxRecord fluxRecord, T bean, BeanDefinition definition, Map<String, Object> values) {
        BeanMap beanMap = BeanMap.create(bean);
        Map<String, String> fields = definition.getFields();
        Object actual = fluxRecord.getValueByKey(FIELD);
        if (actual != null) {
            String key = (String) actual;
            if (fields.containsKey(key)) {
                String field = fields.get(key);
                Class<?> fieldType = beanMap.getPropertyType(field);
                beanMap.put(field, convertType(fieldType, values.get(VALUE)));
            }
        }
    }

    @Nonnull
    private static <T> T getInstanceByRecord(@Nonnull BeanDefinition definition,
                                             @Nonnull Class<T> clazz,
                                             Instant time,
                                             Map<Instant, T> instances,
                                             Map<String, Object> values)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        T bean = instances.get(time);
        if (bean == null) {
            bean = clazz.getDeclaredConstructor().newInstance();
            final BeanMap beanMap = BeanMap.create(bean);
            beanMap.put(definition.getTime(), time);
            definition.getTags().forEach((key, value) -> {
                if (values.containsKey(key)) {
                    Class<?> fieldType = beanMap.getPropertyType(value);
                    beanMap.put(key, convertType(fieldType, values.get(key)));
                }
            });
        }
        return bean;
    }

    private static <T> BeanDefinition resolveBeanDefinition(Class<T> clazz) {
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
        BeanDefinition recordFields = new BeanDefinition();
        recordFields.setTime(time);
        recordFields.setTags(tags);
        recordFields.setFields(fields);
        CACHE.put(qualifyName, recordFields);
        return recordFields;
    }

    private static Object convertType(@Nullable final Class<?> fieldType, @Nullable final Object value) {
        assert fieldType != null;
        assert value != null;
        if (fieldType.equals(value.getClass())) {
            return value;
        }
        if (double.class.isAssignableFrom(fieldType) || Double.class.isAssignableFrom(fieldType)) {
            return toDoubleValue(value);
        }
        if (long.class.isAssignableFrom(fieldType) || Long.class.isAssignableFrom(fieldType)) {
            return toLongValue(value);
        }
        if (int.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType)) {
            return toIntValue(value);
        }
        if (boolean.class.isAssignableFrom(fieldType)) {
            return Boolean.valueOf(String.valueOf(value));
        }
        if (BigDecimal.class.isAssignableFrom(fieldType)) {
            return toBigDecimalValue(value);
        }
        if (Date.class.isAssignableFrom(fieldType)) {
            return toDate(value);
        }

        return value;


    }

    private static Date toDate(Object value) {
        if (Instant.class.isAssignableFrom(value.getClass())) {
            return new Date(((Instant) value).toEpochMilli());
        }
        String message = String.format(ERROR, value.getClass().getName(), value, Date.class);
        throw new ClassCastException(message);
    }

    private static double toDoubleValue(final Object value) {
        if (double.class.isAssignableFrom(value.getClass()) || Double.class.isAssignableFrom(value.getClass())) {
            return (double) value;
        }
        return 0.0;
    }

    private static long toLongValue(final Object value) {

        if (long.class.isAssignableFrom(value.getClass()) || Long.class.isAssignableFrom(value.getClass())) {
            return (long) value;
        }

        return ((Double) value).longValue();
    }

    private static int toIntValue(final Object value) {

        if (int.class.isAssignableFrom(value.getClass()) || Integer.class.isAssignableFrom(value.getClass())) {
            return (int) value;
        }
        if (double.class.isAssignableFrom(value.getClass()) || Double.class.isAssignableFrom(value.getClass())) {
            return ((Double) value).intValue();
        }
        return ((Long) value).intValue();
    }

    private static BigDecimal toBigDecimalValue(final Object value) {
        if (String.class.isAssignableFrom(value.getClass())) {
            return new BigDecimal((String) value);
        }

        if (double.class.isAssignableFrom(value.getClass()) || Double.class.isAssignableFrom(value.getClass())) {
            return BigDecimal.valueOf((double) value);
        }

        if (int.class.isAssignableFrom(value.getClass()) || Integer.class.isAssignableFrom(value.getClass())) {
            return BigDecimal.valueOf((int) value);
        }

        if (long.class.isAssignableFrom(value.getClass()) || Long.class.isAssignableFrom(value.getClass())) {
            return BigDecimal.valueOf((long) value);
        }

        String message = String.format(ERROR, value.getClass().getName(), value, BigDecimal.class);

        throw new ClassCastException(message);
    }


    @Getter
    @Setter
    private static class BeanDefinition {
        private Map<String, String> tags;
        private Map<String, String> fields;
        private String time;
    }
}
