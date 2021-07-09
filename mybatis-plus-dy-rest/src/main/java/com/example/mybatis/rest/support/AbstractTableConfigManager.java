package com.example.mybatis.rest.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatis.rest.config.DyMybatisConfiguration;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.TableConfig;
import com.example.mybatis.rest.service.IOperationService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-07 18:49
 */
@Slf4j
public abstract class AbstractTableConfigManager implements ITableConfigManager{

    protected static final Map<String, TableConfig> TABLE_CONFIG_CACHE = new ConcurrentHashMap<>();

    @Resource
    protected DefaultListableBeanFactory beanFactory;

    @Resource
    private DyMybatisConfiguration dyMybatisConfiguration;

    @Override
    public void clearByTableName(String  tableName) {
        if (TABLE_CONFIG_CACHE.containsKey(tableName)) {
            TableConfig config =  loadTableConfig(tableName);

            try {
                beanFactory.removeBeanDefinition(config.getMapperClass().getName());
                beanFactory.removeBeanDefinition(config.getServiceImplClass().getName());
            } catch (Exception e) {
                log.info("清空Spring容器Bean发生异常",  e);
            }
            try {
                final Class<?> mapperClass = config.getMapperClass();
                dyMybatisConfiguration.removeMappedStatement(mapperClass);
                dyMybatisConfiguration.removeMapper(mapperClass);
                dyMybatisConfiguration.removeLoadResource(mapperClass);
                dyMybatisConfiguration.removeMapperRegistryCache(mapperClass);
                TABLE_CONFIG_CACHE.remove(tableName);
            } catch (Exception e) {
                log.info("清空Mybatis映射发生异常",  e);
            }
        }
    }

    @Override
    public TableConfig loadTableConfig(String tableName) {
        if (TABLE_CONFIG_CACHE.containsKey(tableName)) {
            return TABLE_CONFIG_CACHE.get(tableName);
        }
        final TableConfig tableConfig = load(tableName);
        registerBeanDefinition(tableConfig);
        TABLE_CONFIG_CACHE.put(tableName, tableConfig);
        return tableConfig;
    }

    @Override
    public TableConfig updateTableConfig(String tableName) {
        return null;
    }

    @Override
    public TableConfig registerBeanDefinition(TableConfig tableConfig) {
        // 添加到Spring 容器
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE);

        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(tableConfig.getMapperClass());
        beanDefinition.setBeanClass(MapperFactoryBean.class);
        beanDefinition.getPropertyValues().add("addToConfig", true);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinition.setLazyInit(true);

        final String beanName = tableConfig.getMapperClass().getName();
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, beanFactory);
        final Object mapper = beanFactory.getBean(tableConfig.getMapperClass());
        Assert.isTrue(Objects.nonNull(mapper), tableConfig.getMapperClass() + "注入失败！" );
        tableConfig.setMapperInstance((BaseMapper<?>) mapper);

        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(tableConfig.getServiceImplClass());
        final AbstractBeanDefinition definition = builder.getBeanDefinition();
        definition.setScope(ConfigurableBeanFactory.SCOPE_PROTOTYPE);
        BeanDefinitionHolder serviceImplHold = new BeanDefinitionHolder(definition, tableConfig.getServiceImplClass().getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(serviceImplHold, beanFactory);

        final Object serviceImpl = beanFactory.getBean(tableConfig.getServiceImplClass());
        Assert.isTrue(Objects.nonNull(serviceImpl), tableConfig.getServiceImplClass() + "注入失败！" );
        tableConfig.setServiceImplInstance((IOperationService<BaseModel>) serviceImpl);
        return tableConfig;
    }

    /**
     * 加载配置
     * @param tableName 表名
     * @return   配置信息
     */
    public abstract TableConfig load(String tableName);

    /**
     * 更新配置
     * @param tableName 表名
     * @return   配置信息
     */
    public abstract TableConfig update(String tableName);
}
