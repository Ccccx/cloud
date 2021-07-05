package com.cjz.webmvc.utils;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用Cglib高效的复制对象，不使用Spring的BeanCopy。
 * 注意：对象使用了Lombok注解时，不要有{@link Accessors}注解，否则无法复制。
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-04-01 14:46
 */
@Slf4j
public class FastCopyUtils {

	protected static final Map<String, BeanCopier> CACHE_MAP = new HashMap<>();

	private FastCopyUtils() {
	}

	private static void addBeanCopy(Class<?> clz1, Class<?> clz2) {
		addBeanCopy(clz1, clz2, false);
	}

	private static void addBeanCopy(Class<?> clz1, Class<?> clz2, boolean useConverter) {
		CACHE_MAP.put(buildCacheKey(clz1, clz2), BeanCopier.create(clz1, clz2, useConverter));
	}

	public static <T> T copy(Object form, Class<T> to) {
		T targetSource = null;
		try {
			targetSource = to.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			log.error("创建对象发生异常: ", e);
		}

		return copy(form, to, targetSource, null);
	}

	public static <F, T> List<T> listCopy(List<F> formList, Class<T> to) {
		List<T> toList = new ArrayList<>(formList.size());
		formList.forEach(v -> toList.add(copy(v, to)));
		return toList;
	}

	public static Object copy(Object form, Class<?> to, Object targetSource) {
		return copy(form, to, targetSource, null);
	}

	public static <T> T copy(Object form, Class<?> to, T targetSource, Converter converter) {
		if (form == null) {
			return targetSource;
		}
		final Class<?> formClass = form.getClass();
		String cacheKey = buildCacheKey(formClass, to);
		if (!CACHE_MAP.containsKey(cacheKey)) {
			addBeanCopy(formClass, to);
		}
		CACHE_MAP.get(cacheKey).copy(form, targetSource, converter);
		return targetSource;
	}

	private static String buildCacheKey(Class<?> clz1, Class<?> clz2) {
		return clz1.getSimpleName() + "_" + clz2.getSimpleName();
	}

}
