package com.example.factorydemo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-28 15:09
 */
public class JdkProxy implements InvocationHandler {

	private Object subject;

	public JdkProxy(Object subject) {
		this.subject = subject;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//　　在代理真实对象前我们可以添加一些自己的操作
		before(proxy, method, args);
		//    当代理对象调用真实对象的方法时，其会自动的跳转到代理对象关联的handler对象的invoke方法来进行调用
		final Object result = method.invoke(subject, args);
		//　　在代理真实对象后我们也可以添加一些自己的操作
		return after(proxy, method, args, result);
	}

	private void before(Object proxy, Method method, Object[] args) {
		System.err.println("before >>>>>>>>>>>>>>>>>>> start");
		System.err.println(proxy.getClass().getName());
		System.err.println(method);
		for (Object arg : args) {
			System.err.println(arg);
		}
		System.err.println("before >>>>>>>>>>>>>>>>>>> end  ");
	}

	private Object after(Object proxy, Method method, Object[] args, Object result) {
		System.err.println("after >>>>>>>>>>>>>>>>>>> start");
		System.err.println(result);
		System.err.println("after >>>>>>>>>>>>>>>>>>> end  ");
		return result;
	}


}
