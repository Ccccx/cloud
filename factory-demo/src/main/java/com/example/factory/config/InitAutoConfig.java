package com.example.factory.config;

import com.example.factory.contoiller.DemoController;
import com.example.factory.project.ChildGenerationInvoker;
import com.example.factory.runner.Runner;
import com.example.factory.support.*;
import com.example.factory.support.model.InitializrMetadata;
import com.example.factory.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 基础容器自动初始化
 *
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 9:16
 */
@Slf4j
@Import({InitializrMetadata.class})
public class InitAutoConfig {

    @PostConstruct
    public void init() {
        log.info("InitAutoConfig ...");
    }

    @Bean
    @RequestScope
    public TestProperties testProperties() {
        return new TestProperties();
    }

    @Bean
    public DefaultCustomize defaultCustomize() {
        return new DefaultCustomize();
    }

    @Bean
    @ConditionalOnMissingBean(InitializrMetadataProvider.class)
    public InitializrMetadataProvider initializrMetadataProvider(InitializrMetadata initializrMetadata) {
        return () -> initializrMetadata;
    }

    @Bean
    public DemoController demoController(ApplicationContext context) {
        log.debug("demoController ...");
        ChildGenerationInvoker<BaseRequest> childGenerationInvoker = new ChildGenerationInvoker<>(context, new DefaultRequestToDescriptionConverter());
        return new DemoController(childGenerationInvoker);
    }

    @Bean
    public ContextInnerBean contextInnerBean() {
        return new ContextInnerBean();
    }

    @Bean
    public Runner runner() {
        return new Runner();
    }

    @Bean
    public ChildFinishListener childFinishListener() {
        log.debug("childFinishListener ...");
        return new ChildFinishListener();
    }


    @Bean
    public FactoryBean<ObjectFactory<Object>> factoryBean() {
        final ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
        factoryBean.setTargetBeanName("demoController");
        return factoryBean;
    }


    static class TmAutoConfigSelect implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata annotationMetadata) {
            final List<String> factories = SpringFactoriesLoader.loadFactoryNames(TmAutoConfig.class, getClass().getClassLoader());
            final String[] array = factories.toArray(new String[0]);
            return array;
        }
    }

}
