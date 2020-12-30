package com.example.factory.config;

import com.example.factory.contoiller.DemoController;
import com.example.factory.project.ChildGenerationInvoker;
import com.example.factory.runner.Runner;
import com.example.factory.support.ChildFinishListener;
import com.example.factory.support.DefaultRequestToDescriptionConverter;
import com.example.factory.support.InitializrMetadataProvider;
import com.example.factory.support.model.InitializrMetadata;
import com.example.factory.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 基础容器自动初始化
 *
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 9:16
 */
@Slf4j
@Configuration
@Import(InitializrMetadata.class)
public class InitAutoConfig {


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

}
