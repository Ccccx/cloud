package com.example.influxdb.runner;

import com.example.influxdb.model.BusInfo;
import com.example.influxdb.service.InfluxDbComponent;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-28 10:32
 */
@Component
public class AppRunner implements ApplicationRunner {

	@Resource
	private InfluxDbComponent component;


	public static void main(String[] args) throws Exception {
		new AppRunner().run(new DefaultApplicationArguments());
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// mockBusInfo(150 * 1000);
	}

	private void mockBusInfo(int max) throws InterruptedException {
		LocalDateTime localDateTime = LocalDateTime.of(2020, 9, 16, 4, 0);
		final Gson gson = new Gson();
		for (int i = 0; i < max; i++) {
			final BusInfo busInfo = randomBusInfo(i + 1, localDateTime, gson);
			component.busHistory(busInfo);
			TimeUnit.MILLISECONDS.sleep(20);
			localDateTime = localDateTime.plusSeconds(RandomUtils.nextInt(1, 120));
		}
	}

	public BusInfo randomBusInfo(int index, LocalDateTime localDateTime, Gson gson) {
		final BusInfo busInfo = new BusInfo();
		busInfo.setMachineNo("M1111");
		busInfo.setCarNo("C6666");
		busInfo.setCarNumber("豫A00000");
		busInfo.setSiteTime(localDateTime.toInstant(ZoneOffset.UTC));
		busInfo.setLng(BigDecimal.valueOf(113.0 + RandomUtils.nextDouble(0.1, 0.9)));
		busInfo.setLng(BigDecimal.valueOf(34.0 + RandomUtils.nextDouble(0.1, 0.9)));
		busInfo.setVelocity(BigDecimal.valueOf(RandomUtils.nextDouble(40.0, 120.0)));
		busInfo.setOrientation(RandomUtils.nextInt(1, 10));
		busInfo.setState(RandomUtils.nextInt(0, 1));
		busInfo.setOilVol(BigDecimal.valueOf(RandomUtils.nextDouble(10.0, 240.0)));
		busInfo.setOpenDoor(RandomUtils.nextInt(0, 1));
		busInfo.setTemperature(BigDecimal.valueOf(RandomUtils.nextDouble(0.0, 40.0)));
		busInfo.setHumidity(BigDecimal.valueOf(RandomUtils.nextDouble(10.0, 100.0)));
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.rightPad(index + "", 6));
		sb.append(StringUtils.rightPad(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 20));
		sb.append(gson.toJson(busInfo));
		System.out.println(sb.toString());
		return busInfo;
	}
}
