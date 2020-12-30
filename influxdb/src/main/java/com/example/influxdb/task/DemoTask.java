package com.example.influxdb.task;

import com.example.influxdb.service.InfluxDbComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-05-12 18:10
 */
@Slf4j
@Component
public class DemoTask {

    private final Random random = new Random(Integer.MAX_VALUE);
    @Resource
    private InfluxDbComponent dbComponent;

    //@Scheduled(cron = "*/3 * * * * ?")
    public void t1() {
        dbComponent.save(random.nextDouble());
    }

}
