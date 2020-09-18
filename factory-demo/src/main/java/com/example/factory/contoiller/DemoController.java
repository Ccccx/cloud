package com.example.factory.contoiller;

import com.example.factory.config.ContextInnerBean;
import com.example.factory.project.ChildGenerationInvoker;
import com.example.factory.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.annotation.Resource;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 9:18
 */
@RestController
@Slf4j
public class DemoController {

	private final ChildGenerationInvoker<BaseRequest> childGenerationInvoker;


	@Resource
	private ContextInnerBean innerBean;


	public DemoController(ChildGenerationInvoker<BaseRequest> childGenerationInvoker) {
		this.childGenerationInvoker = childGenerationInvoker;
	}

	@GetMapping
	public Object info(BaseRequest request) {
		log.info("BeanFactory: {}", innerBean.getBeanFactory().getClass().getSimpleName());
		log.info("ObjectFactory: {}", innerBean.getObjectFactory());
		log.info("Bean Count: {}", innerBean.getApplicationContext().getBeanDefinitionCount());
		log.info("Environment : {}", innerBean.getEnvironment());
//		final UriComponents info = MvcUriComponentsBuilder.fromMethodName(DemoController.class, "info", request).build();
//		System.out.println("URL: " + info.toString());
		return childGenerationInvoker.invokeBuildGeneration(request);
	}



}
