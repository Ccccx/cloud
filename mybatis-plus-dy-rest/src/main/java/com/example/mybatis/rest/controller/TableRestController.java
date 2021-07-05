package com.example.mybatis.rest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mybatis.rest.component.TableRestComponent;
import com.example.mybatis.rest.model.BaseModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

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
    @GetMapping(value = {"/page/{number:\\d+}", "/page/{number:\\d+}/size/{size:\\d+}"})
    public  IPage<BaseModel> pageQuery(
                                    @PathVariable @ApiParam("表名") String  tableName,
                                    @PathVariable  @ApiParam("页码") Integer number,
                                   @PathVariable(required = false) @ApiParam("每页条数") Integer size,
                                   HttpServletRequest request) {
        Page<BaseModel> page = new Page<>(Optional.ofNullable(number).orElse(1), Optional.ofNullable(size).orElse(10));
        return component.pageQuery(tableName, page, request);
    }

    @GetMapping(value = {"/{id}/{subTableName}/{subId}/page/{number:\\d+}", "/{id}/{subTableName}/{subId}/page/{number:\\d+}/size/{size:\\d+}"})
    public IPage<BaseModel> multiTablePageQuery(
            @PathVariable String  tableName,
            @PathVariable String  id,
            @PathVariable String  subTableName,
            @PathVariable String  subId,
            @PathVariable Integer number,
            @PathVariable(required = false) Integer size,
            HttpServletRequest request) {
        Page<BaseModel> page = new Page<>(Optional.ofNullable(number).orElse(1), Optional.ofNullable(size).orElse(10));
        return component.pageQuery(tableName, id, subTableName, subId, page, request);
    }

    @GetMapping("/clear")
    public void clear(@PathVariable String tableName) {
         component.clear(tableName);
    }

    @PostMapping
    public BaseModel save(@PathVariable String tableName, @RequestBody BaseModel model) {
        BaseModel save =   component.save(tableName, model);
        log.info("Save: {}", save.toMap());
        return save;
    }

    @PutMapping
    public BaseModel update(@PathVariable String tableName, @RequestBody BaseModel model) {
        final BaseModel update = component.update(tableName, model);
        log.info("Update: {}", update.toMap());
        return update;
    }

    @DeleteMapping("/{pk}")
    public void delete(@PathVariable String tableName, @PathVariable String pk) {
          component.delete(tableName, pk);
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
