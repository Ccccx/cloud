package com.example.rest;

import com.example.rest.model.Bar;
import com.example.rest.model.Foo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * https://www.baeldung.com/byte-buddy
 * @author chengjz
 * @version 1.0
 * @since 2021-02-01 16:43
 */
 class ByteBuddyTest {
     @Test
    void t1() throws IllegalAccessException, InstantiationException {
        // 1 创建二进制动态类
        DynamicType.Unloaded unloadedType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.isToString())
                .intercept(FixedValue.value("Hello World ByteBuddy!"))
                .make();
        // 2 加载到JVM中
         Class<?> dynamicType = unloadedType.load(getClass()
                 .getClassLoader())
                 .getLoaded();

         assertEquals(dynamicType.newInstance().toString(), "Hello World ByteBuddy!");
    }

    @Test
    void t2() throws IllegalAccessException, InstantiationException {
        String r = new ByteBuddy()
                .subclass(Foo.class)
                .method(named("sayHelloFoo")
                        .and(isDeclaredBy(Foo.class)
                                .and(returns(String.class))))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance()
                .sayHelloFoo();
        assertEquals(r, Bar.sayHelloBar());
    }

    /**
     * 向类添加一个新方法（和一个字段）
     * @throws Exception 异常
     */
    @Test
    void t4() throws Exception {
        Class<?> type = new ByteBuddy()
                .subclass(Object.class)
                .name("MyClassName")
                .defineMethod("custom", String.class, Modifier.PUBLIC)
                .intercept(MethodDelegation.to(Bar.class))
                .defineField("x", String.class, Modifier.PUBLIC)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Method m = type.getDeclaredMethod("custom", null);
        assertEquals(m.invoke(type.newInstance()), Bar.sayHelloBar());
        assertNotNull(type.getDeclaredField("x"));
    }

    @Test
    void t3() {
        final RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        final ResponseEntity<Void> exchange = restTemplate.exchange("http://127.0.0.1:8080/logi?username={username}&password={password}", HttpMethod.GET, requestEntity, Void.class, "111", "222");
        System.out.println(exchange.getStatusCode());
    }
}
