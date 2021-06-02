package com.example.factorydemo.bean;


import lombok.Getter;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-03-04 18:42
 */
@Getter
public class SpringContextUtils {
	public static BeanFactory beanFactory;
	public static Foo foo;
	public void setBeanFactory(BeanFactory beanFactory) {
		SpringContextUtils.beanFactory = beanFactory;
	}

	public static void setFoo(Foo foo) {
		SpringContextUtils.foo = foo;
	}

	public static <T> T getBean(Class<T> clz) {
		return beanFactory.getBean(clz);
	}
}
