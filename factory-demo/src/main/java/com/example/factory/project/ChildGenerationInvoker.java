package com.example.factory.project;

import com.example.factory.support.ChildFinishEvent;
import com.example.factory.support.DefaultRequestToDescriptionConverter;
import com.example.factory.support.InitializrMetadataProvider;
import com.example.factory.support.model.InitializrMetadata;
import com.example.factory.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:10
 */
@Slf4j
public class ChildGenerationInvoker<R extends BaseRequest> {

    private final ApplicationContext parentApplicationContext;

    private final ApplicationEventPublisher eventPublisher;
    private final DefaultRequestToDescriptionConverter converter;

    public ChildGenerationInvoker(ApplicationContext parentApplicationContext, DefaultRequestToDescriptionConverter converter) {
        this(parentApplicationContext, parentApplicationContext, converter);
    }

    public ChildGenerationInvoker(ApplicationContext parentApplicationContext,
                                  ApplicationEventPublisher eventPublisher,
                                  DefaultRequestToDescriptionConverter converter) {
        this.parentApplicationContext = parentApplicationContext;
        this.eventPublisher = eventPublisher;
        this.converter = converter;
    }


    public Object invokeBuildGeneration(R request) {
        final InitializrMetadata initializrMetadata = this.parentApplicationContext.getBean(InitializrMetadataProvider.class).get();
        final ProjectDescription description = this.converter.convert(request);
        final ChildApplicationContextGenerator childApplicationContextGenerator = new ChildApplicationContextGenerator((projectGenerationContext) -> customizeChildGenerationContext(projectGenerationContext, initializrMetadata));
        return childApplicationContextGenerator.generator(description, process(request));
    }

    private void customizeChildGenerationContext(AnnotationConfigApplicationContext context, InitializrMetadata metadata) {
        context.setParent(this.parentApplicationContext);
        context.registerBean(InitializrMetadata.class, () -> metadata);
    }

    /**
     * 子上下文完成后，会回调业务处理
     *
     * @param request 原始请求
     * @return ig
     */
    private ProjectAssetGenerator<Object> process(R request) {
        return (context) -> {
            Object content = doProcess(context);
            publishFinishEvent(request, context);
            return content;
        };
    }

    /**
     * 模拟业务处理。这里只做了简单打印
     *
     * @param context 上下文 {@link ChildApplicationContext}
     * @return 返回结果
     * @throws IOException ig
     */
    private Object doProcess(ChildApplicationContext context) throws IOException {
        ProjectDescription description = context.getBean(ProjectDescription.class);
        log.debug(description.toString());
        return description;
    }

    private void publishFinishEvent(R request, ChildApplicationContext context) {
        log.debug("publishFinishEvent ...");
        final InitializrMetadata metadata = context.getBean(InitializrMetadata.class);
        ChildFinishEvent event = new ChildFinishEvent(request, metadata);
        this.eventPublisher.publishEvent(event);
    }
}
