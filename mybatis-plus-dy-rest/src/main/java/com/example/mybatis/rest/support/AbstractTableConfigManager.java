package com.example.mybatis.rest.support;

import com.example.mybatis.rest.config.DyMybatisConfiguration;
import com.example.mybatis.rest.model.TableConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import javax.annotation.Resource;
import java.util.Map;
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
            try {
                TableConfig config =  loadTableConfig(tableName);
                final Class<?> mapperClass = config.getMapperClass();
                beanFactory.removeBeanDefinition(mapperClass.getName());
                dyMybatisConfiguration.removeMappedStatement(mapperClass);
                dyMybatisConfiguration.removeMapper(mapperClass);
                dyMybatisConfiguration.removeLoadResource(mapperClass);
                dyMybatisConfiguration.removeMapperRegistryCache(mapperClass);
                TABLE_CONFIG_CACHE.remove(tableName);
            } catch (Exception e) {
                log.info("清空 发生异常",  e);
            }
        }
    }

    @Override
    public TableConfig loadTableConfig(String tableName) {
        if (TABLE_CONFIG_CACHE.containsKey(tableName)) {
            return TABLE_CONFIG_CACHE.get(tableName);
        }
        final TableConfig tableConfig = load(tableName);
        TABLE_CONFIG_CACHE.put(tableName, tableConfig);

        // 添加到Spring 容器
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);

        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(tableConfig.getMapperClass());
        beanDefinition.setBeanClass(MapperFactoryBean.class);
        beanDefinition.getPropertyValues().add("addToConfig", true);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinition.setLazyInit(false);

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, tableConfig.getMapperClass().getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, beanFactory);

        return tableConfig;
    }

    @Override
    public TableConfig updateTableConfig(String tableName) {
        return null;
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
