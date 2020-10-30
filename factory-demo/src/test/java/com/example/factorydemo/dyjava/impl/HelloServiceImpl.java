package com.example.factorydemo.dyjava.impl;

import com.example.factorydemo.dyjava.IHelloService;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-27 15:45
 */
public class HelloServiceImpl implements IHelloService {
	@Override
	public void sayHello(String name) {
		System.out.println(String.format("%s say hello [by default]", name));
	}
}
