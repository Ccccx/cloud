package com.example.factory.contoiller;

import com.example.factory.config.ContextInnerBean;
import com.example.factory.project.ChildGenerationInvoker;
import com.example.factory.support.TestProperties;
import com.example.factory.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestScope;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @Resource
    private TestProperties testProperties;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private Map<String, Scope> scopes = new LinkedHashMap<>(8);

    public DemoController(ChildGenerationInvoker<BaseRequest> childGenerationInvoker) {
        this.childGenerationInvoker = childGenerationInvoker;
    }

    @GetMapping
    public Object info(BaseRequest request) {
        log.info("BeanFactory: {}", innerBean.getBeanFactory().getClass().getSimpleName());
        log.info("ObjectFactory: {}", innerBean.getObjectFactory());
        log.info("Bean Count: {}", innerBean.getApplicationContext().getBeanDefinitionCount());
        log.info("Environment : {}", innerBean.getEnvironment());
//        final UriComponents info = MvcUriComponentsBuilder.fromMethodName(DemoController.class, "info", request).build();
//        System.out.println("URL: " + info.toString());
        return childGenerationInvoker.invokeBuildGeneration(request);
    }

    @GetMapping("/test")
    public TestProperties test() {
        log.info("{}", testProperties);
        return new TestProperties();
    }


}
