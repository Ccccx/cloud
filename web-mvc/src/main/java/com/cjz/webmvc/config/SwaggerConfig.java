package com.cjz.webmvc.config;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.Principal;
import java.time.LocalDate;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-16 9:36
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket defaultDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.paths(PathSelectors.any())
				.build()
				.ignoredParameterTypes(Principal.class, Authentication.class)
				.pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class).genericModelSubstitutes(ResponseEntity.class)
				.useDefaultResponseMessages(true)
				.enableUrlTemplating(false)
				.enable(true);
	}


	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("演示项目")
				.description("这是一个演示项目")
//				.contact(new Contact(info.getContact().getName(), info.getContact().getUrl(),
//						info.getContact().getEmail()))
				.version("v1.0.0")
				.build();
	}
}
