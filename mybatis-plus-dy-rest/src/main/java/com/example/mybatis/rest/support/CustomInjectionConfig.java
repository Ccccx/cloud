package com.example.mybatis.rest.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.example.mybatis.rest.service.IOperationService;
import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-06 13:37
 */
public  class CustomInjectionConfig extends InjectionConfig {

    @Override
    public void initMap() {

    }

    @Override
    public Map<String, Object> prepareObjectMap(Map<String, Object> objectMap) {
        final String entity = (String) objectMap.get("entity");

        final TableInfo tableInfo = (TableInfo) objectMap.get("table");
        final Set<String> importPackages = new HashSet<>();
        importPackages.add(Constants.class.getName());
        importPackages.add(SFunction.class.getName());
        importPackages.add(LambdaQueryWrapper.class.getName());
        importPackages.add(IPage.class.getName());
        importPackages.add(Wrappers.class.getName());
        importPackages.add(SqlMethod.class.getName());
        importPackages.add(SqlHelper.class.getName());
        importPackages.add(BeanWrapper.class.getName());
        importPackages.add(BeanWrapperImpl.class.getName());
        importPackages.add(Page.class.getName());
        importPackages.add(MapUtils.class.getName());
        importPackages.add(MapperMethod.class.getName());
        importPackages.add(Log.class.getName());
        importPackages.add(LogFactory.class.getName());
        importPackages.add(Assert.class.getName());
        importPackages.add(StringMap.class.getName());
        importPackages.add(SqlSession.class.getName());
        importPackages.add("java.util.*");
        importPackages.add(CollectionUtils.class.getName());
        importPackages.add(StringUtils.class.getName());
        importPackages.add(Sets.class.getName());
        importPackages.add(IOperationService.class.getName());
        importPackages.add(Transactional.class.getName());

        importPackages.removeAll(tableInfo.getImportPackages());
        objectMap.put("modelPackages", importPackages);

        if (Objects.equals(entity, "SysMenu")) {
            List<QueryCriteria> list = new ArrayList<>();
            list.add(new QueryCriteria("eq", "menuId"));
            list.add(new QueryCriteria("ge", "orderNum"));
            list.add(new QueryCriteria("like", "name"));
            objectMap.put("criteriaList", list);

            objectMap.put("saveNonnull", Arrays.asList("type", "url", "name"));

            objectMap.put("updateNonnull", Arrays.asList("id"));
        }
        return objectMap;
    }
}
