package com.example.mybatis.rest.support;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.TableConfig;
import com.example.mybatis.rest.model.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription.Builder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default;
import net.bytebuddy.implementation.FieldAccessor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.DocumentationCache;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.mybatis.rest.support.TableResolver.TYPE_MAP;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 11:11
 */
@Slf4j
public class ByteBuddyTableConfigManager extends AbstractTableConfigManager{

    public static final String VALUE = "value";
    public static final String NAME = "name";

    @Resource
    protected DefaultListableBeanFactory beanFactory;

    @Resource
    protected TableResolver tableResolver;

    @Override
    public TableConfig update(String tableName) {
        return null;
    }

    @Override
    public TableConfig load(String tableName) {
        final TableInfo tableInfo = tableResolver.getDataSourceConfig(tableName);
        TableConfig config = new TableConfig(tableInfo);
        // 定义model
        DynamicType.Builder<BaseModel> modelBuilder = new ByteBuddy()
                .subclass(BaseModel.class)
                .annotateType(Builder.ofType(TableName.class).define(VALUE, tableInfo.getName()).build())
                .name(tableInfo.getEntityName());

        // 给model添加字段
        for (com.baomidou.mybatisplus.generator.config.po.TableField column : tableInfo.getFields()) {
            FieldDefinition.Optional<BaseModel> modelOptional;
            final Class<?> columnType = TYPE_MAP.get(column.getColumnType());
            // 添加Mybatis plus 注解
            if (column.isKeyFlag()) {
                modelOptional = modelBuilder.defineField(column.getPropertyName(), columnType, Modifier.PRIVATE)
                        .annotateField(Builder.ofType(TableId.class).define(VALUE, column.getColumnName()).build());
            } else {
                modelOptional = modelBuilder.defineField(column.getPropertyName(), columnType, Modifier.PRIVATE)
                        .annotateField(Builder.ofType(TableField.class).define(VALUE, column.getColumnName()).build());
            }

            // 添加Easy Poi 注解
            if (DbColumnType.DATE.equals(column.getColumnType())) {
                modelBuilder = modelOptional.annotateField(Builder.ofType(Excel.class)
                        .define(NAME, defaultIfEmpty(column.getComment(), column.getPropertyName()))
                        .define("format", "yyyy-MM-dd HH:mm:ss")
                        .define("width", 40.0)
                        .build());
            } else {
                modelBuilder = modelOptional.annotateField(Builder.ofType(Excel.class).define(NAME,
                        defaultIfEmpty(column.getComment(), column.getPropertyName())).build());
            }

            final String methodName = NamingStrategy.capitalFirst(column.getPropertyName());
            // getter
            modelBuilder = modelBuilder.defineMethod("get" + methodName, columnType, Modifier.PUBLIC)
                    .intercept(FieldAccessor.ofBeanProperty());
           // setter
            modelBuilder = modelBuilder.defineMethod("set" + methodName, void.class, Modifier.PUBLIC).withParameters(columnType)
                    .intercept(FieldAccessor.ofBeanProperty());
        }

        final Unloaded<BaseModel> modelMake = modelBuilder.make();
        // 保存文件
        saveClassFile(modelMake);
        final Class<?> model = modelMake.load(getClass().getClassLoader(), Default.INJECTION).getLoaded();


        TypeDescription.Generic mapperType =
                TypeDescription.Generic.Builder.parameterizedType(BaseMapper.class, model).build();

        String mapperName = tableInfo.getMapperName();
        final Unloaded<?> mapperMake = new ByteBuddy()
                .makeInterface(mapperType)
                .annotateType(Builder.ofType(Mapper.class).build())
                .name(mapperName)
                .make();

        final Class<?> mapper = mapperMake.load(getClass().getClassLoader(), Default.INJECTION).getLoaded();

        config.setModelClass(model);
        config.setMapperClass(mapper);
        return config;
    }


    /**
     * 保存字节码文件
     *
     * @param <T>  字节码原始类型
     * @param make 标记
     */
    public <T> void saveClassFile(Unloaded<T> make) {
        try {
            make.saveIn(new File("E:\\IDEA\\cloud\\mybatis-plus-dy-rest\\target\\classes"));
        } catch (IOException e) {
            log.error("SaveClassFile Exception：", e);
        }
    }


}
