package com.example.mybatis.rest.support;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.model.TableMetaConfig;
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

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-02 11:11
 */
@Slf4j
@Component
public class TableMetaManager {

    public static final String VALUE = "value";
    public static final String NAME = "name";
    public static Map<DbColumnType, Class<?>> TYPE_MAP = new EnumMap<>(DbColumnType.class);
    private final Map<String, TableMetaConfig> TABLE_CONFIG_CACHE = new ConcurrentHashMap<>();
    @Resource
    protected DefaultListableBeanFactory beanFactory;
    @Resource
    protected DataSourceConfig dataSourceConfig;

    {
        // 基本类型
        TYPE_MAP.put(DbColumnType.BASE_BYTE, byte.class);
        TYPE_MAP.put(DbColumnType.BASE_SHORT, short.class);
        TYPE_MAP.put(DbColumnType.BASE_CHAR, char.class);
        TYPE_MAP.put(DbColumnType.BASE_INT, int.class);
        TYPE_MAP.put(DbColumnType.BASE_LONG, long.class);
        TYPE_MAP.put(DbColumnType.BASE_FLOAT, float.class);
        TYPE_MAP.put(DbColumnType.BASE_DOUBLE, double.class);
        TYPE_MAP.put(DbColumnType.BASE_BOOLEAN, boolean.class);
        // 包装类型
        TYPE_MAP.put(DbColumnType.BYTE, Byte.class);
        TYPE_MAP.put(DbColumnType.SHORT, Short.class);
        TYPE_MAP.put(DbColumnType.CHARACTER, Character.class);
        TYPE_MAP.put(DbColumnType.INTEGER, Integer.class);
        TYPE_MAP.put(DbColumnType.LONG, Long.class);
        TYPE_MAP.put(DbColumnType.FLOAT, Float.class);
        TYPE_MAP.put(DbColumnType.DOUBLE, Double.class);
        TYPE_MAP.put(DbColumnType.BOOLEAN, Boolean.class);
        TYPE_MAP.put(DbColumnType.STRING, String.class);
        // sql 包下数据类型
        TYPE_MAP.put(DbColumnType.DATE_SQL, java.sql.Date.class);
        TYPE_MAP.put(DbColumnType.TIME, Time.class);
        TYPE_MAP.put(DbColumnType.TIMESTAMP, Timestamp.class);
        TYPE_MAP.put(DbColumnType.BLOB, Blob.class);
        TYPE_MAP.put(DbColumnType.CLOB, Clob.class);
        // java8 新时间类型
        TYPE_MAP.put(DbColumnType.LOCAL_DATE, LocalDate.class);
        TYPE_MAP.put(DbColumnType.LOCAL_TIME, LocalTime.class);
        TYPE_MAP.put(DbColumnType.YEAR, Year.class);
        TYPE_MAP.put(DbColumnType.YEAR_MONTH, YearMonth.class);
        TYPE_MAP.put(DbColumnType.LOCAL_DATE_TIME, LocalDateTime.class);
        TYPE_MAP.put(DbColumnType.INSTANT, Instant.class);
        // 其他杂类
        TYPE_MAP.put(DbColumnType.BYTE_ARRAY, byte[].class);
        TYPE_MAP.put(DbColumnType.OBJECT, Object.class);
        TYPE_MAP.put(DbColumnType.DATE, java.util.Date.class);
        TYPE_MAP.put(DbColumnType.BIG_INTEGER, BigInteger.class);
        TYPE_MAP.put(DbColumnType.BIG_DECIMAL, BigDecimal.class);
    }

    public TableMetaConfig findDyClassConfig(String tableName)  {
        if (TABLE_CONFIG_CACHE.containsKey(tableName)) {
            return TABLE_CONFIG_CACHE.get(tableName);
        }
        return loadTableMeta(tableName);
    }

    public void remove(TableMetadata tableMetadata)  {
        TABLE_CONFIG_CACHE.remove(tableMetadata.getClassName());
    }

    public TableMetaConfig loadTableMeta(String tableName)  {
        return loadTableMeta(tableName, true);
    }

    public TableMetaConfig loadTableMeta(String tableName, boolean enableCache) {
        final TableInfo tableInfo = getDataSourceConfig(tableName);
        TableMetaConfig config = new TableMetaConfig(tableInfo);
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
                modelOptional = modelBuilder.defineField(column.getPropertyName(), columnType, Modifier.PUBLIC)
                        .annotateField(Builder.ofType(TableId.class).define(VALUE, column.getColumnName()).build());
            } else {
                modelOptional = modelBuilder.defineField(column.getPropertyName(), columnType, Modifier.PUBLIC)
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
//            // setter
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

        // 添加到Spring 容器
        final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(mapper);
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, mapper.getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, beanFactory);

        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(mapper);
        beanDefinition.setBeanClass(MapperFactoryBean.class);
        beanDefinition.getPropertyValues().add("addToConfig", true);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinition.setLazyInit(false);

