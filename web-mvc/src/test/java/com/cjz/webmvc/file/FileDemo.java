package com.cjz.webmvc.file;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.tomcat.util.security.MD5Encoder;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-19 9:15
 */
@Slf4j
public class FileDemo {
	private final static Path path = Paths.get("upload-dir");
	static {
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	@SneakyThrows
	public void writeFile() {
		final File file = new File("E:\\IDEA\\cloud\\nacos-config\\target\\nacos-config-0.0.1-SNAPSHOT.jar");

		if (file.exists()) {
			final long length = file.length();
			final int splitSize = 1024 * 1024 * 5;
			final long split = split(length, splitSize);
			final String fileName = file.getName();
			log.info("文件名: {} 文件大小: {}  分片数: {}", fileName, length, split);

			final RandomAccessFile in = new RandomAccessFile("E:\\IDEA\\cloud\\nacos-config\\target\\nacos-config-0.0.1-SNAPSHOT.jar", "rw");
			List<FileSplitInfo> fileInfo = new LinkedList<>();

			for (int i = 0; i < split; i++) {
				final int seek = splitSize * i;
				byte[] bytes  = new byte[i == split - 1 ? (int) (length - seek) : splitSize];
			    in.seek(seek);
				in.read(bytes);
				final String md5 = DigestUtils.md5DigestAsHex(bytes).toUpperCase();
				final File out = path.resolve(fileName + "_" + i).toFile();
				IOUtils.write(bytes, new FileOutputStream(out));
				fileInfo.add(new FileSplitInfo(seek, i * splitSize + bytes.length, md5, out.getName()));
			}
			fileInfo.forEach(System.out::println);
			final File fileTmp = path.resolve(fileName + "_tmp").toFile();
			IOUtils.write(JSONObject.toJSONBytes(fileInfo), new FileOutputStream(fileTmp));

		}
	}

	@Test
	public void margeFile() throws Exception{
		String fileName = "nacos-config-0.0.1-SNAPSHOT.jar";
		final File fileTmp = path.resolve(fileName + "_tmp").toFile();
		if (fileTmp.exists()) {
			final StringBuilderWriter sb = new StringBuilderWriter();
			final PrintWriter printWriter = new PrintWriter(sb);
			IOUtils.copy(new FileInputStream(fileTmp), printWriter, StandardCharsets.UTF_8);
			final List<FileSplitInfo> splitInfos = JSONObject.parseArray(sb.toString(), FileSplitInfo.class);
			final File out = path.resolve(fileName).toFile();

			final RandomAccessFile accessFile = new RandomAccessFile(out.getAbsolutePath(), "rw");
				splitInfos.forEach(splitInfo -> {
					byte[] bytes  = new byte[splitInfo.end - splitInfo.start];
					final File splitFile = path.resolve(splitInfo.getName()).toFile();
					if (splitFile.exists()) {
						log.debug("read : {}, size: {}", splitFile.getAbsolutePath(), splitFile.length());
						try {
							IOUtils.read(new FileInputStream(splitFile), bytes );
							accessFile.seek(splitInfo.start);
							accessFile.write(bytes);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			log.info("文件输出路径: {}", 	out.getAbsolutePath());
		}

	}

	public int split(long total, int split) {
		return (int) ((total - 1) / split + 1);
	}

	@Data
	@AllArgsConstructor
	public static class FileSplitInfo {
		private int start;
		private int end;
		private String md5;
		private String name;
	}

}
