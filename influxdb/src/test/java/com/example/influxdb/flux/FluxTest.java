package com.example.influxdb.flux;

import com.example.influxdb.model.BusInfo;
import com.example.influxdb.runner.AppRunner;
import com.google.gson.Gson;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.domain.DeletePredicateRequest;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.flux.FluxClient;
import com.influxdb.client.flux.FluxClientFactory;
import com.influxdb.client.flux.FluxConnectionOptions;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cglib.beans.BeanMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-30 10:19
 */
public class FluxTest {
	private static final String BUCKET = "demo";

	private static final String URL = "http://192.168.240.185:9999";
	private static final String ORG = "tiamaes";
	private static final String TOKEN = "3EmkEs3kL5pwmV3TK-n-E3Ysok92GR9_N6zxUl5qrSfYYLoecojSaBi-xi6A5vAtC7_04WZu4qEZkZqAo9eW4w==";

	private static final String CLOUD_URL = "https://us-central1-1.gcp.cloud2.influxdata.com";
	private static final String CLOUD_ORG = "Ccx";
	private static final String CLOUD_TOKEN = "EosP9yZwK2qcJEyY9QVAKEN2YU85pUwubKaJu3MuNAV0hIdlwnXV6TcpdpiK95tk0Lunb6NzWQypZ8UJNkLjNA==";
	private static InfluxDBClient influxDBClient;
	private static InfluxDBClient influxDBClientCloud;

	@Before
	public void before() throws Exception {
		influxDBClient = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, BUCKET);

		InfluxDBClientOptions options = InfluxDBClientOptions.builder()
				.url(CLOUD_URL)
				.authenticateToken(CLOUD_TOKEN.toCharArray())
				.org(CLOUD_ORG)
				.bucket(BUCKET)
				.okHttpClient(new OkHttpClient.Builder().proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 58591))))
				.build();

		influxDBClientCloud = InfluxDBClientFactory.create(options);
	}

	@After
	public void after() {
		influxDBClient.close();
		influxDBClientCloud.close();
	}

	@Test
	@SneakyThrows
	public void save() {
		//BufferedReader是可以按行读取文件
		FileInputStream inputStream = new FileInputStream("E:\\IDEA\\cloud\\influxdb\\src\\test\\java\\com\\example\\influxdb\\flux\\points.csv");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String str = null;
		final InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, "history");
		while ((str = bufferedReader.readLine()) != null) {
			final String[] data = str.split(",");
			Date date = DateUtils.parseDate(data[0], "yyyy/MM/dd HH:mm");
			final Calendar instance = Calendar.getInstance();
			instance.setTime(date);
			instance.add(Calendar.MONTH, +1);
			instance.set(Calendar.DAY_OF_MONTH, 20);
			date = instance.getTime();
			final Point point = Point.measurement("replays")
					.addTag("id", "1310115752375099393")
					.time(date.getTime(), WritePrecision.MS);
			point.addField("lon", Double.valueOf(data[1]));
			point.addField("lat", Double.valueOf(data[2]));
			point.addField("speed", RandomUtils.nextLong(10, 100));
			client.getWriteApi().writePoint("history", ORG, point);
			System.out.println(date + "\t" + data[1] + "\t" + data[2]);
			Thread.sleep(20);
		}
		//close
		inputStream.close();
		bufferedReader.close();
	}

	@Test
	@SneakyThrows
	public void delete() {
		InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, "history");
		DeletePredicateRequest request = new DeletePredicateRequest();
		request.setPredicate("id=1602221568424");
		request.setStart(OffsetDateTime.of(LocalDateTime.of(2020, 10, 1, 0, 0), ZoneOffset.UTC));
		request.setStop(OffsetDateTime.of(LocalDateTime.of(2020, 10, 31, 23, 59), ZoneOffset.UTC));
		client.getDeleteApi().delete(request, "history", ORG);
		Thread.sleep(5 * 1000);
	}

	@Test
	public void t1() throws Exception {
		String flux = "from(bucket: \"demo\")\n" +
				"  |> range(start: -1d)\n" +
				"  |> filter(fn: (r) => r._measurement == \"busInfoV2\")\n" +
				"  |> filter(fn: (r) => r.carNo == \"C6666\")\n" +
				"  |> filter(fn: (r) => r.carNumber == \"豫A00000\")\n" +
				"  |> filter(fn: (r) => r.machineNo == \"M1111\")\n" +
				"  |> aggregateWindow(every: 15m, fn: max)\n" +
				"  |> yield(name: \"max\")";

//		flux = "from(bucket: \"demo\")\n" +
//				"  |> range(start: -1d)\n" +
//				"  |> filter(fn: (r) => r._measurement == \"busInfoV2\")\n" +
//				"  |> filter(fn: (r) => r._field == \"openDoor\")\n" +
//				"  |> aggregateWindow(every: 1h, fn: mean)\n" +
//				"  |> yield(name: \"mean\")";

		List<FluxTable> tables = influxDBClient.getQueryApi().query(flux);

		final Gson gson = new Gson();
//		InfluxMapper.toPOJO(tables, BusInfo.class).forEach(val -> {
//			System.out.println(gson.toJson(val));
//		});
	}

	@Test
	public void t3() throws Exception {
		String flux = "from(bucket: \"history\")\n" +
				"  |> range(start: -2h)\n" +
				"  |> filter(fn: (r) => r._measurement == \"replays\")\n" +
				"  |> limit(n:1, offset: 0)";

		List<FluxTable> tables = influxDBClient.getQueryApi().query(flux);

		final Gson gson = new Gson();
//		InfluxMapper.toPOJO(tables, Replay.class).forEach(val -> System.out.println(gson.toJson(val)));
	}

	@Test
	public void t2() {
		BusInfo busInfo = AppRunner.randomBusInfo(1, LocalDateTime.now(), new Gson());
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
		influxDBClientCloud.getWriteApi().writePoint(BUCKET, ORG, point);
	}

	@Test
	public void t999() {
		FluxConnectionOptions options = FluxConnectionOptions.builder()
				.url(URL)
				.okHttpClient(new OkHttpClient.Builder().authenticator(((route, response) -> response.request().newBuilder()
						.header("Authorization", TOKEN)
						.build())))
				.build();
		final FluxClient fluxClient = FluxClientFactory.create(options);
		String flux = "from(bucket: \"demo\")" +
				"  |> range(start: -30d)" +
				"  |> filter(fn: (r) => r._measurement == \"busInfoV2\")" +
				"  |> filter(fn: (r) => r._field == \"temperature\")" +
				"  |> filter(fn: (r) => r.carNo == \"C6666\")" +
				"  |> filter(fn: (r) => r.carNumber == \"豫A00000\")" +
				"  |> filter(fn: (r) => r.machineNo == \"M1111\")" +
				"  |> limit(n:10)";

		//
		// Synchronous query
		//
		List<FluxTable> tables = fluxClient.query(flux);

		for (FluxTable fluxTable : tables) {
			List<FluxRecord> records = fluxTable.getRecords();
			for (FluxRecord fluxRecord : records) {
				System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
			}
		}

		//
		// Asynchronous query
		//
		fluxClient.query(flux, (cancellable, record) -> {

			// process the flux query result record
			System.out.println(record.getTime() + ": " + record.getValue());

		}, error -> {

			// error handling while processing result
			System.out.println("Error occurred: " + error.getMessage());

		}, () -> {

			// on complete
			System.out.println("Query completed");
		});

		fluxClient.close();
	}


}
