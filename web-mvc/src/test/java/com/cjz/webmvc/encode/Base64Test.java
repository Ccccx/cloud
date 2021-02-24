package com.cjz.webmvc.encode;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-28 11:07
 */
@Slf4j
 class Base64Test {

    @Test
     void t1() {
         String uid = "3";
         String str = uid + ";" + System.currentTimeMillis();
         final byte[] encode = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
         log.info("Token: {}", new String(encode));
     }
}
