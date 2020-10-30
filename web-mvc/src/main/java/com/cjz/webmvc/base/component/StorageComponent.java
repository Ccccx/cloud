package com.cjz.webmvc.base.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.util.Streams;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.ProgressListener;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-18 17:12
 */
@Slf4j
@Component
public class StorageComponent {

	private final ServletContext servletContext;
	private static final Path ROOT = Paths.get("upload-dir");
	private static final Path TMP = ROOT.resolve("upload-dir-tmp");
	private static final DiskFileItemFactory FILE_FACTORY = new DiskFileItemFactory();

	static {
		try {
			Files.createDirectories(TMP);
		} catch (IOException e) {
			log.warn("创建上传目录异常");
		}
		//为基于磁盘的文件项创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setDefaultCharset(StandardCharsets.UTF_8.name());
		factory.setSizeThreshold(10240);
		//配置存储库（以确保使用安全的临时位置）
		// File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(TMP.toFile());

	}

	public StorageComponent(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void upload(HttpServletRequest request) {
		// 检查是否是上传文件请求
		final boolean multipartContent = ServletFileUpload.isMultipartContent(request);
		Assert.isTrue(multipartContent, "非法请求");

		/**
		 * 创建一个新的文件上传处理程序
		 */
		ServletFileUpload upload = new ServletFileUpload();
		/**
		 * 进度监听器
		 */
		ProgressListener progressListener = new ProgressListener() {
			private long megaBytes = -1;

			@Override
			public void update(long pBytesRead, long pContentLength, int pItems) {
				long mBytes = pBytesRead / 1000000;
				if (megaBytes == mBytes) {
					return;
				}
				megaBytes = mBytes;
				if (pContentLength == -1) {
					log.info("正在处理第{}个文件， 已经读取了{}个字节", pItems, pBytesRead);
				} else {
					BigDecimal process = new BigDecimal(pBytesRead).divide(new BigDecimal(pContentLength), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
					log.info("正在处理第{}个文件，已经读取了{}个字节，正在读取{}个字节, 当前进度 {}%", pItems, pBytesRead, pContentLength, process);
				}
			}
		};
		upload.setFileItemFactory(FILE_FACTORY);
		upload.setProgressListener(progressListener);
		try {
			final FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					log.info("Form field : {}  Value: {}", name, Streams.asString(stream));
				} else {
					log.info("Form field : {}  File Name: {}", name, item.getName());
					IOUtils.copy(stream, new FileOutputStream(ROOT.resolve(item.getName()).toFile()));
				}
				stream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
