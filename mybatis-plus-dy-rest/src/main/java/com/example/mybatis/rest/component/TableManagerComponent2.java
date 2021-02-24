//package com.example.mybatis.rest.component;
//
//import com.baomidou.mybatisplus.core.MybatisConfiguration;
//import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.example.mybatis.rest.model.DyOrmConfig;
//import com.example.mybatis.rest.model.TableColumns;
//import com.example.mybatis.rest.model.TableMetadata;
//import com.example.mybatis.rest.support.DyOrmGenerator;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.lang.reflect.Field;
//import java.util.*;
//
///**
// * @author chengjz
// * @version 1.0
// * @since 2021-02-02 16:14
// */
//@Slf4j
//@Component
//public class TableManagerComponent2 {
//
//    private static final Map<String, TableMetadata> TABLES;
//
//    static {
//        Map<String, TableMetadata> table = new HashMap<>();
//        final TableMetadata dyRest = new TableMetadata();
//        dyRest.setTableName("DY_REST");
//        dyRest.setClassName("DyRest");
//        dyRest.setDesc("测试动态Rest");
//
//        List<TableColumns> columns = new ArrayList<>();
//        TableColumns c1 = new TableColumns();
//        c1.setFieldName("age");
//        c1.setColumnName("AGE");
//        c1.setClz(Integer.class);
//
//        TableColumns c2 = new TableColumns();
//        c2.setFieldName("name");
//        c2.setColumnName("NAME");
//        c2.setClz(String.class);
//
//        columns.add(c1);
//        columns.add(c2);
//        dyRest.setColumns(columns);
//
//        table.put("dyRest", dyRest);
//
//        TABLES = Collections.unmodifiableMap(table);
//    }
//
//    @Resource
//    protected DefaultListableBeanFactory beanFactory;
//    @Resource
//    private DyOrmGenerator generator;
//    @Resource
//    private DefaultSqlSessionFactory sqlSessionFactory;
//
//    public List<?> selectList() {
//        DyOrmConfig config = null;
//        try {
//            final TableMetadata dyRest = TABLES.get("dyRest");
//            config = generator.generator(dyRest, false);
//            final Object bean = beanFactory.getBean(config.getMapperClass());
//            return ((BaseMapper<?>) bean).selectList(Wrappers.lambdaQuery());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (config != null) {
//                beanFactory.removeBeanDefinition(config.getMapperClass().getName());
//                final MybatisConfiguration configuration = (MybatisConfiguration) sqlSessionFactory.getConfiguration();
//                final MybatisMapperRegistry mapperRegistry = (MybatisMapperRegistry) configuration.getMapperRegistry();
//                try {
//                    //  移除Mapper
//                    final Field knownMappers = mapperRegistry.getClass().getDeclaredField("knownMappers");
//                    knownMappers.setAccessible(true);
//                    final Map<Class<?>, MybatisMapperProxyFactory<?>> mappersMap = (Map<Class<?>, MybatisMapperProxyFactory<?>>) knownMappers.get(mapperRegistry);
//                    mappersMap.remove(config.getMapperClass());
//
//                    final Field mappedStatements = configuration.getClass().getSuperclass().getDeclaredField("mappedStatements");
//                    mappedStatements.setAccessible(true);
//                    final Map<String, MappedStatement> statementMap = (Map<String, MappedStatement>) mappedStatements.get(configuration);
//                    List<String> rmKey = new ArrayList<>();
//                    for (String key : statementMap.keySet()) {
//                        final MappedStatement mappedStatement = statementMap.get(key);
//                        if (mappedStatement.getId().startsWith(config.getMapperClass().getName())) {
//                            rmKey.add(key);
//                        }
//                    }
//
//                    for (String key : rmKey) {
//                        statementMap.remove(key);
//                        log.info("rm: {}", key);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return new ArrayList<>();
//    }
//
//
//}
