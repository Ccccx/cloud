package com.example.mybatis.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mybatis.rest.component.TableRestComponent;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.support.StringMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 17:23
 */
@Slf4j
@Api(tags = {"动态表单"})
@RestController
@RequestMapping("/form/{tableName}")
public class TableRestController {

    TableRestComponent component;

    public TableRestController(TableRestComponent component) {
        this.component = component;
    }

    @GetMapping
    public List<BaseModel> selectList(@PathVariable String  tableName) {
        return component.selectList(tableName);
    }


    @ApiOperation("单表分页查询")
    @GetMapping("/page")
    public  IPage<BaseModel> pageQueryV2( @PathVariable @ApiParam("表名") String  tableName, HttpServletRequest request) {
        return component.pageQueryV2(tableName, request);
    }



    @PostMapping
    public List<BaseModel> save(@PathVariable String tableName, @RequestBody ArrayList<StringMap> req) {
        return  component.save(tableName, req);
    }

    @PutMapping
    public List<BaseModel> update(@PathVariable String tableName, @RequestBody  ArrayList<StringMap> req) {
        return component.update(tableName, req);
    }

    @DeleteMapping
    public void delete(@PathVariable String tableName, @RequestBody ArrayList<String> pks) {
          component.delete(tableName, pks);
    }

    /**
     * 导出模版
     * @param response
     */
    @GetMapping("/export")
    public void exportExcel(@PathVariable String tableName, HttpServletResponse response){
        try {
            //模拟从数据库获取需要导出的数据
            component.exportExcel(tableName,  response);
        } catch (Exception e) {
            log.info("导出[{}]失败", tableName, e);
        }
    }

    /**
     * 导入excel
     * @param file 文件
     *
     */
    @PostMapping(value = "/import")
    public void importExcel(@PathVariable String tableName, @RequestParam("file") MultipartFile file) {
        try {
            component.importExcel(tableName, file);
        } catch (Exception e) {
            log.info("导入[{}]失败", tableName, e);
        }
    }
}
