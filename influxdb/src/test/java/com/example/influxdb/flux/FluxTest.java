package com.example.influxdb.flux;

import com.example.influxdb.model.BusInfo;
import com.example.influxdb.model.M1Replay;
import com.example.influxdb.runner.AppRunner;
import com.example.influxdb.utils.InfluxMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.DeletePredicateRequest;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.WriteSuccessEvent;
import com.influxdb.query.FluxTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-30 10:19
 */
@Slf4j
public class FluxTest {
    private static final String BUCKET = "demo";

    private static final String URL = "http://192.168.240.185:9999";
    private static final String ORG = "tiamaes";
    private static final String TOKEN = "A9MB6VdHOnLpoGSEznsZHBPLhK00ZEbcbA4UlUHevPn2OtOTBUocVCxgCYXl9R69MhdG6IP7tDAuFxBpTAsl5Q==";

    private static final String CLOUD_URL = "https://us-central1-1.gcp.cloud2.influxdata.com";
    private static final String CLOUD_ORG = "Ccx";
    private static final String CLOUD_TOKEN = "EosP9yZwK2qcJEyY9QVAKEN2YU85pUwubKaJu3MuNAV0hIdlwnXV6TcpdpiK95tk0Lunb6NzWQypZ8UJNkLjNA==";
    private static InfluxDBClient influxDBClient;
    private static InfluxDBClient influxDBClientCloud;

    @Before
    public void before() throws Exception {
        influxDBClient = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, BUCKET);

//		InfluxDBClientOptions options = InfluxDBClientOptions.builder()
//				.url(CLOUD_URL)
//				.authenticateToken(CLOUD_TOKEN.toCharArray())
//				.org(CLOUD_ORG)
//				.bucket(BUCKET)
//				.okHttpClient(new OkHttpClient.Builder().proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 58591))))
//				.build();
//
//		influxDBClientCloud = InfluxDBClientFactory.create(options);
    }

    @After
    public void after() {
        influxDBClient.close();
        //influxDBClientCloud.close();
    }

    @Test
    @SneakyThrows
    public void save() {
        //BufferedReader是可以按行读取文件
        FileInputStream inputStream = new FileInputStream("E:\\IDEA\\cloud\\influxdb\\src\\test\\java\\com\\example\\influxdb\\flux\\points.csv");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        final InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, "history");
        final WriteApi writeApi = client.getWriteApi();
        while ((str = bufferedReader.readLine()) != null) {
            final String[] data = str.split(",");
            final Calendar instance = Calendar.getInstance();
            instance.setTime(DateUtils.parseDate(data[0], "YYYY/MM/DD HH:mm"));
            instance.set(Calendar.YEAR, 2021);
            instance.set(Calendar.MONTH, Calendar.JANUARY);
            instance.set(Calendar.DAY_OF_MONTH, 27);
            Date date = instance.getTime();
            final Point point = Point.measurement("replays")
                    .addTag("id", "1310115752375099393")
                    .time(date.getTime(), WritePrecision.MS);
            final Double lon = Double.valueOf(data[1]);
            final Double lat = Double.valueOf(data[2]);
            point.addField("lon", lon);
            point.addField("lat", lat);
            final long speed = RandomUtils.nextLong(10, 100);
            final int direction = RandomUtils.nextInt(1, 360);
            point.addField("speed", speed);
            point.addField("direction", direction);
            writeApi.writePoint("history", ORG, point);
            System.out.println(date + "\t" + lon + "\t" + lat + "\t" + speed + "\t" + direction);
            writeApi.flush();
        }
        //close
        inputStream.close();
        bufferedReader.close();
    }

    @Test
    @SneakyThrows
    public void deleteHistory() {
        // InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, "history");
        InfluxDBClient client = InfluxDBClientFactory.create("http://192.168.58.131:9999", "iOG0Psxeg9lh5y2O0A3MRGIH94_2WDp0azVEgEmd69DYALhwmtxI4CUWvQRmGJ8wiFqLk_SpHzQYYs5bnhnuQw==".toCharArray(), ORG, "history");
        DeletePredicateRequest request = new DeletePredicateRequest();
        request.setPredicate("_measurement=boltdb_reads_total");
        request.setStart(OffsetDateTime.of(LocalDateTime.of(2020, 11, 1, 0, 0), ZoneOffset.UTC));
        request.setStop(OffsetDateTime.of(LocalDateTime.of(2021, 3, 30, 0, 0), ZoneOffset.UTC));
        final DeleteApi deleteApi = client.getDeleteApi();
        deleteApi.delete(request, "history", ORG);
        Thread.sleep(5 * 1000);
    }


    @Test
    @SneakyThrows
    public void saveLinesBus() {
        final InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, "demo");
        final WriteApi writeApi = client.getWriteApi();

        writeApi.listenEvents(WriteSuccessEvent.class, value -> log.info("he data was successfully written to InfluxDB."));

        final ClassPathResource resource = new ClassPathResource("com/example/influxdb/flux/lines-bus.json");
        final String lineStr = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
        final Gson gson = new Gson();
        Type typeOfT = new TypeToken<List<List<Integer>>>() {
        }.getType();
        List<List<Integer>> lineList = gson.fromJson(lineStr, typeOfT);
        int count = 0;
        for (List<Integer> list : lineList) {
            count += list.size();
        }

        Map<String, ArrayList<LinePoint>> map = new LinkedHashMap<>();
        Date date = Date.from(LocalDateTime.of(2020, 12, 1, 8, 0).toInstant(ZoneOffset.of("+8")));
        lineList.forEach(points -> {
            ArrayList<LinePoint> linePoints = new ArrayList<>();
            map.put(UUID.randomUUID().toString().replace("-", ""), linePoints);
            LinePoint tmpPoint = null;
            for (int i = 0; i < points.size(); i += 2) {
                if (i == 0) {
                    tmpPoint = new LinePoint(date, points.get(i), points.get(i + 1));
                } else {
                    final Calendar instance = Calendar.getInstance();
                    instance.setTime(tmpPoint.getDate());
                    instance.add(Calendar.SECOND, +15);
                    tmpPoint = new LinePoint(instance.getTime(), tmpPoint.lng + points.get(i), tmpPoint.lat + points.get(i + 1));
                }
                linePoints.add(tmpPoint);
            }
        });

        AtomicInteger pCounter = new AtomicInteger(0);
        map.forEach((k, v) -> {
            pCounter.addAndGet(v.size());
        });
        log.info("总计 {} 条, 折算点数: {}  ", count, pCounter.get());
