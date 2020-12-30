package com.cjz.webmvc.reactor;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-28 9:36
 */
class ReactorTests {

    private static Publisher<? extends Integer> errorHandler(Throwable throwable) {
        throwable.printStackTrace();
        return null;
    }

    @Test
    @SneakyThrows
    void t1() {
        final Flux<Integer> flux = Flux.range(1, 5);
        final Consumer<Throwable> errConsumer = error -> System.err.println("Error: " + error);
        final Runnable finished = () -> System.out.println("已完成");
        flux.delayElements(Duration.ofSeconds(1))
                .subscribe(v -> {
                            if (v % 2 == 0) {
                                // 异常不会触发finished 回调
                                // throw new IllegalArgumentException(v +"能被2整除");
                            }
                            System.out.println(v);
                        },
                        errConsumer,
                        finished,
                        subscription -> subscription.request(10));
        TimeUnit.SECONDS.sleep(10);
        System.out.println();
    }

    @Test
    @SneakyThrows
    void t2() {
        Flux.generate(() -> 0, (state, sink) -> {
            sink.next("3 x " + state + " = " + 3 * state);
            if (state == 10) sink.complete();
            return state + 1;
        })
                .log()
                .subscribe(System.out::println);
        System.out.println();
    }

    @Test
    @SneakyThrows
    void t3() {
        List<String> data = new ArrayList<>();
        Flux.push(fluxSink -> {
            data.forEach(v -> fluxSink.next(v));
        }).subscribe(System.out::println);
        data.add("1");
        TimeUnit.SECONDS.sleep(1);
        data.add("2");
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @SneakyThrows
    void t4() {
        final Flux<Integer> flux = Flux.range(1, 5);
        flux.delayElements(Duration.ofSeconds(1))
                .onErrorReturn(0)
                .map(v -> {
                    if (v % 2 == 0) {
                        // 异常不会触发finished 回调
                        throw new IllegalArgumentException(v + "能被2整除");
                    }
                    return v;
                })
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(10);
        System.out.println();
    }


}
