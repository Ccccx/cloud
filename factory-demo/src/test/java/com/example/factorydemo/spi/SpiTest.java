package com.example.factorydemo.spi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Processor;
import java.util.ServiceLoader;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-07 18:12
 */
@Slf4j
 class SpiTest {

    @Test
    void t1() {
        final ServiceLoader<Processor> load = ServiceLoader.load(Processor.class);
        for (Processor processor : load) {
            log.info("{}", processor);
        }
    }
}
