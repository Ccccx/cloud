package com.example.factorydemo.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-10 9:10
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
class AopTests {


	@Bean
	public Foo foo() {
		return new Foo();
	}

	@Test
	void t1() {
		final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopTests.class);
		final Foo foo = context.getBean(Foo.class);
		foo.say("aspect 测试");
	}


	@Component
	@Aspect
	public static class ConcurrentOperationExecutor implements Ordered {
		private static final int DEFAULT_MAX_RETRIES = 2;

		private int maxRetries = DEFAULT_MAX_RETRIES;
		private final int order = 1;

		public void setMaxRetries(int maxRetries) {
			this.maxRetries = maxRetries;
		}

		public int getOrder() {
			return this.order;
		}

		/**
		 * 任何通知方法都可以将类型的参数声明为它的第一个参数 org.aspectj.lang.JoinPoint（请注意，在通知周围需要声明type的第一个参数ProceedingJoinPoint，它是的子类JoinPoint。该 JoinPoint接口提供了许多有用的方法：
		 * getArgs()：返回方法参数。
		 * getThis()：返回代理对象。
		 * getTarget()：返回目标对象。
		 * getSignature()：返回建议使用的方法的描述。
		 * toString()：打印有关所建议方法的有用描述。
		 */
		@Around("@annotation(com.example.factorydemo.proxy.AopTests.LogTrace)")
		public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
			final long start = System.currentTimeMillis();
			log.info("{}", pjp.getArgs());
			log.info("{}", pjp.getThis());
			log.info("{}", pjp.getTarget());
			log.info("{}", pjp.toString());
			try {
				return pjp.proceed();
			} finally {
				log.info("耗时: {} ------------------------", System.currentTimeMillis() - start);
			}
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface LogTrace {

	}

	public static class Foo {

		@LogTrace
		public void say(String str) {
			System.out.println("Foo say: " + str);
		}
	}


}