        config.setModelClass(model);
        config.setMapperClass(mapper);
        if (enableCache) {
            TABLE_CONFIG_CACHE.put(tableName, config);
        }
        return config;
    }

    protected TableInfo getDataSourceConfig(String tableName) {
        return getDataSourceConfig(Arrays.asList(tableName)).getOrDefault(tableName, null);
    }

    protected Map<String, TableInfo> getDataSourceConfig(List<String> table) {
        //所有的表信息
        Map<String, TableInfo> tableInfoMap = new HashMap<>(8);
        try (final Connection conn = dataSourceConfig.getConn()) {
            final DbType dbType = dataSourceConfig.getDbType();
            final IDbQuery dbQuery = dataSourceConfig.getDbQuery();
            final GlobalConfig globalConfig = new GlobalConfig();
            globalConfig.setDateType(DateType.ONLY_DATE);
            final StrategyConfig strategyConfig = new StrategyConfig();
            strategyConfig.setNaming(NamingStrategy.underline_to_camel);
            strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);

            String tablesSql = dbQuery.tablesSql();
            if (DbType.ORACLE == dbType) {
                //oracle 默认 schema=username
                String schema = dataSourceConfig.getUsername().toUpperCase();
                dataSourceConfig.setSchemaName(schema);
                tablesSql = String.format(tablesSql, schema);
            }
            StringBuilder sql = new StringBuilder(tablesSql);

            sql.append(" AND ").append(dbQuery.tableName()).append(" IN (")
                    .append(table.stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(","))).append(")");


            try (PreparedStatement preparedStatement = conn.prepareStatement(sql.toString()); ResultSet results = preparedStatement.executeQuery()) {
                TableInfo tableInfo;
                while (results.next()) {
                    String dbTableName = results.getString(dbQuery.tableName());
                    if (StringUtils.isNotBlank(dbTableName)) {
                        tableInfo = new TableInfo();
                        tableInfo.setName(dbTableName);
                        String tableComment = results.getString(dbQuery.tableComment());
                        tableInfo.setComment(tableComment);
                        tableInfoMap.put(dbTableName, tableInfo);
                    } else {
                        log.warn("当前数据库为空！！！");
                    }
                }
            } catch (Exception e) {
                log.info("获取表信息失败: ", e);
            }
            tableInfoMap.values().forEach(tableInfo -> processTableField(conn, dbType, dbQuery, globalConfig, strategyConfig, tableInfo));
            processTable(new ArrayList<>(tableInfoMap.values()), strategyConfig);
        } catch (Exception e) {
            log.error("建立连接发生异常", e);
        }
        return tableInfoMap;
    }

    private void processTableField(Connection conn, DbType dbType, IDbQuery dbQuery, GlobalConfig globalConfig, StrategyConfig strategyConfig, TableInfo tableInfo) {
        boolean haveId = false;
        List<com.baomidou.mybatisplus.generator.config.po.TableField> fieldList = new ArrayList<>();
        List<com.baomidou.mybatisplus.generator.config.po.TableField> commonFieldList = new ArrayList<>();
        String tableName = tableInfo.getName();
        try {
            String tableFieldsSql = dbQuery.tableFieldsSql();
            Set<String> h2PkColumns = new HashSet<>();
            if (DbType.POSTGRE_SQL == dbType) {
                tableFieldsSql = String.format(tableFieldsSql, dataSourceConfig.getSchemaName(), tableName);
            } else if (DbType.KINGBASE_ES == dbType) {
                tableFieldsSql = String.format(tableFieldsSql, dataSourceConfig.getSchemaName(), tableName);
            } else if (DbType.DB2 == dbType) {
                tableFieldsSql = String.format(tableFieldsSql, dataSourceConfig.getSchemaName(), tableName);
            } else if (DbType.ORACLE == dbType) {
                tableName = tableName.toUpperCase();
                tableFieldsSql = String.format(tableFieldsSql.replace("#schema", dataSourceConfig.getSchemaName()), tableName);
            } else if (DbType.DM == dbType) {
                tableName = tableName.toUpperCase();
                tableFieldsSql = String.format(tableFieldsSql, tableName);
            } else if (DbType.H2 == dbType) {
                try (PreparedStatement pkQueryStmt = conn.prepareStatement(String.format(H2Query.PK_QUERY_SQL, tableName));
                     ResultSet pkResults = pkQueryStmt.executeQuery()) {
                    while (pkResults.next()) {
                        String primaryKey = pkResults.getString(dbQuery.fieldKey());
                        if (Boolean.parseBoolean(primaryKey)) {
                            h2PkColumns.add(pkResults.getString(dbQuery.fieldName()));
                        }
                    }
                }
                tableFieldsSql = String.format(tableFieldsSql, tableName);
            } else {
                tableFieldsSql = String.format(tableFieldsSql, tableName);
            }
            try (
                    PreparedStatement preparedStatement = conn.prepareStatement(tableFieldsSql);
                    ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    com.baomidou.mybatisplus.generator.config.po.TableField field = new com.baomidou.mybatisplus.generator.config.po.TableField();
                    String columnName = results.getString(dbQuery.fieldName());
                    // 避免多重主键设置，目前只取第一个找到ID，并放到list中的索引为0的位置
                    boolean isId;
                    if (DbType.H2 == dbType) {
                        isId = h2PkColumns.contains(columnName);
                    } else {
                        String key = results.getString(dbQuery.fieldKey());
                        if (DbType.DB2 == dbType || DbType.SQLITE == dbType) {
                            isId = StringUtils.isNotBlank(key) && "1".equals(key);
                        } else {
                            isId = StringUtils.isNotBlank(key) && "PRI".equalsIgnoreCase(key);
                        }
                    }

                    // 处理ID
                    if (isId && !haveId) {
                        field.setKeyFlag(true);
                        if (DbType.H2 == dbType || DbType.SQLITE == dbType || dbQuery.isKeyIdentity(results)) {
                            field.setKeyIdentityFlag(true);
                        }
                        haveId = true;
                    } else {
                        field.setKeyFlag(false);
                    }
                    // 自定义字段查询
                    String[] fcs = dbQuery.fieldCustom();
                    if (null != fcs) {
                        Map<String, Object> customMap = new HashMap<>(fcs.length);
                        for (String fc : fcs) {
                            customMap.put(fc, results.getObject(fc));
                        }
                        field.setCustomMap(customMap);
                    }
                    // 处理其它信息
                    field.setName(columnName);
                    String newColumnName = columnName;
                    IKeyWordsHandler keyWordsHandler = dataSourceConfig.getKeyWordsHandler();
                    if (keyWordsHandler != null) {
                        if (keyWordsHandler.isKeyWords(columnName)) {
                            log.error("当前表[{}]存在字段[{}]为数据库关键字或保留字!", tableName, columnName);
                            field.setKeyWords(true);
                            newColumnName = keyWordsHandler.formatColumn(columnName);
                        }
                    }
                    field.setColumnName(newColumnName);
                    field.setType(results.getString(dbQuery.fieldType()));

                    field.setPropertyName(strategyConfig, NamingStrategy.underlineToCamel(field.getName()));
                    field.setColumnType(dataSourceConfig.getTypeConvert().processTypeConvert(globalConfig, field));

                    field.setComment(results.getString(dbQuery.fieldComment()));
                    fieldList.add(field);
                }
            }
        } catch (SQLException e) {
            log.error("QL Exception：", e);
        }
        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);
    }


    /**
     * 处理表对应的类名称
     *
     * @param tableList      表名称
     * @param strategyConfig 策略配置项
     * @return 补充完整信息后的表
     */
    private List<TableInfo> processTable(List<TableInfo> tableList, StrategyConfig strategyConfig) {
        for (TableInfo tableInfo : tableList) {
            String entityName = NamingStrategy.capitalFirst(NamingStrategy.underlineToCamel(tableInfo.getName())) + System.currentTimeMillis();
            tableInfo.setEntityName(strategyConfig, entityName);
            tableInfo.setMapperName(entityName + ConstVal.MAPPER);
        }
        return tableList;
    }

    /**
     * 保存字节码文件
     *
     * @param make 标记
     * @param <T>  字节码原始类型
     * @return 结果
     */
    public <T> Unloaded<T> saveClassFile(Unloaded<T> make) {
        try {
            final Map<TypeDescription, File> typeDescriptionFileMap = make.saveIn(new File("E:\\IDEA\\cloud\\mybatis-plus-dy-rest\\target\\classes"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return make;
    }

}
