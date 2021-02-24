package com.example.mybatis.rest.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.example.mybatis.rest.config.DyMybatisConfiguration;
import com.example.mybatis.rest.model.*;
import com.example.mybatis.rest.support.TableMetaManager;
import com.example.mybatis.rest.support.wrapper.QueryWrapperBuilder;
import com.example.mybatis.rest.utils.FileWithExcelUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers.Base;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 16:14
 */
@Slf4j
@Component
public class TableManagerComponent {



    @Resource
    private DefaultListableBeanFactory beanFactory;

    @Resource
    private TableMetaManager classInject;

    @Resource
    private DyMybatisConfiguration dyMybatisConfiguration;

    @Resource
    private QueryWrapperBuilder builder;

    @Resource
    private ObjectMapper objectMapper;

    public List<BaseModel> selectList(String  tableName) {
        return getMapperByTableName(tableName).selectList(Wrappers.lambdaQuery());
    }

    public void clear(String  tableName) {
        TableMetaConfig config = null;
        try {
            //final TableMetadata metadata = TABLES.get(tableName);
            config = classInject.loadTableMeta(tableName);
            beanFactory.removeBeanDefinition(config.getMapperClass().getName());
            dyMybatisConfiguration.removeMappedStatement(config.getMapperClass());
            dyMybatisConfiguration.removeMapper(config.getMapperClass());
            //classInject.remove(metadata);
        } catch (Exception e) {
            log.info("清空 发生异常",  e);
        }
    }

    public IPage<BaseModel> pageQuery(String  tableName, Page<BaseModel> page, HttpServletRequest request) {
        final TableMetaConfig metaConfig = getConfigByTableName(tableName);
        final QueryWrapper<BaseModel> wrapper = builder.buildQueryWrapper(metaConfig, request.getParameterMap());
        return getMapperByTableName(tableName).selectPage(page, wrapper);
    }

    public IPage<BaseModel> pageQuery(String tableName, String id, String subTableName, String subId, Page<BaseModel> page, HttpServletRequest request) {
        final IPage<BaseModel> list = pageQuery(tableName, page, request);
        final List<BaseModel> records = list.getRecords();
        final TableMetaConfig subTableMetaConfig = getConfigByTableName(subTableName);
        for (BaseModel record : records) {
            final Map<String, Object> objectMap = record.toMap();
            final Object idVal = objectMap.get(id);
            final QueryWrapper<BaseModel> wrapper = builder.buildQueryWrapper(subTableMetaConfig, subId, idVal.toString());
            List<BaseModel>  subTable = getMapperByTableName(subTableName).selectList(wrapper);
            record.setSubTable(subTable);
        }
        return list;
    }

    public BaseModel save(String  tableName, BaseModel model) {
        final TableMetaConfig dyRest = getConfigByTableName(tableName);
        try {
            final BaseModel o =(BaseModel)objectMapper.convertValue(model.getParamMap(), dyRest.getModelClass());
            getMapper(dyRest).insert(o);
            return o;
        } catch (Exception e) {
            log.info("保存 发生异常",  e);
            return null;
        }
    }

    public BaseModel update(String  tableName, BaseModel model) {
        final TableMetaConfig dyRest = getConfigByTableName(tableName);
        try {
            final BaseModel o =(BaseModel)objectMapper.convertValue(model.getParamMap(), dyRest.getModelClass());
            getMapper(dyRest).updateById(o);
            return o;
        } catch (Exception e) {
            log.info("更新 发生异常",  e);
            return null;
        }
    }

    public void delete(String  tableName, String  pk) {
        final TableMetaConfig dyRest = getConfigByTableName(tableName);
        try {
            getMapper(dyRest).deleteById(pk);
        } catch (Exception e) {
            log.info("删除 发生异常",  e);
        }
    }

    public void exportExcel(String tableName, HttpServletResponse response) {
        TableMetaConfig dyRest = getConfigByTableName(tableName);
        final List<BaseModel> baseModels = selectList(tableName);
        FileWithExcelUtil.exportExcel(baseModels,"数据导出",tableName, dyRest.getModelClass(),tableName + ".xls",response);
    }

    public void importExcel(String tableName, MultipartFile file) {
        TableMetaConfig dyRest = getConfigByTableName(tableName);
        List<?> personList = FileWithExcelUtil.importExcel(file, 1, 1, dyRest.getModelClass());
        log.info("导入{}行", personList.size());
        for(Object obj : personList) {
            getMapper(dyRest).insert((BaseModel) obj);
        }
    }

    public BaseMapper<BaseModel> getMapperByTableName(String tableName) {
        return  getMapper(getConfigByTableName(tableName));
    }

    public TableMetaConfig getConfigByTableName(String tableName) {
        try {
          return classInject.findDyClassConfig(tableName);
        } catch (Exception e) {
            log.info("获取配置 发生异常",  e);
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public BaseMapper<BaseModel> getMapper(TableMetaConfig config) {
         return (BaseMapper<BaseModel> )beanFactory.getBean(config.getMapperClass());
    }



}
