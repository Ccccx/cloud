package com.example.factorydemo.jsonformat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII;

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

    @Test
    @SneakyThrows
    void t2() {
        final Class<DateTest> type = DateTest.class;
        final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        SerializationConfig serializationConfig = mapper.getSerializationConfig();
        JavaType javaType = serializationConfig.constructType(type);
        BeanDescription description = serializationConfig.introspect(javaType);

        final List<BeanPropertyDefinition> definitions = description.findProperties();
         boolean isValue = description.findJsonValueAccessor() != null;

        DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
        JavaType deserializationType = deserializationConfig.constructType(type);

        final List<BeanPropertyDefinition> deserializationDefinitions = deserializationConfig.introspect(deserializationType).findProperties();
        log.info("end");
    }

    @Test
    @SneakyThrows
    void t3() {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator jg = factory.createGenerator(System.err, JsonEncoding.UTF8)) {
            // jg.disable(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS);

            jg.writeNumber(0.9);
            jg.writeNumber(1.9);

            jg.writeNumber(Float.NaN);
            jg.writeNumber(Float.NEGATIVE_INFINITY);
            jg.writeNumber(Float.POSITIVE_INFINITY);
        }
    }

    /**
     * 输出ascii 码
     */
    @Test
    @SneakyThrows
    void t4() {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator jg = factory.createGenerator(System.err, JsonEncoding.UTF8)) {
            jg.enable(ESCAPE_NON_ASCII);
            jg.writeString("A哥");
        }
    }

    @Test
    @SneakyThrows
    void t5() {
        String jsonStr = "{\"name\":\"YourBatman\",\"age\":18, \"pickName\":null}";
        System.out.println(jsonStr);
        JsonFactory factory = new JsonFactory();
        try (JsonParser jsonParser = factory.createParser(jsonStr)) {
            while (true) {
                JsonToken token = jsonParser.nextToken();
                System.out.println(token + " -> 值为:" + jsonParser.getValueAsString());
                if (token == JsonToken.END_OBJECT) {
                    break;
                }
            }
        }
    }

    @Test
    @SneakyThrows
    void t6() {
        final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        String json = " {\"mysql_user\":\"tmkj\",\"mysql_user_password\":\"Tmkj@zGb1/23\"}";
        final Mysql mysql = objectMapper.readValue(json, Mysql.class);
        log.info("{}", mysql);
        log.info("{}", objectMapper.writeValueAsString(mysql));

        final Gson gson = new Gson();
        final String toJson = gson.toJson(mysql);
        log.info("{}", toJson);
    }

    @Data
    public static class DateTest {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT")
        private Date date;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
        private Date date2;
    }

    @Data
    public static class Mysql {
        @JsonProperty("mysql_user")
        @SerializedName("mysql_user")
        private String user;
        @JsonProperty("mysql_user_password")
        @SerializedName("mysql_user_password")
        private String password;

        @Expose(serialize = false, deserialize = false)
        private String test = "123";
    }
}
