package com.example.mybatis.rest;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.TableColumns;
import com.example.mybatis.rest.model.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationDescription.Builder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 13:57
 */
@Slf4j
 class DyTest {

   static Map<DbColumnType, Class<?>> typeMap = new HashMap<>();
    {
        // 基本类型
        typeMap.put(DbColumnType.BASE_BYTE, byte.class);
        typeMap.put(DbColumnType.BASE_SHORT, short.class);
        typeMap.put(DbColumnType.BASE_CHAR, char.class);
        typeMap.put(DbColumnType.BASE_INT, int.class);
        typeMap.put(DbColumnType.BASE_LONG, long.class);
        typeMap.put(DbColumnType.BASE_FLOAT, float.class);
        typeMap.put(DbColumnType.BASE_DOUBLE, double.class);
        typeMap.put(DbColumnType.BASE_BOOLEAN, boolean.class);
// 包装类型
        typeMap.put(DbColumnType.BYTE, Byte.class);
        typeMap.put(DbColumnType.SHORT, Short.class);
        typeMap.put(DbColumnType.CHARACTER, Character.class);
        typeMap.put(DbColumnType.INTEGER, Integer.class);
        typeMap.put(DbColumnType.LONG, Long.class);
        typeMap.put(DbColumnType.FLOAT, Float.class);
        typeMap.put(DbColumnType.DOUBLE, Double.class);
        typeMap.put(DbColumnType.BOOLEAN, Boolean.class);
        typeMap.put(DbColumnType.STRING, String.class);
// sql 包下数据类型
        typeMap.put(DbColumnType.DATE_SQL, Date.class);
        typeMap.put(DbColumnType.TIME, Time.class);
        typeMap.put(DbColumnType.TIMESTAMP, Timestamp.class);
        typeMap.put(DbColumnType.BLOB, Blob.class);
        typeMap.put(DbColumnType.CLOB, Clob.class);
// java8 新时间类型
        typeMap.put(DbColumnType.LOCAL_DATE, LocalDate.class);
        typeMap.put(DbColumnType.LOCAL_TIME, LocalTime.class);
        typeMap.put(DbColumnType.YEAR, Year.class);
        typeMap.put(DbColumnType.YEAR_MONTH, YearMonth.class);
        typeMap.put(DbColumnType.LOCAL_DATE_TIME, LocalDateTime.class);
        typeMap.put(DbColumnType.INSTANT, Instant.class);
// 其他杂类
        typeMap.put(DbColumnType.BYTE_ARRAY, byte[].class);
        typeMap.put(DbColumnType.OBJECT, Object.class);
        typeMap.put(DbColumnType.DATE, java.util.Date.class);
        typeMap.put(DbColumnType.BIG_INTEGER, BigInteger.class);
        typeMap.put(DbColumnType.BIG_DECIMAL, BigDecimal.class);
    }

     @Test
    void t1() throws IOException{
         ClassPathResource resource = new ClassPathResource("");
         log.info("Class Url: {}", resource.getPath());
         new ByteBuddy()
                 .subclass(BaseModel.class)
                 .annotateType(AnnotationDescription.Builder.ofType(TableName.class).define("value", "DY_TEST").build())
                 .name("org.cjz.dy.DyRest")
                 .defineField("name", String.class, Modifier.PUBLIC)
                 .annotateField(AnnotationDescription.Builder.ofType(TableField.class).define("value", "NAME").build())
                 .make()
                .saveIn(resource.getFile()) ;

     }

     @Test
     void t2 () throws IOException {
         final ClassPathResource resource = new ClassPathResource("DyRest");
         if (resource.exists()) {
             log.info("resource exist ?  {}", resource.exists());
         }
         final ClassPathResource classPathResource = new ClassPathResource("");
         final File file = classPathResource.getFile();
         log.info("file : {}", file);
     }

    @Test
    void t3() throws Exception{
        final TableMetadata tableMetadata = getTableMetadata();
        ClassPathResource resource = new ClassPathResource("");
          DynamicType.Builder<BaseModel> modelBuilder = new ByteBuddy()
                .subclass(BaseModel.class)
                .annotateType(Builder.ofType(TableName.class).define("value", tableMetadata.getTableName()).build())
                .name(tableMetadata.getClassName());

        for (TableColumns column : tableMetadata.getColumns()) {
            modelBuilder = modelBuilder.defineField(column.getFieldName(), column.getClz(), Modifier.PUBLIC)
                    .annotateField(Builder.ofType(TableField.class).define("value", column.getColumnName()).build());
        }
        modelBuilder.make().saveIn(resource.getFile());
//        final Class<? extends BaseModel> modelType = value
//                .make()
//                .load(getClass().getClassLoader(), Default.WRAPPER)
//                .getLoaded();

//        TypeDescription.Generic mapperType =
//                Generic.Builder.parameterizedType(BaseMapper.class, String.class).build();
//
//        final Map<TypeDescription, File> typeDescriptionFileMap = new ByteBuddy()
//                .makeInterface(mapperType)
//                .annotateType(Builder.ofType(Mapper.class).build())
//                .name(tableMetadata.getClassName() + "Mapper")
//                .make()
//                .saveIn(resource.getFile());
        log.info("------------ ");
    }

    @Test
    void t4() throws Exception{
        // TypePool typePool = TypePool.Default.ofClassPath();
        final TableMetadata tableMetadata = getTableMetadata();
        for (int i = 0; i < 2; i++) {
            // 定义model
            DynamicType.Builder<?>  modelBuilder = new ByteBuddy()
                    .subclass(BaseModel.class)
                    .annotateType(Builder.ofType(TableName.class).define("value", tableMetadata.getTableName()).build())
                    .name(tableMetadata.getClassName());

            // 给model添加字段
            for (TableColumns column : tableMetadata.getColumns()) {
                modelBuilder = modelBuilder.defineField(column.getFieldName(), column.getClz(), Modifier.PUBLIC).annotateField(Builder.ofType(TableField.class).define("value", column.getColumnName()).build());
            }

            final Unloaded<?> modelMake =  modelBuilder.make();
            final Class<?> model =  modelMake.load( ClassLoader.getSystemClassLoader()).getLoaded();

            Generic mapperType =
                    Generic.Builder.parameterizedType(BaseMapper.class, model).build();

            String mapperName = tableMetadata.getClassName() + "Mapper";
            Unloaded<?>  mapperMake = new ByteBuddy()
                    .makeInterface(mapperType)
                    .annotateType(Builder.ofType(Mapper.class).build())
                    .name(mapperName)
                    .make();

            Class<?> mapper =  mapperMake.load(ClassLoader.getSystemClassLoader()).getLoaded();
            final Object o = model.newInstance();
            final Type genericSuperclass = o.getClass().getGenericSuperclass();
            final Type[] genericInterfaces = mapper.getGenericInterfaces();
        }

        log.info("---------");
    }

    @Test
    void  t5() throws Exception{
        final Type[] genericInterfaces = Sub.class.getGenericInterfaces();
        // 基本类型
        pt(DbColumnType.BASE_BYTE);
        pt(DbColumnType.BASE_SHORT);
        pt(DbColumnType.BASE_CHAR);
        pt(DbColumnType.BASE_INT);
        pt(DbColumnType.BASE_LONG);
        pt(DbColumnType.BASE_FLOAT);
        pt(DbColumnType.BASE_DOUBLE);
        pt(DbColumnType.BASE_BOOLEAN);
// 包装类型
        pt(DbColumnType.BYTE);
        pt(DbColumnType.SHORT);
        pt(DbColumnType.CHARACTER);
        pt(DbColumnType.INTEGER);
        pt(DbColumnType.LONG);
        pt(DbColumnType.FLOAT);
        pt(DbColumnType.DOUBLE);
        pt(DbColumnType.BOOLEAN);
        pt(DbColumnType.STRING);
// sql 包下数据类型
        pt(DbColumnType.DATE_SQL);
        pt(DbColumnType.TIME);
        pt(DbColumnType.TIMESTAMP);
        pt(DbColumnType.BLOB);
        pt(DbColumnType.CLOB);
// java8 新时间类型
        pt(DbColumnType.LOCAL_DATE);
        pt(DbColumnType.LOCAL_TIME);
        pt(DbColumnType.YEAR);
        pt(DbColumnType.YEAR_MONTH);
        pt(DbColumnType.LOCAL_DATE_TIME);
        pt(DbColumnType.INSTANT);
// 其他杂类
        pt(DbColumnType.BYTE_ARRAY);
        pt(DbColumnType.OBJECT);
        pt(DbColumnType.DATE);
        pt(DbColumnType.BIG_INTEGER);
        pt(DbColumnType.BIG_DECIMAL);
        log.info("---------");
    }

    @Test
    void t6() throws Exception{
    }


    private void pt(DbColumnType type) {
        if (typeMap.containsKey(type)) {
            log.info("解析类完成: {}", typeMap.get(type));
            return;
        }
        try {
            log.info("解析类完成: {}", StringUtils.isNoneEmpty(type.getPkg()) ? Class.forName(type.getPkg()) : Class.forName("java.lang." + type.getType()));
        } catch (ClassNotFoundException e) {
            log.warn("解析类失败: {}", type.getType());
        }
    }

    interface Parent {}

    interface Sub extends Parent {

    }



    private TableMetadata getTableMetadata() {
        final TableMetadata dyRest = new TableMetadata();
        dyRest.setTableName("DY_REST");
        dyRest.setClassName("cjz.DyRest");
        dyRest.setDesc("测试动态Rest");

        List<TableColumns> columns = new ArrayList<>();
        TableColumns c1 = new TableColumns();
        c1.setFieldName("age");
        c1.setColumnName("AGE");
        c1.setClz(Integer.class);

        TableColumns c2 = new TableColumns();
        c2.setFieldName("name");
        c2.setColumnName("NAME");
        c2.setClz(String.class);

        columns.add(c1); columns.add(c2);
        dyRest.setColumns(columns);

        return dyRest;
    }

    /**
     * 保存字节码文件
     * @param make 标记
     * @param <T>    字节码原始类型
     * @return  结果
     */
    public<T> Unloaded<T>  saveClassFile(Unloaded<T> make) {
        try {
            final Map<TypeDescription, File> typeDescriptionFileMap = make.saveIn(new File("E:\\IDEA\\cloud\\mybatis-plus-dy-rest\\target\\test-classes"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return make;
    }

    public boolean isLoadClass(String className) {
        try {
            ClassUtils.resolveClassName(className, getClass().getClassLoader());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public class UserEntity {

        @Excel(name = "ID")
        private int id;

        @Excel(name = "姓名")
        private String name;

        @Excel(name = "电子邮件", width = 20)
        private String email;

        @Excel(name = "年龄")
        private int age;

        @Excel(name = "性别", replace = {"男_1", "女_2"})
        private int sex;

        @Excel(name = "操作时间", format = "yyyy-MM-dd HH:mm:ss", width = 20)
        private Date time;
    }

}
