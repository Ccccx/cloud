package com.example.factory.support;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 应用配置信息
 * @author chengjz
 * @version 1.0
 * @since 2021-03-01 11:33
 */
@Data
@EqualsAndHashCode
@ConfigurationProperties(prefix = "m1.server")
public class TestProperties {
    /**
     * 系统名称
     */
    private String title = "快速开发平台";
    /**
     * 系统版权
     */
    private String copyright = "版权 &copy; 郑州天迈科技";
    /**
     * 是否开启多系统模式
     */
    private boolean multiSystem = false;
    /**
     * 是否开启验证码模式
     */
    private boolean captcha = false;

    private Customize customize;

    /**
     * 随机32位MD5字符串
     */
    @Value("${random.value}")
    private  String secret;

    /**
     * 随机int数字
     */
    @Value("${random.int}")
    private int intNumber;

    /**
     * 随机long数字
     */
    @Value("${random.long}")
    private long longNumber;

    /**
     * 随机uuid
     */
    @Value("${random.uuid}")
    private String  uuid;

    /**
     * 随机10以内的数字
     */
    @Value("${random.int(10)}")
    private int lessTen;

    /**
     * 随机1024~65536之内的数字
     */
    @Value("${random.int[1024,65536]}")
    private int range;

    @Value("${spring.application.name}")
    private String appName;

    public TestProperties target() {
        return this;
    }
}
