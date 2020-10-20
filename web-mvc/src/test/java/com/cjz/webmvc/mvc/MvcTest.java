package com.cjz.webmvc.mvc;

import com.cjz.webmvc.WebMvcApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-17 11:36
 */
public class MvcTest extends WebMvcApplicationTests {
	@Test
	public void t1() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/cache/t2"))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("t2休眠5秒结束"));
	}
}
