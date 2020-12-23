package com.example.factorydemo.conditional;

import com.example.factorydemo.conditional.ConditionalTest.ConditionalDemo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-08 14:44
 */
@Slf4j
@Profile("prd")
@Conditional(value = ConditionalDemo.class)
class ConditionalTest {

	@Test
	void t1() {
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(ConditionalTest.class);
		final ConfigurableEnvironment environment = context.getEnvironment();
		environment.setActiveProfiles("prd");
		context.refresh();
		final MutablePropertySources propertySources = environment.getPropertySources();

		log.info("{}", environment.getActiveProfiles());
	}

	public static class ConditionalDemo implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
			if (attrs != null) {
				for (Object value : attrs.get("value")) {
					if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
						return true;
					}
				}
				return false;
			}
			return true;
		}
	}

}
