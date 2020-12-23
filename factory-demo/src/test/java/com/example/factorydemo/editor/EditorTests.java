package com.example.factorydemo.editor;

import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-09 16:55
 */
@Configuration
class EditorTests {

	@Bean
	public DependsOnExoticType exoticType() {
		final DependsOnExoticType type = new DependsOnExoticType();
		return type;
	}

	public CustomEditorConfigurer customEditorConfigurer() {
		final CustomEditorConfigurer configurer = new CustomEditorConfigurer();
		Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<>();
		customEditors.put(ExoticType.class, ExoticTypeEditor.class);
		return configurer;
	}

	public class ExoticTypeEditor extends PropertyEditorSupport {
		public void setAsText(String text) {
			setValue(new ExoticType(text.toUpperCase()));
		}
	}

	public static class ExoticType {
		private final String name;

		public ExoticType(String name) {
			this.name = name;
		}
	}

	public static class DependsOnExoticType {
		private ExoticType type;

		public void setType(ExoticType type) {
			this.type = type;
		}
	}
}
