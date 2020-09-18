package com.example.factorydemo.jsonformat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Data;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-06 15:57
 */
public class JsonDemo {

	public static void main(String[] args) throws JsonProcessingException {
		final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		final TypeFactory typeFactory = objectMapper.getTypeFactory();
		typeFactory.constructType(DateTest.class);
		System.out.println(objectMapper.readValue("{\"date\":\"2020-06-06  00:00:00\",\"date2\":\"2020-06-06  00:00:00\"}", DateTest.class));
	}

	@Data
	public static class DateTest {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT")
		private Date date;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
		private Date date2;
	}
}
