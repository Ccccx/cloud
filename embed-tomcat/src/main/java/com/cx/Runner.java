package com.cx;

import com.cx.servlet.HelloServlet;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-04 16:39
 */
@Slf4j
public class Runner {
	public static void main(String[] args) throws URISyntaxException {
		// 项目目录
		log.info(System.getProperty("user.dir") + File.separator + "embed-tomcat");
		log.info(Objects.requireNonNull(EmbedStarter.class.getClassLoader().getResource("")).getPath());
		log.info(HelloServlet.class.getName());

	}
}
