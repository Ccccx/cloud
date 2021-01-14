package com.example.factorydemo.dyjava;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-06 10:04
 */
class JavassistClient {
	@Test
	@SneakyThrows
	void t1() {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get("com.example.factorydemo.dyjava.impl.HelloServiceImpl");
		CtMethod ctMethod = cc.getDeclaredMethod("sayHello", new CtClass[]{pool.get("java.lang.String")});
		ctMethod.insertBefore("System.out.println(\"insert before by Javassist\");");
		ctMethod.insertAfter("System.out.println(\"insert after by Javassist\");");
		Class<?> klass = cc.toClass();
		System.out.println(klass.getName());
		IHelloService helloService = (IHelloService) klass.getDeclaredConstructor().newInstance();
		helloService.sayHello("throwable");
	}

	@Test
	@SneakyThrows
	void t2() {
		ClassPool pool = ClassPool.getDefault();
		final CtClass ctClass = pool.makeClass("com.example.test");

	}
}
