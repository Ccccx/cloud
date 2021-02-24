package com.cjz.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author chengjz
 */
@Controller
@SpringBootApplication
public class GatewayApplication implements Runnable {

    @Value("${property.from.sample.custom.source:default}")
    public String source;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // @GetMapping("/test")
    public String index() {
        return "test";
    }

    /**
     * 配置静态资源
     */
//    @Bean
//    public RouterFunction<ServerResponse> staticResourceLocator() {
//        return RouterFunctions.resources("/static/**", new ClassPathResource("/static/"));
//    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        return builder.routes()
                // 根据路径路由
//                .route("rlzy", r -> r.path("/rlzy/**")
//                        .uri("http://192.168.242.221:17101/"))
//                .route("base", r -> r.path("/**")
//                        .uri("http://192.168.32.87:8080/"))
                // 主机路由
                .route("host_route", r -> r.host("*.myhost.org")
                        .uri("http://httpbin.org"))
                .route("rewrite_route", r -> r.host("*.rewrite.org")
                        .filters(f -> f.rewritePath("/foo/(?<segment>.*)",
                                "/${segment}"))
                        .uri("http://httpbin.org"))
//				.route("hystrix_route", r -> r.host("*.hystrix.org")
//						.filters(f -> f.hystrix(c -> c.setName("slowcmd")))
//						.uri("http://httpbin.org"))
//				.route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
//						.filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
//						.uri("http://httpbin.org"))
//				.route("limit_route", r -> r
//						.host("*.limited.org").and().path("/anything/**")
//						.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
//						.uri("http://httpbin.org"))
//				.route("websocket_route", r -> r.path("/echo")
//						.uri("ws://localhost:9000"))
                .build();
        //@formatter:on
    }

    @Override
    public void run() {
        System.out.println(source);
    }
}
