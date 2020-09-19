package com.cjz.webmvc.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.util.Streams;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-18 17:12
 */
@Slf4j
@Component
public class StorageComponent {

	ServletContext servletContext;

	public StorageComponent(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void upload(HttpServletRequest request) {
		// 检查是否是上传文件请求
		final boolean multipartContent = ServletFileUpload.isMultipartContent(request);
		Assert.isTrue(multipartContent, "非法请求");

		//为基于磁盘的文件项创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//配置存储库（以确保使用安全的临时位置）
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);
		//创建一个新的文件上传处理程序
		ServletFileUpload upload = new ServletFileUpload();
		try {
			final FileItemIterator iter  = upload.getItemIterator(request);
			while (iter .hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					System.out.println("Form field " + name + " with value " + Streams.asString(stream) + " detected.");
				} else { System.out.println("File field " + name + " with file name " + item.getName() + " detected.");
					// Process the input stream
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
