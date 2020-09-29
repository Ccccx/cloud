package com.example.influxdb.controller;

import com.example.influxdb.model.BusInfoTemperature;
import com.example.influxdb.service.InfluxDbComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-28 15:41
 */
@RestController
@RequestMapping("/influx")
public class InfluxController {
	private final InfluxDbComponent influxDbComponent;

	public InfluxController(InfluxDbComponent influxDbComponent) {
		this.influxDbComponent = influxDbComponent;
	}

	@GetMapping("/busInfo/temperature")
	public List<BusInfoTemperature> queryTemperature() {
		return influxDbComponent.queryTemperature();
	}
}
