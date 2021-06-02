package com.cjz.webmvc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.ProgressListener;

import java.math.BigDecimal;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-26 18:10
 */
@Slf4j
public class FileProgressListener implements ProgressListener {

    private static FileProgressListener LISTENER = new FileProgressListener();

    private long megaBytes = -1;

    private FileProgressListener() {
    }

    public static ProgressListener getInstance() {
        return LISTENER;
    }

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
}
