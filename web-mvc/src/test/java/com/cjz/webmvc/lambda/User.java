package com.cjz.webmvc.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

/**
 * @author chengjinzhou
 * @version 1.0
 * @date 2019-04-14 15:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
	private Integer id = 1;
	private String userId;
	private String name;
	private String gender;
	private Integer age;
	private String address;
	private Integer balance;
	private String phone;

	public static List<User> randUserData(int size) {
		List<String> names = new ArrayList<>();
		names.add("张三");
		names.add("李四");
		names.add("王五");
		names.add("赵六");
		names.add("小红");
		names.add("小明");

		List<String> genders = new ArrayList<>();
		genders.add("男");
		genders.add("女");

		List<String> address = new ArrayList<>();
		address.add("北京");
		address.add("深圳");
		address.add("上海");
		address.add("杭州");
		address.add("广州");
		address.add("郑州");

		Random random = new Random(System.currentTimeMillis());
		List<User> list = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			list.add(new User(i,
					UUID.randomUUID().toString(),
					names.get(random.nextInt(names.size())),
					genders.get(random.nextInt(genders.size())),
					random.nextInt(100),
					address.get(random.nextInt(address.size())),
					random.nextInt(1000000),
					random.nextInt(999999) + ""));
		}

		return list;
	}

	public static void print(Collection<User> users) {
		users.forEach(System.out::println);
	}
}
