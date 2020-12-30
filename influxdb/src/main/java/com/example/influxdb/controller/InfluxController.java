package com.example.influxdb.controller;

import com.example.influxdb.model.BusInfo;
import com.example.influxdb.model.BusInfoTemperature;
import com.example.influxdb.model.M1Replay;
import com.example.influxdb.runner.AppRunner;
import com.example.influxdb.service.InfluxDbComponent;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-28 15:41
 */
@RestController
@RequestMapping("/influx")
public class InfluxController {

    private static final AtomicInteger COUNT = new AtomicInteger(1);
    private static final Gson GSON = new Gson();

    private final InfluxDbComponent influxDbComponent;

    public InfluxController(InfluxDbComponent influxDbComponent) {
        this.influxDbComponent = influxDbComponent;
    }

    @GetMapping("/busInfo/temperature")
    public List<BusInfoTemperature> queryTemperature() {
        return influxDbComponent.queryTemperature();
    }


    @GetMapping("/busInfo")
    public BusInfo saveBusInfo() {
        final int flag = COUNT.getAndIncrement();
        final BusInfo busInfo = AppRunner.randomBusInfo(flag, LocalDateTime.now().plusSeconds(RandomUtils.nextInt(1, 20) + flag), GSON);
        return influxDbComponent.busHistory(busInfo);
    }

    @PostMapping("/queryReplay")
    public List<M1Replay> queryReplay(@RequestBody String flux) throws Exception {
        return influxDbComponent.queryReplay(flux);
    }

    @PostMapping("/queryReplay/point")
    public List<List<Double>> queryReplayPoint(@RequestBody String flux) throws Exception {
        return influxDbComponent.queryReplayPoint(flux);
    }
}
