package com.example.factorydemo.jsonformat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-06 15:57
 */
@Slf4j
class JsonDemo {

    @Test
    @SneakyThrows
    void t1() {
        final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        final TypeFactory typeFactory = objectMapper.getTypeFactory();
        typeFactory.constructType(DateTest.class);
        log.info("{}", objectMapper.readValue("{\"date\":\"2020-06-06  00:00:00\",\"date2\":\"2020-06-06  00:00:00\"}", DateTest.class));
    }

    @Data
    public static class DateTest {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT")
        private Date date;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
        private Date date2;
    }
}
