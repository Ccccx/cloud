package com.cjz.webmvc.str;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-27 11:55
 */
@Slf4j
public class StringTest {

    @Test
    void t1() {
        String str = "1-2-3-4-5-6";
        List<String> strs = new ArrayList<>();
        final StringTokenizer tokenizer = new StringTokenizer(str, "-");
        StringBuilder sb = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (StringUtils.isEmpty(sb)) {
                sb.append(token);
            } else {
                sb.append("-").append(token);
            }
            log.info(sb.toString());
            strs.add(sb.toString());
        }
    }
}