//		if (true) {
//			return;
//		}
        int index = 1;

        for (Map.Entry<String, ArrayList<LinePoint>> entry : map.entrySet()) {
            final String k = entry.getKey();
            final ArrayList<LinePoint> v = entry.getValue();
            log.info("{} : {}", index, k);
            for (LinePoint p : v) {
                Point point = Point.measurement("lineBus").addTag("id", k);
                point.time(p.getDate().getTime(), WritePrecision.MS);
                point.addField("lon", p.getLng());
                point.addField("lat", p.getLat());
                // writeApi.writePoint("demo", ORG, point);
            }
            writeApi.flush();
            index++;
        }
        Thread.sleep(20 * 1000L);
        client.close();
    }

    @Test
    @SneakyThrows
    public void queryLinePoints() {
        String query = "from(bucket: \"demo\")\n" +
                "  |> range(start:2020-10-28T16:00:00.000Z, stop: 2020-10-31T15:00:00.000Z)" +
                "  |> filter(fn: (r) => r._measurement == \"lineBus\")\n";
        final InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, "demo");
        List<FluxTable> tables = client.getQueryApi().query(query);
        final List<M1Replay> m1Replays = InfluxMapper.toPojo(tables, M1Replay.class);

        final List<List<Integer>> result = m1Replays.parallelStream().collect(Collectors.groupingBy(M1Replay::getId)).entrySet().parallelStream().map(Map.Entry::getValue).map(v -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < v.size(); i++) {
                final M1Replay point = v.get(i);
                if (list.size() == 0) {
                    list.add((int) (point.getLng() * 10000));
                    list.add((int) (point.getLat() * 10000));
                } else {
                    final M1Replay prePoint = v.get(i - 1);
                    list.add(((int) (point.getLng() * 10000)) - ((int) (prePoint.getLng() * 10000)));
                    list.add(((int) (point.getLat() * 10000)) - ((int) (prePoint.getLat() * 10000)));
                }
            }
            return list;
        }).collect(Collectors.toList());
        log.info("end ...{}", m1Replays.size());
    }

    @Test
    @SneakyThrows
    public void delete() {
        //InfluxDBClient client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, "history");
        InfluxDBClient client = InfluxDBClientFactory.create("http://192.168.58.131:9999", "iOG0Psxeg9lh5y2O0A3MRGIH94_2WDp0azVEgEmd69DYALhwmtxI4CUWvQRmGJ8wiFqLk_SpHzQYYs5bnhnuQw==".toCharArray(), ORG, "history");
        DeletePredicateRequest request = new DeletePredicateRequest();
        request.setPredicate("_measurement=boltdb_reads_total");
        // request.setPredicate("id=1310115752375099393");
        request.setStart(OffsetDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0), ZoneOffset.UTC));
        request.setStop(OffsetDateTime.of(LocalDateTime.of(2021, 3, 26, 0, 0), ZoneOffset.UTC));
        final DeleteApi deleteApi = client.getDeleteApi();
        deleteApi.delete(request, "history", ORG);
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

    @Data
    @AllArgsConstructor
    public static class LinePoint {
        Date date;
        Integer lng;
        Integer lat;

        public Double getLng() {
            return lng / 10000.0;
        }

        public Double getLat() {
            return lat / 10000.0;
        }
    }


}
