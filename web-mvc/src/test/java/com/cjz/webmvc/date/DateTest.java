package com.cjz.webmvc.date;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-20 19:42
 */
@Slf4j
public class DateTest {

	@Test
	public void t1() {
		log.info("{}", new Date());
		log.info("{}", new Date().toInstant());
		log.info("{}", new Date().toInstant().atZone(ZoneOffset.of("-8")).toInstant());
	}
}
