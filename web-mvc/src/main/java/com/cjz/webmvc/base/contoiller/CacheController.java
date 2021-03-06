package com.cjz.webmvc.base.contoiller;

import com.cjz.webmvc.base.component.CacheComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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


	@GetMapping("/demo")
	public ModelAndView demo(ModelAndView mav) {
		mav.setViewName("demo.jsp");
		return mav;
	}


	@GetMapping("/t1")
	public String t1() {
		return cacheComponent.t1();
	}

	@GetMapping("/clear")
	public String clear() {
		return cacheComponent.clear();
	}


    @GetMapping("/t2")
    public String t2() {
        return cacheComponent.t2();
    }

    /**
     * 通过AppConfig手动注入MVC请求
     *
     * @return 结果
     */
    public String t3() {
        return cacheComponent.t2();
    }


}
