package com.cjz.webmvc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-29 14:23
 */
@Slf4j
@Configuration
public class FileUploadConfig {


    private static final Path ROOT = Paths.get("upload-dir");
    private static final Path TMP = ROOT.resolve("upload-dir-tmp");

    /**
     * 替换文件解析器，使用apache common 文件上传方式, 默认是 StandardServletMultipartResolver
     * 注意: 返回类型必须为CommonsMultipartResolver  不然会和 MultipartConfigElement 条件判断冲突
     * @see MultipartAutoConfiguration
     * @return 文件解析
     */
    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public CommonsMultipartResolver commonsMultipartResolver(ServletContext servletContext, DiskFileItemFactory diskFileItemFactory) throws IOException {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(servletContext) {
            @Override
            protected DiskFileItemFactory newFileItemFactory() {
                return diskFileItemFactory;
            }

            @Override
            protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
                /*
                 * 文件上传处理程序
                 */
                final ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
                /*
                 * 进度监听器
                 */
                upload.setProgressListener(FileProgressListener.getInstance());

                return upload;
            }
        };
        // 延迟文件解析
        multipartResolver.setResolveLazily(true);
        multipartResolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 内存最多占用2M
        multipartResolver.setMaxInMemorySize(2*1024*1024);
        // 每个文件不允许超过500M
        multipartResolver.setMaxUploadSize(500*1024*1024L);
        multipartResolver.setUploadTempDir(new FileSystemResource(Files.createDirectories(TMP).toFile()));
        return multipartResolver;
    }

    @Bean
    public FileCleanerCleanup cleanerCleanup() {
        return new FileCleanerCleanup();
    }

    @Bean
    public DiskFileItemFactory diskFileItemFactory(ServletContext servletContext) {
        //为基于磁盘的文件项创建工厂
        final DiskFileItemFactory factory = new DiskFileItemFactory();
        // 自动删除文件
        // PhantomReference 虚引用的方式来删除文件
        final FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(servletContext);
        factory.setFileCleaningTracker(fileCleaningTracker);
        return factory;
    }

}
