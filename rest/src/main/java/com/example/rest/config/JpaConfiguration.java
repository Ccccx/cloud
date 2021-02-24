package com.example.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-26 14:09
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.rest.*")
public class JpaConfiguration {
}
