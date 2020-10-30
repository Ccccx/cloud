package com.example.factorydemo.beaninit;

import com.example.factorydemo.bean.Bar;
import com.example.factorydemo.bean.Foo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-08-14 13:41
 */
@Data
@Configuration
public class BeanInitDemo {

	@Autowired
	private Bar bar;

	@Autowired
	private Foo foo;

	public static void main(String[] args) {
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(BeanInitDemo.class);
		// 测试通过代码方式手动注入
		final BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(AutoModeBean.class);
		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		beanDefinition.setLazyInit(true);
		context.registerBeanDefinition("autoModeBean", beanDefinition.getBeanDefinition());
		context.refresh();

		System.out.println(context.getBean("autoModeBean"));
		// 测试引用问题  BeanInitDemo 加上@Configuration 为true ,  @Configuration(proxyBeanMethods = false) 或不加为false
		// @Configuration 为@Bean 做了方法拦截,保证是单例
		final Foo foo = context.getBean(Foo.class);
		final Bar bar = context.getBean(Bar.class);
		System.out.println("是否为相同引用 ?   " + (foo == bar.getFoo()));
	}

	@PostConstruct
	public void init() {
		System.out.println("是否为相同引用 ?   " + (foo == bar.getFoo()));
	}

	@Bean
	public Bar bar() {
		final Bar bar = new Bar();
		bar.setFoo(foo());
		return bar;
	}


	@Bean
	public Foo foo() {
		System.out.println("foo init ...");
		return new Foo();
	}


	@Data
	public static class AutoModeBean {
		@Autowired
		private Bar bar;
		@Autowired
		private Foo foo;
	}

}
