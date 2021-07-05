package com.example.mybatis.rest.controller;

import com.example.mybatis.rest.component.TableManagerComponent;
import com.example.mybatis.rest.component.TableRestComponent;
import com.example.mybatis.rest.model.TableConfig;
import com.example.mybatis.rest.model.TableConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-06-30 15:55
 */
@Api(tags = {"表信息管理"})
@RestController
@RequestMapping("/table")
public class TableManagerController {

    private TableManagerComponent managerComponent;

    public TableManagerController(TableManagerComponent managerComponent) {
        this.managerComponent = managerComponent;
    }

    @ApiOperation("查看表配置元信息")
    @GetMapping("/{tableName}/config")
    public TableConfigVo query(@PathVariable @ApiParam("表名")  String tableName) {
        return  managerComponent.getConfigByTableName(tableName);
    }

    @ApiOperation("保存表配置元信息")
    @PutMapping("/config")
    public TableConfigVo save(@RequestBody TableConfigVo req) {
        return managerComponent.save(req);
    }
}
