package com.example.factory.contoiller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-03-09 11:15
 */
@Slf4j
public class LongPollingConfigClient {

    private final RestTemplate restTemplate;

    public LongPollingConfigClient() {
        int timeout = 40000;
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(timeout);
        factory.setConnectTimeout(timeout + 10000);
        this.restTemplate = new RestTemplate(factory);
    }

    @SneakyThrows
    public void longPolling(String url, String dataId) {
        String endpoint = url + "?dataId=" + dataId;
        HttpGet request = new HttpGet(endpoint);
        final ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
        switch (response.getStatusCode().value()) {
            case 200: {
                String configInfo = response.getBody();
                log.info("dataId: [{}] changed, receive configInfo: {}", dataId, configInfo);
                longPolling(url, dataId);
                break;
            }
            //  304 响应码标记配置未变更
            case 304: {
                log.info("longPolling dataId: [{}] once finished, configInfo is unchanged, longPolling again", dataId);
                longPolling(url, dataId);
                break;
            }
            default: {
                throw new RuntimeException("unExcepted HTTP status code");
            }
        }
    }

    public static void main(String[] args) {
        // httpClient 会打印很多 debug 日志，关闭掉
        Logger logger = (Logger)LoggerFactory.getLogger("org.apache.http");
        logger.setLevel(Level.INFO);
        logger.setAdditive(false);

        LongPollingConfigClient configClient = new LongPollingConfigClient();
        // ③ 对 dataId: user 进行配置监听
        configClient.longPolling("http://127.0.0.1:8081/listener", "user");
    }
}
