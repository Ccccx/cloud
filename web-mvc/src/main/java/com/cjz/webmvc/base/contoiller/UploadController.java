package com.cjz.webmvc.base.contoiller;

import com.cjz.webmvc.base.component.StorageComponent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-18 17:10
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    private final StorageComponent component;

    public UploadController(StorageComponent component) {
        this.component = component;
    }

    @PostMapping
    public String uploadFile(HttpServletRequest request) {
        component.upload(request);
        return "SUCCESS";
    }

}
