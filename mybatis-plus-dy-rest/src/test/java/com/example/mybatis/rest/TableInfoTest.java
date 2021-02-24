package com.example.mybatis.rest;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-22 15:32
 */
@Slf4j
class TableInfoTest {


    /**
     * 数据源配置
     */
    public static final DataSourceConfig DSC = new DataSourceConfig();

    static {
        DSC.setUrl("jdbc:mysql://cx:13306/cloud?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8");
        DSC.setDriverName("com.mysql.cj.jdbc.Driver");
        DSC.setUsername("root");
        DSC.setPassword("meiyoumima.0");
    }

    @Test
    void t1() throws Exception{
        final Connection conn = DSC.getConn();
        final DbType dbType = DSC.getDbType();
        final IDbQuery dbQuery = DSC.getDbQuery();
        /**
         * 全局配置信息
         */
        final GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDateType(DateType.ONLY_DATE);

        final StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);

        List<String> table = new ArrayList<>();
        table.add("T_USERS");
        String tablesSql = dbQuery.tablesSql();
        if (DbType.ORACLE == dbType) {
            //oracle 默认 schema=username
            String schema = DSC.getUsername().toUpperCase();
            DSC.setSchemaName(schema);
            tablesSql = String.format(tablesSql, schema);
        }
        StringBuilder sql = new StringBuilder(tablesSql);

        sql.append(" AND ").append(dbQuery.tableName()).append(" IN (")
                .append(table.stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(","))).append(")");
        //所有的表信息
        List<TableInfo> tableList = new ArrayList<>();

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql.toString()); ResultSet results = preparedStatement.executeQuery()) {
            TableInfo tableInfo;
            while (results.next()) {
                String tableName = results.getString(dbQuery.tableName());
                if (StringUtils.isNotBlank(tableName)) {
                    tableInfo = new TableInfo();
                    tableInfo.setName(tableName);

                    String tableComment = results.getString(dbQuery.tableComment());
//                        if ( "VIEW".equals(tableComment)) {
//                            // 跳过视图
//                            continue;
//                        }
                    tableInfo.setComment(tableComment);

                    tableList.add(tableInfo);
                } else {
                    System.err.println("当前数据库为空！！！");
                }
            }
        }

        tableList.forEach(tableInfo -> convertTableFields(conn, dbType, dbQuery, globalConfig, strategyConfig, tableInfo));

        processTable(tableList, strategyConfig);
        log.info("---- {}", tableList);
    }

    /**
     * 处理表对应的类名称
     *
     * @param tableList 表名称
     * @param strategyConfig    策略配置项
     * @return 补充完整信息后的表
     */
    private List<TableInfo> processTable(List<TableInfo> tableList, StrategyConfig strategyConfig) {
        for (TableInfo tableInfo : tableList) {
            String entityName = NamingStrategy.capitalFirst(NamingStrategy.underlineToCamel(tableInfo.getName())) +  System.currentTimeMillis();
            tableInfo.setEntityName(strategyConfig, entityName);
            tableInfo.setMapperName(entityName + ConstVal.MAPPER);
        }
        return tableList;
    }


    private void convertTableFields(Connection conn, DbType dbType, IDbQuery dbQuery, GlobalConfig globalConfig, StrategyConfig strategyConfig, TableInfo tableInfo) {
        boolean haveId = false;
        List<TableField> fieldList = new ArrayList<>();
        List<TableField> commonFieldList = new ArrayList<>();
        String tableName = tableInfo.getName();
        try {
            String tableFieldsSql = dbQuery.tableFieldsSql();
            Set<String> h2PkColumns = new HashSet<>();
            if (DbType.POSTGRE_SQL == dbType) {
                tableFieldsSql = String.format(tableFieldsSql, DSC.getSchemaName(), tableName);
            } else if (DbType.KINGBASE_ES == dbType) {
                tableFieldsSql = String.format(tableFieldsSql, DSC.getSchemaName(), tableName);
            } else if (DbType.DB2 == dbType) {
                tableFieldsSql = String.format(tableFieldsSql, DSC.getSchemaName(), tableName);
            } else if (DbType.ORACLE == dbType) {
                tableName = tableName.toUpperCase();
                tableFieldsSql = String.format(tableFieldsSql.replace("#schema", DSC.getSchemaName()), tableName);
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
                    TableField field = new TableField();
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
                            isId = StringUtils.isNotBlank(key) && "PRI".equals(key.toUpperCase());
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
                    IKeyWordsHandler keyWordsHandler = DSC.getKeyWordsHandler();
                    if (keyWordsHandler != null) {
                        if (keyWordsHandler.isKeyWords(columnName)) {
                            System.err.println(String.format("当前表[%s]存在字段[%s]为数据库关键字或保留字!", tableName, columnName));
                            field.setKeyWords(true);
                            newColumnName = keyWordsHandler.formatColumn(columnName);
                        }
                    }
                    field.setColumnName(newColumnName);
                    field.setType(results.getString(dbQuery.fieldType()));

                    field.setPropertyName(strategyConfig,   NamingStrategy.underlineToCamel(field.getName()));
                    field.setColumnType(DSC.getTypeConvert().processTypeConvert(globalConfig, field));

                        field.setComment(results.getString(dbQuery.fieldComment()));
                    fieldList.add(field);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception：" + e.getMessage());
        }
        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);
    }


}
