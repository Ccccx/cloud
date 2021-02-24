package com.cjz.webmvc.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Objects;

/**
 * @author chengjinzhou
 * @version 1.0
 * @since 2019-09-05 14:16
 */
@Slf4j
public class HttpUtils {
    private static final FileNameMap FILE_NAME_MAP = URLConnection.getFileNameMap();

    /**
     * 获取当前HTTP请求的HttpServletRequest
     *
     * @return
     */
    public static HttpServletRequest getCurrentRequest() {
        final RequestAttributes requestAttributes = Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 识别文件的MIME类型
     *
     * @param fileName
     * @return
     */
    @SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "PMD"})
    public static String fileMIMEType(String fileName) {
        try {
            return FILE_NAME_MAP.getContentTypeFor(fileName);
        } catch (Exception e) {
            log.warn("不能识别文件类型！", e);
        }
        return null;
    }

}
