package com.example.influxdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author chengjz
 */
@SpringBootApplication
@EnableScheduling
public class InfluxdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfluxdbApplication.class, args);
	}

}
