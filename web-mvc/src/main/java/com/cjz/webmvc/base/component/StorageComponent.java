package com.cjz.webmvc.base.component;

import com.cjz.webmvc.config.FileProgressListener;
import com.cjz.webmvc.config.FileUploadConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-18 17:12
 */
@Slf4j
@Component
public class StorageComponent {

    @Resource
    private DiskFileItemFactory fileItemFactory;

    public void upload(MultipartFile[] files) {
        for (MultipartFile file : files) {
            log.info("File Name: {}",  file.getName());
        }
    }

    public void upload(HttpServletRequest request) {
        // 检查是否是上传文件请求
        final boolean multipartContent = ServletFileUpload.isMultipartContent(request);
        Assert.isTrue(multipartContent, "非法请求");

        /*
         * 创建一个新的文件上传处理程序
         */
        final ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
        /*
         * 进度监听器
         */
        upload.setProgressListener(FileProgressListener.getInstance());
        /*
         * 临时保存文件到磁盘
         */
        final ServletRequestContext context = new ServletRequestContext(request);
        try {
            final List<FileItem> fileItems = upload.parseRequest(context);
            fileItems.forEach(fileItem -> {
                log.info("Form field : {}  File Name: {}", fileItem.getFieldName(), fileItem.getName());
            });
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
