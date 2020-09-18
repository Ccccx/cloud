package com.example.factory.runner;

import com.example.factory.contoiller.DemoController;
import com.example.factory.vo.BaseRequest;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.annotation.Resource;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-01-11 9:40
 */
public class Runner implements ApplicationRunner {

	@Resource
	private DemoController demoController;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		BaseRequest request = new BaseRequest();
		request.setMsg("Hello Context!");
		demoController.info(request);

	}
}
