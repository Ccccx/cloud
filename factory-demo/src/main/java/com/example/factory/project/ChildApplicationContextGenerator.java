package com.example.factory.project;

import com.example.factory.project.model.MutableProjectDescription;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 9:30
 */
public class ChildApplicationContextGenerator {

	private final Consumer<ChildApplicationContext> contextConsumer;
	private final Supplier<? extends ChildApplicationContext> contextFactory;

	public ChildApplicationContextGenerator(Consumer<ChildApplicationContext> contextConsumer) {
		this(contextConsumer, defaultContextFactory());
	}

	public ChildApplicationContextGenerator(Consumer<ChildApplicationContext> contextConsumer, Supplier<? extends ChildApplicationContext> contextFactory) {
		this.contextConsumer = contextConsumer;
		this.contextFactory = contextFactory;
	}

	private static Supplier<ChildApplicationContext> defaultContextFactory() {
		return () -> {
			ChildApplicationContext context = new ChildApplicationContext();
			context.setAllowBeanDefinitionOverriding(false);
			return context;
		};
	}

	public <T> T generator(ProjectDescription projectDescription, ProjectAssetGenerator<T> projectAssetGenerator) {
		try (ChildApplicationContext context = this.contextFactory.get()) {
			context.registerBean(ProjectDescription.class, resolve(projectDescription, context));
			context.register(CoreConfig.class);
			this.contextConsumer.accept(context);
			context.refresh();
			try {
				return projectAssetGenerator.generate(context);
			} catch (IOException ex) {
				throw new ChildGenerationException("Failed to generate project", ex);
			}
		}

	}

	private Supplier<ProjectDescription> resolve(ProjectDescription description, ChildApplicationContext context) {
		return () -> {
			if (description instanceof MutableProjectDescription) {
				context.getBeanProvider(ProjectDescriptionCustomizer.class).orderedStream()
						.forEach((customizer) -> customizer.customize((MutableProjectDescription) description));
			}
			return description;
		};
	}

	@Configuration
	@Import(ChildGenerationImportSelector.class)
	static class CoreConfig {

	}

	static class ChildGenerationImportSelector implements ImportSelector {
		@Override
		public String[] selectImports(AnnotationMetadata annotationMetadata) {
			final List<String> factories = SpringFactoriesLoader.loadFactoryNames(ChildGenerationConfiguration.class, getClass().getClassLoader());
			final String[] array = factories.toArray(new String[0]);
			return array;
		}
	}

	private static class ChildGenerationException extends com.example.factory.project.ChildGenerationException {
		public ChildGenerationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
