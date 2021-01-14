package com.cjz.cloud;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtils.HostInfo;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-06 15:39
 */
@Slf4j
class InetUtilsTest {
	@Test
	void t1() {
		final InetUtilsProperties properties = new InetUtilsProperties();
		properties.setIgnoredInterfaces(Arrays.asList("docker0", "veth.*", ".*VMware.*", ".*VirtualBox.*"));
		@Cleanup final InetUtils inetUtils = new InetUtils(properties);
		final HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo();
		final InetAddress address = inetUtils.findFirstNonLoopbackAddress();
		log.info("{}\t{}", hostInfo.getIpAddress(), hostInfo.getHostname());
		log.info("{}\t{}", address.getHostAddress(), address.getHostName());
	}
}
