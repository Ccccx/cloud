package com.example.mybatis.rest.config;

import com.baomidou.mybatisplus.core.MybatisMapperAnnotationBuilder;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-19 17:45
 */
@Slf4j
public class DyMybatisMapperRegistry extends MapperRegistry {

    private final Map<Class<?>, MybatisMapperProxyFactory<?>> knownMappers = new ConcurrentHashMap<>();
    private final DyMybatisConfiguration config;

    public DyMybatisMapperRegistry(DyMybatisConfiguration config) {
        super(config);
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        //  这里换成 MybatisMapperProxyFactory 而不是 MapperProxyFactory
        final MybatisMapperProxyFactory<T> mapperProxyFactory = (MybatisMapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + type + " is not known to the MybatisPlusMapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    @Override
    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                //  如果之前注入 直接返回
                return;
                //  这里就不抛异常了
                //  throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                //  这里也换成 MybatisMapperProxyFactory 而不是 MapperProxyFactory
                knownMappers.put(type, new MybatisMapperProxyFactory<>(type));
                //  这里也换成 MybatisMapperAnnotationBuilder 而不是 MapperAnnotationBuilder
                MybatisMapperAnnotationBuilder parser = new MybatisMapperAnnotationBuilder(config, type);
                parser.parse();
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }

    /**
     * 使用自己的 knownMappers
     */
    @Override
    public Collection<Class<?>> getMappers() {
        return Collections.unmodifiableCollection(knownMappers.keySet());
    }

    /**
     * 额外删除Mapper犯法
     * @param type   Mapper类型
     * @param <T>   类型
     */
    public <T> void removeMapper(Class<T> type) {
        knownMappers.remove(type);
        log.debug("removeMapper: {}", type);
    }
}
