package com.cjz.webmvc.path;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-17 10:35
 */
@Slf4j
public class PathTest {

	@Test
	public void t1() throws Exception {
		final URI uri = this.getClass().getResource("").toURI();
		final Path txt = Paths.get(uri).resolve("坐标点.csv");
		final File file = txt.toFile();
		log.info("file exist : {}", file.exists());
		log.info("\n{}", FileUtils.readFileToString(file, StandardCharsets.UTF_8));
		log.info("---------------------------------");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			log.info(line);
		}
	}


	@ParameterizedTest
	@CsvFileSource(resources = "/坐标点.csv")
	public void t2(String lng, String lat) {
		log.info("lng : {} lat: {}", lng, lat);
	}

	@Test
	public void t3() {
		final Path rootPath = Paths.get("123");
		final Path subPath = rootPath.resolve("456");
		log.info("fullPath: {}", subPath.toUri());
	}

}
