package com.example.influxdb.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-05-13 11:00
 */
@Slf4j
@Component
public class InfluxDbComponent {

	private static final String BUCKET = "demo";
	private static final String ORG = "tiamaes";
	private final InfluxDBClient influxClient;

	private static final String MEASUREMENT = "test";

	@Value("${spring.application.name}")
	private String applicationName;

	public InfluxDbComponent(InfluxDBClient influxClient) {
		this.influxClient = influxClient;
	}

	public void save(Double random) {
		final Point point = Point.measurement(MEASUREMENT).addTag("host", "192.168.32.220").addTag("appName", applicationName).time(Instant.now(), WritePrecision.NS);
		point.addField("random", random);
		influxClient.getWriteApi().writePoint(BUCKET, ORG, point);
		log.info("send : {}", random);
	}

}
