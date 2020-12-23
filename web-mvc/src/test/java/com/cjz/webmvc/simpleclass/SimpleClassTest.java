package com.cjz.webmvc.simpleclass;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cjz.webmvc.model.Par;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-07 9:19
 */
@Slf4j
public class SimpleClassTest {

	@Test
	public void t1() {
		Set<Class<?>> classSet = new LinkedHashSet<Class<?>>() {
			{
				add(boolean.class);
				add(boolean[].class);
				add(long.class);
				add(long[].class);
				add(short.class);
				add(short[].class);
				add(int.class);
				add(int[].class);
				add(byte.class);
				add(byte[].class);
				add(float.class);
				add(float[].class);
				add(double.class);
				add(double[].class);
				add(char.class);
				add(char[].class);
				add(Boolean.class);
				add(Long.class);
				add(Short.class);
				add(Integer.class);
				add(Byte.class);
				add(Float.class);
				add(Double.class);
				add(Character.class);
				add(String.class);
				add(Date.class);
				add(Locale.class);
				add(Class.class);
				add(Enum.class);
			}
		};
		Assert.assertNotNull(classSet);
	}

	@Test
	public void t2() {
		final Foo<String> foo = new Foo<String>() {
		};
		log.info("{}", foo);
		final Bar<Date> date = new Bar<>(Date.class);
		log.info("{}", date);
		log.info("{}", date.getTClz());
	}

	@Test
	public void t3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final LambdaQueryWrapper<Par> wrapper = Wrappers.lambdaQuery(Par.class);
		final Method getEntityClass = AbstractWrapper.class.getDeclaredMethod("getEntityClass");
		getEntityClass.setAccessible(true);
		final Object invoke = getEntityClass.invoke(wrapper);
		System.out.println(invoke);
		final Class clz = (Class) invoke;
		final Optional<Component> annotation = AnnotationUtils.findAnnotation(clz, Component.class);
		System.out.println(annotation);
	}


	@Data
	public static class Foo<T> {
		final Class<? super T> rawType;
		final Type type;
		final int hashCode;
		private T t;

		protected Foo() {
			final Class<? extends Foo> subClass = getClass();
			final Type superclass = subClass.getGenericSuperclass();
			ParameterizedType parameterized = (ParameterizedType) superclass;
			type = parameterized.getActualTypeArguments()[0];
			rawType = (Class<? super T>) type;
			hashCode = type.hashCode();
		}
	}


	@Data
	public static class Bar<T> {
		final Class<T> tClz;
		private T t;
		private String str;

		public Bar(Class<T> tClz) {
			this.tClz = tClz;
		}
	}

}
