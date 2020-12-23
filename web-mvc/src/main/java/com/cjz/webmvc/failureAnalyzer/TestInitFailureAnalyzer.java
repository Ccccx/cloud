package com.cjz.webmvc.failureAnalyzer;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;

/**
 * https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-failure-analyzer
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-12-15 8:54
 */
public class TestInitFailureAnalyzer implements FailureAnalyzer {
	@Override
	public FailureAnalysis analyze(Throwable failure) {
		return null;
	}
}
