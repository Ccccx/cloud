package com.example.factorydemo;

import com.example.factorydemo.bean.Foo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-28 16:50
 */
public class ConfigDemo {
	@Test
	public void t1() {
		Foo from = new Foo();
		from.setName("cjz");
		final PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		Foo to = new Foo();
		mapper.from(from::getName).to(to::setName);
		mapper.from(from::isFlag).to(to::setFlag);
		mapper.from(from::getAge).to(to::setAge);
		System.out.println(to);
	}
}
