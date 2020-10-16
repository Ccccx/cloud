package com.example.factorydemo.beaninit;

import com.example.factorydemo.bean.Bar;
import com.example.factorydemo.bean.Foo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-08-14 13:41
 */
@Data
public class BeanInitDemo {

	@Autowired
	private Bar bar;

	@Autowired
	private Foo foo;

	public static void main(String[] args) {
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(BeanInitDemo.class);
		context.refresh();
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
		foo1();
		bar.setFoo(foo());
		return bar;
	}


	@Bean
	public Foo foo() {
		System.out.println("foo init ...");
		return new Foo();
	}

	public Foo foo1() {
		System.out.println("foo1 init ...");
		return new Foo();
	}

}
