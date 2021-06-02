package com.example.factorydemo.thread;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-12 17:03
 */
public class ThreadTest {

    @Test
    @SneakyThrows
    void t1() {
        final Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(60 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        final Thread t2 = new Thread(() -> {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();

        TimeUnit.SECONDS.sleep(10);

        t1.interrupt();
        System.out.println("-------------");
        t2.interrupt();
    }
}
