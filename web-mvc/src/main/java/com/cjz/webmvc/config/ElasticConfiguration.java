//package com.cjz.webmvc.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.apache.http.ssl.SSLContexts;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Description;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.util.Assert;
//
//import javax.net.ssl.SSLContext;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateFactory;
//import java.util.concurrent.Executor;
//
///**
// * @author chengjz
// * @version 1.0
// * @since 2020-11-26 9:12
// */
//@Slf4j
////@EnableScheduling
////@Configuration
////@EnableElasticsearchRepositories(basePackages = {"com.cjz"})
//public class ElasticConfiguration {
//
//    @Value("${elasticsearch.username:elastic}")
//    private String username;
//
//    @Value("${elasticsearch.password:tmkj@zgb123}")
//    private String password;
//
//    @Bean
//    @Primary
//    @Description(" Elasticsearch高级特性支持")
//    public RestHighLevelClient restHighLevelClient() throws Exception {
//        ClassPathResource resource = new ClassPathResource("ca.crt");
//        CertificateFactory factory = CertificateFactory.getInstance("X.509");
//        Certificate trustedCa;
//        try (InputStream inputStream = resource.getInputStream()) {
//            trustedCa = factory.generateCertificate(inputStream);
//        } catch (Exception e) {
//            try (InputStream inputStream = new FileInputStream(new File(ElasticConfiguration.class.getResource("es01.crt").toURI()))) {
//                trustedCa = factory.generateCertificate(inputStream);
//            }
//        }
//        Assert.notNull(trustedCa, "获取签名文件失败");
//        KeyStore trustStore = KeyStore.getInstance("pkcs12");
//        trustStore.load(null, null);
//        trustStore.setCertificateEntry("ca", trustedCa);
//
//        SSLContextBuilder sslContextBuilder = SSLContexts.custom()
//                .loadTrustMaterial(trustStore, null);
//        final SSLContext sslContext = sslContextBuilder.build();
//
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("es01:9200").usingSsl(sslContext)
//                .withBasicAuth(username, password)
//                .build();
//        return RestClients.create(clientConfiguration).rest();
//    }
//
//
//    @Bean
//    @Description("调度线程池，elasticsearch同步数据时使用")
//    public Executor taskScheduler() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setKeepAliveSeconds(30);
//        executor.setMaxPoolSize(1);
//        executor.setQueueCapacity(1);
//        executor.initialize();
//        return executor;
//    }
//
//}
