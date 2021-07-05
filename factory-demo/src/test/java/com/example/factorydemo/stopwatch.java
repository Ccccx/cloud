package com.example.factorydemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-07-02 14:01
 */
@Slf4j
public class stopwatch {
    @Test
    public void t1() {
        final StopWatch watch = new StopWatch();
        watch.setKeepTaskList(true);
        watch.start("开始");
        watch.stop();
        watch.start("11111111 start");
        watch.stop();
        log.info(watch.prettyPrint());

    }

    @Test
    void t2() {
        int a = 2;
        System.out.println(a++);
        System.out.println(a);
        System.out.println(++a);
    }
}
