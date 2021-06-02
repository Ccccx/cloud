package com.example.mybatis.rest.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
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
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.BiFunction;

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
    private final DyMybatisMapperRegistry mybatisMapperRegistry = new DyMybatisMapperRegistry(this);

    private final Set<String> loadedResources = new ConcurrentSkipListSet<>();

    private final Set<String> mappedStatementSet = new ConcurrentSkipListSet<>();

    private final Map<String, MappedStatement> mappedStatements = new StrictMap<MappedStatement>("Mapped Statements collection")
            .conflictMessageProducer((savedValue, targetValue) ->
                    ". please check " + savedValue.getResource() + " and " + targetValue.getResource());

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
        if (mappedStatementSet.contains(ms.getId())) {
            /*
             * 说明已加载了xml中的节点； 忽略mapper中的SqlProvider数据
             */
            log.error("mapper[" + ms.getId() + "] is ignored, because it exists, maybe from xml file");
            return;
        }
        mappedStatements.put(ms.getId(), ms);
        mappedStatementSet.add(ms.getId());
    }


    @Override
    public Collection<String> getMappedStatementNames() {
        buildAllStatements();
        return mappedStatements.keySet();
    }

    @Override
    public Collection<MappedStatement> getMappedStatements() {
        buildAllStatements();
        return mappedStatements.values();
    }

    @Override
    public MappedStatement getMappedStatement(String id) {
        return this.getMappedStatement(id, true);
    }

    @Override
    public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }
        return mappedStatements.get(id);
    }

    @Override
    public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }
        return mappedStatementSet.contains(statementName);
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

    @Override
    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    @Override
    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
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
            mappedStatementSet.remove(key);
            log.debug("rm mappedStatements: {}", key);
        }

    }

    /**
     * 额外删除Mapper
     * @param type   Mapper类型
     * @param <T>   类型
     */
    public <T> void removeMapper(Class<T> type) {
        mybatisMapperRegistry.removeMapper(type);
        log.debug("removeMapper: {}", type);
    }

    public <T> void removeLoadResource(Class<T> type){
        loadedResources.remove(type.toString());
        log.debug("removeLoadResource: {}", type);
    }

    public <T> void removeMapperRegistryCache(Class<T> type){
        Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(this);
        mapperRegistryCache.remove(type.toString());
        log.debug("removeMapperRegistryCache: {}", type);
    }


    protected class StrictMap<V> extends ConcurrentHashMap<String, V> {

        private static final long serialVersionUID = -4950446264854982944L;
        private final String name;
        private BiFunction<V, V, String> conflictMessageProducer;

        public StrictMap(String name) {
            super();
            this.name = name;
        }

        /**
         * Assign a function for producing a conflict error message when contains value with the same key.
         * <p>
         * function arguments are 1st is saved value and 2nd is target value.
         *
         * @param conflictMessageProducer A function for producing a conflict error message
         * @return a conflict error message
         * @since 3.5.0
         */
        public DyMybatisConfiguration.StrictMap<V> conflictMessageProducer(BiFunction<V, V, String> conflictMessageProducer) {
            this.conflictMessageProducer = conflictMessageProducer;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V put(String key, V value) {
            if (mappedStatementSet.contains(key)) {
                throw new IllegalArgumentException(name + " already contains value for " + key
                        + (conflictMessageProducer == null ? "" : conflictMessageProducer.apply(super.get(key), value)));
            }
            if (isUseGeneratedShortKey()) {
                if (key.contains(".")) {
                    final String shortKey = getShortName(key);
                    if (super.get(shortKey) == null) {
                        super.put(shortKey, value);
                    } else {
                        super.put(shortKey, (V) new DyMybatisConfiguration.StrictMap.Ambiguity(shortKey));
                    }
                }
            }
            return super.put(key, value);
        }

        @Override
        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            if (isUseGeneratedShortKey() && value instanceof DyMybatisConfiguration.StrictMap.Ambiguity) {
                throw new IllegalArgumentException(((DyMybatisConfiguration.StrictMap.Ambiguity) value).getSubject() + " is ambiguous in " + name
                        + " (try using the full name including the namespace, or rename one of the entries)");
            }
            return value;
        }

        protected class Ambiguity {
            private final String subject;

            public Ambiguity(String subject) {
                this.subject = subject;
            }

            public String getSubject() {
                return subject;
            }
        }

        private String getShortName(String key) {
            final String[] keyParts = key.split("\\.");
            return keyParts[keyParts.length - 1];
        }
    }

    protected class Ambiguity {
        private final String subject;

        public Ambiguity(String subject) {
            this.subject = subject;
        }

        public String getSubject() {
            return subject;
        }
    }
}
