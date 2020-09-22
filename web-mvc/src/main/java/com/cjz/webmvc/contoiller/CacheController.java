package com.cjz.webmvc.contoiller;

import com.cjz.webmvc.component.CacheComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-21 14:39
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

	private final CacheComponent cacheComponent;

	public CacheController(CacheComponent cacheComponent) {
		this.cacheComponent = cacheComponent;
	}

	@GetMapping("/t1")
	public String t1() {
		return cacheComponent.t1();
	}

	@GetMapping("/t2")
	public String t2() {
		return cacheComponent.t2();
	}
}
