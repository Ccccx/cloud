package com.cjz.webmvc.lambda;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-29 18:11
 */
@Slf4j
public class MapTest {
	@Test
	@SneakyThrows
	public void t1() {
		final ClassPathResource resource = new ClassPathResource("response_1603964901275.json");
		final String lineStr = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
		final List<Point> points = JSONObject.parseArray(lineStr, Point.class);
		final Map<String, List<Point>> listMap = points.parallelStream().collect(Collectors.groupingBy(Point::getId));
		log.info("Point size {}", points.size());
		final List<List<Integer>> result = listMap.entrySet().parallelStream().map(Map.Entry::getValue).map(v -> {
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < v.size(); i++) {
				final Point point = v.get(i);
				if (list.size() == 0) {
					list.add((int) (point.getLng() * 10000));
					list.add((int) (point.getLat() * 10000));
				} else {
					final Point prePoint = v.get(i - 1);
					list.add(((int) (point.getLng() * 10000)) - ((int) (prePoint.getLng() * 10000)));
					list.add(((int) (point.getLat() * 10000)) - ((int) (prePoint.getLat() * 10000)));
				}
			}
			return list;
		}).collect(Collectors.toList());
		log.info("end ...");
	}

	@Data
	public static class Point {
		String id;
		String line;
		Double lng;
		Double lat;
		Instant instant;
		Date time;
	}
}
