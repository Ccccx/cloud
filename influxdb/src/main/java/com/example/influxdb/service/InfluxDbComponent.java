package com.example.influxdb.service;

import com.example.influxdb.model.BusInfo;
import com.example.influxdb.model.BusInfoTemperature;
import com.example.influxdb.model.M1Replay;
import com.example.influxdb.utils.InfluxMapper;
import com.google.gson.Gson;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

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

	private static final Gson GSON = new Gson();

	@Value("${spring.application.name}")
	private String applicationName;

	public InfluxDbComponent(InfluxDBClient influxClient) {
		this.influxClient = influxClient;
	}

	public void save(Double random) {
		final Point point = Point.measurement(MEASUREMENT).addTag("host", "192.168.32.220").addTag("appName", applicationName).time(Instant.now(), WritePrecision.MS);
		point.addField("random", random);
		influxClient.getWriteApi().writePoint(BUCKET, ORG, point);
		log.info("send : {}", random);
	}

	public BusInfo busHistory(BusInfo busInfo) {
		final Point point = Point.measurement("busInfoV2")
				.addTag("machineNo", busInfo.getMachineNo())
				.addTag("carNo", busInfo.getCarNo())
				.addTag("carNumber", busInfo.getCarNumber())
				.time(busInfo.getSiteTime(), WritePrecision.MS);

		final BeanMap beanMap = BeanMap.create(busInfo);
		beanMap.forEach((k, v) -> {
			if (!"machineNo".equals(k) &&
					!"carNo".equals(k) &&
					!"carNumber".equals(k) &&
					!"siteTime".equals(k)) {
				final Object o = beanMap.get(k);
				if (o != null) {
					final String val = o.toString();
					if (o instanceof Number) {
						point.addField((String) k, Double.valueOf(val));
					} else if (o instanceof String) {
						point.addField((String) k, val);
					}
				}
			}
		});
		influxClient.getWriteApi().writePoint(BUCKET, ORG, point);
		log.info("Send Success ...");
		return busInfo;
	}

	public List<BusInfoTemperature> queryTemperature() {
		String query = "from(bucket: \"demo\")" +
				"  |> range(start: -30d)" +
				"  |> filter(fn: (r) => r._measurement == \"busInfoV2\")" +
				"  |> filter(fn: (r) => r._field == \"temperature\")" +
				"  |> filter(fn: (r) => r.carNo == \"C6666\")" +
				"  |> filter(fn: (r) => r.carNumber == \"豫A00000\")" +
				"  |> filter(fn: (r) => r.machineNo == \"M1111\")" +
				"  |> limit(n:10)";
		return influxClient.getQueryApi().query(query, ORG, BusInfoTemperature.class);
	}


	public List<M1Replay> queryReplay(String flux) throws Exception {
		log.info("\n{}", flux);
		final List<FluxTable> fluxTables = influxClient.getQueryApi().query(flux, ORG);
		return InfluxMapper.toPojo(fluxTables, M1Replay.class);
	}

	public List<List<Double>> queryReplayPoint(String flux) throws Exception {
		final List<M1Replay> replayList = queryReplay(flux);
		List<List<Double>> result = new LinkedList<>();
		replayList.forEach(v -> {
			List<Double> points = new LinkedList<>();
			points.add(v.getLng());
			points.add(v.getLat());
			result.add(points);
		});
		return result;
	}
}
