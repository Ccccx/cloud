package com.cjz.webmvc.url;

import org.junit.Test;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-23 9:06
 */
public class URLTest {
	@Test
	public void t1() throws Exception {
		java.net.URL url = new java.net.URL("http://test_1.tanglei.name");
		//test_1.tanglei.name
		System.out.println(url.getHost());
		java.net.URI uri = new java.net.URI("http://test_1.tanglei.name");
		//null
		System.out.println(uri.getHost());
	}

}
