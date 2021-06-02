package com.cjz.webmvc.base.contoiller;

import com.cjz.webmvc.base.component.StorageComponent;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-18 17:10
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/upload")
public class UploadController {
    private final StorageComponent component;

    public UploadController(StorageComponent component) {
        this.component = component;
    }

    @PostMapping(path = "/1", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传文件1")
    public String uploadFile(@RequestParam @ApiParam(required = true, allowMultiple = true) MultipartFile[] file) {
        component.upload(file);
        return "SUCCESS";
    }


    @PostMapping(path = "/2", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传文件2")
    public String uploadFile1(HttpServletRequest request) {
        component.upload(request);
        return "SUCCESS";
    }

}
