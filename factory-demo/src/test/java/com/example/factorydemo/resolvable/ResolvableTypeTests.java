package com.example.factorydemo.resolvable;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;

import java.util.HashMap;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-08 19:44
 */
@Slf4j
class ResolvableTypeTests {
	@Test
	@SneakyThrows
	void t1() {
		ResolvableType t = ResolvableType.forField(Foo.class.getDeclaredField("myMap"));
		log.info("{} ", t.getSuperType()); // AbstractMap<Integer, List<String>>
		log.info("{} ", t.asMap()); // Map<Integer, List<String>>
		log.info("{} ", t.getGeneric(0).resolve()); // Integer
		log.info("{} ", t.getGeneric(1).resolve()); // List
		log.info("{} ", t.getGeneric(1)); // List<String>
		log.info("{} ", t.resolveGeneric(1, 0)); // String
	}

	private static class Foo {
		private HashMap<Integer, List<String>> myMap;
	}
}
