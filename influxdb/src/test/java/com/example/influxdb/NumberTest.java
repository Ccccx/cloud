package com.example.influxdb;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-28 18:55
 */
public class NumberTest {
	@Test
	public void t1() {
		final Object bigDecimal = BigDecimal.valueOf(113.0 + RandomUtils.nextDouble(0.1, 0.9));
		System.out.println(bigDecimal.toString());
		System.out.println(Double.valueOf(bigDecimal.toString()));
	}

	@Test
	public void t2() {
		System.out.println(-1 >>> 1);
		System.out.println(-1 >> 1);
	}
}
