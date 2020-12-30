package com.example.factory.support.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:33
 */
@Data
@ConfigurationProperties(prefix = "initializr")
public class InitializrMetadata {
    private String name = "parent";
    private String desc = "This is a demo!";

}
