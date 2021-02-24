package com.example.mybatis.rest.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.executor.MybatisBatchExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisCachingExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisReuseExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisSimpleExecutor;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-19 17:41
 */
@Slf4j
public class DyMybatisConfiguration extends MybatisConfiguration {

    /**
     * Mapper 注册
     */
    protected final DyMybatisMapperRegistry mybatisMapperRegistry = new DyMybatisMapperRegistry(this);

    public DyMybatisConfiguration(Environment environment) {
        this();
        this.environment = environment;
    }

    @Setter
    @Getter
    private GlobalConfig globalConfig = GlobalConfigUtils.defaults();

    /**
     * 初始化调用
     */
    public DyMybatisConfiguration() {
        super();
        this.mapUnderscoreToCamelCase = true;
        languageRegistry.setDefaultDriverClass(MybatisXMLLanguageDriver.class);
    }

    /**
     * MybatisPlus 加载 SQL 顺序：
     * <p>1、加载XML中的SQL</p>
     * <p>2、加载sqlProvider中的SQL</p>
     * <p>3、xmlSql 与 sqlProvider不能包含相同的SQL</p>
     * <p>调整后的SQL优先级：xmlSql > sqlProvider > curdSql</p>
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        log.debug("addMappedStatement: " + ms.getId());
        if (mappedStatements.containsKey(ms.getId())) {
            /*
             * 说明已加载了xml中的节点； 忽略mapper中的SqlProvider数据
             */
            log.error("mapper[" + ms.getId() + "] is ignored, because it exists, maybe from xml file");
            return;
        }
        super.addMappedStatement(ms);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public MapperRegistry getMapperRegistry() {
        return mybatisMapperRegistry;
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public <T> void addMapper(Class<T> type) {
        mybatisMapperRegistry.addMapper(type);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mybatisMapperRegistry.addMappers(packageName, superType);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public void addMappers(String packageName) {
        mybatisMapperRegistry.addMappers(packageName);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mybatisMapperRegistry.getMapper(type, sqlSession);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public boolean hasMapper(Class<?> type) {
        return mybatisMapperRegistry.hasMapper(type);
    }

    /**
     * 指定动态SQL生成的默认语言
     *
     * @param driver LanguageDriver
     */
    @Override
    public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> driver) {
        if (driver == null) {
            //todo 替换动态SQL生成的默认语言为自己的。
            driver = MybatisXMLLanguageDriver.class;
        }
        getLanguageRegistry().setDefaultDriverClass(driver);
    }

    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        executorType = executorType == null ? defaultExecutorType : executorType;
        executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
        Executor executor;
        if (ExecutorType.BATCH == executorType) {
            executor = new MybatisBatchExecutor(this, transaction);
        } else if (ExecutorType.REUSE == executorType) {
            executor = new MybatisReuseExecutor(this, transaction);
        } else {
            executor = new MybatisSimpleExecutor(this, transaction);
        }
        if (cacheEnabled) {
            executor = new MybatisCachingExecutor(executor);
        }
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }

    public <T> void removeMappedStatement(Class<T> type) {
        List<String> rmKey = new ArrayList<>();
        for (String key : mappedStatements.keySet()) {
            if (key.startsWith(type.getName())) {
                rmKey.add(key);
            }
        }
        for (String key : rmKey) {
            mappedStatements.remove(key);
            log.info("rm: {}", key);
        }
    }

    /**
     * 额外删除Mapper犯法
     * @param type   Mapper类型
     * @param <T>   类型
     */
    public <T> void removeMapper(Class<T> type) {
        mybatisMapperRegistry.removeMapper(type);
        log.debug("removeMapper: {}", type);
    }
}
