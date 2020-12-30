package com.cjz.webmvc.url;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.URI;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-23 9:06
 */
@Slf4j
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

    @Test
    public void t2() {
        final String urlStr = "http://192.168.32.87:20002/test/1/2";
        final URI uri = URI.create(urlStr);
        log.info("{} {} {} {}", uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath());
    }

}
