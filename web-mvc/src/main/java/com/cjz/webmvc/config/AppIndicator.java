package com.cjz.webmvc.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 自定义健康检查 http://192.168.32.87:8080/actuator
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-11-07 14:06
 */
@Component
public class AppIndicator implements HealthIndicator {


	@Override
	public Health health() {
		return Health.up().withDetail("message", "可以").build();
	}
}
