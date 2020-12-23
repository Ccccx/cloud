package com.example.factorydemo.mvc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-07 18:46
 */
@Slf4j
public class MvcTests {

	private final MockHttpServletRequest request = new MockHttpServletRequest();

	@BeforeEach
	public void setup() {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));
	}

	@AfterEach
	public void reset() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	public void t1() {
		this.request.setScheme("https");
		this.request.addHeader("X-Forwarded-Host", "somethingDifferent");
		final UriComponents uriComponents = MvcUriComponentsBuilder.fromController(PersonControllerImpl.class).buildAndExpand(1);
		log.info(uriComponents.toUriString());


	}

	@RequestMapping("/people/{id}/addresses")
	interface PersonController {
	}


	static class PersonControllerImpl implements PersonController {

		@RequestMapping("/{country}")
		HttpEntity<Void> getAddressesForCountry(@PathVariable String country) {
			return null;
		}
	}
}
