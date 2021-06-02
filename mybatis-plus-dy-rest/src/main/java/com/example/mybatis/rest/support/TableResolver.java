package com.example.mybatis.rest.support;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-03-23 14:22
 */
@Component
@Slf4j
public class TableResolver {

    protected static final Map<IColumnType, Class<?>> TYPE_MAP = new HashMap<>();

    private final DataSourceConfig dataSourceConfig;

    public TableResolver(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    static {
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


    public  TableInfo getDataSourceConfig( String tableName) {
        return getDataSourceConfig( Collections.singletonList(tableName)).getOrDefault(tableName, null);
    }

    public  Map<String, TableInfo> getDataSourceConfig( List<String> table) {
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

            String sql = tablesSql + " AND " + dbQuery.tableName() + " IN (" +
                    table.stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(",")) + ")";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql); ResultSet results = preparedStatement.executeQuery()) {
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
                processFields(dbType, dbQuery, globalConfig, strategyConfig, haveId, fieldList, tableName, h2PkColumns, results);
            }
        } catch (SQLException e) {
            log.error("QL Exception：", e);
        }
        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);
    }

    private void processFields(DbType dbType, IDbQuery dbQuery, GlobalConfig globalConfig, StrategyConfig strategyConfig, boolean haveId, List<com.baomidou.mybatisplus.generator.config.po.TableField> fieldList, String tableName, Set<String> h2PkColumns, ResultSet results) throws SQLException {
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


    /**
     * 处理表对应的类名称
     *
     * @param tableList      表名称
     * @param strategyConfig 策略配置项
     */
    private void processTable(List<TableInfo> tableList, StrategyConfig strategyConfig) {
        for (TableInfo tableInfo : tableList) {
            String entityName = NamingStrategy.capitalFirst(NamingStrategy.underlineToCamel(tableInfo.getName())) + System.currentTimeMillis();
            tableInfo.setEntityName(strategyConfig, entityName);
            tableInfo.setMapperName(entityName + ConstVal.MAPPER);
        }
    }
}
