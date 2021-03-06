package com.cjz.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 使用junit5 测试注意 用的是 {@link Test}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WebMvcApplicationTests {

    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebClient webClient;

    public static void main(String[] args) {

    }


}
