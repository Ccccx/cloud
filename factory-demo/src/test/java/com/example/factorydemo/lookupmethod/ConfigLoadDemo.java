package com.example.factorydemo.lookupmethod;

import com.example.factorydemo.bean.Foo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Random;

/**
 * 原型模式,每次都会返回新示例
 * {@link AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors(Class, String)} 回去解析@Lookup方法
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-07-16 9:58
 */
@Slf4j
@Configuration
public class ConfigLoadDemo {

	public static void main(String[] args) {
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(ConfigLoadDemo.class);
		context.refresh();
		final ConfigLoadDemo bean = context.getBean(ConfigLoadDemo.class);
		// 第一种方式
		System.out.println(bean.getFoo());
		System.out.println(bean.getFoo());
//		System.out.println(bean.foo());
//		System.out.println(bean.foo());

		// 第二种方式
		final Bar bar = context.getBean(Bar.class);
		System.out.println(bar.createFoo());
		System.out.println(bar.createFoo());
	}

	@Bean
	@Scope("prototype")
	public Foo foo() {
		log.info("foo init ...");
		return new Foo("cjz", true, new Random(System.currentTimeMillis()).nextInt());
	}

	@Lookup("foo")
	public Foo getFoo() {
		return null;
	}


	public abstract static class Bar {
		protected abstract Foo createFoo();
	}

	/**
	 * 通过依赖的形式来获取原型bean
	 */
	@Bean
	public Bar bar() {
		return new Bar() {
			@Override
			protected Foo createFoo() {
				return foo();
			}
		};
	}

}
