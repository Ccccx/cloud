package com.cjz.webmvc.liquibase.service;

import com.cjz.webmvc.liquibase.persistence.model.FormChangelog;
import com.cjz.webmvc.liquibase.support.DbConf;
import com.cjz.webmvc.liquibase.support.TableInfo;
import com.cjz.webmvc.liquibase.utils.ChangeLogGeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-08 10:47
 */
@Slf4j
@Component
public class ChangelogManagerComponent {
    @Resource
    private IFormChangelogService changelogService;

    @Resource
    private DataSourceProperties dataSourceProperties;

    public String createOrUpdateTable(TableInfo tableInfo) {

        DbConf properties = new DbConf();
        properties.setUrl(dataSourceProperties.getUrl());
        properties.setUser(dataSourceProperties.getUsername());
        properties.setPassword(dataSourceProperties.getPassword());
        properties.setDriver(dataSourceProperties.getDriverClassName());
        tableInfo.setDbConf(properties);

        FormChangelog formChangelog = changelogService.getById(tableInfo.getTableName());
        final StringBuilder changeLogFile = new StringBuilder();
        if (formChangelog == null) {
            formChangelog = new FormChangelog();
            formChangelog.setTableName(tableInfo.getTableName());
            formChangelog.setStatus(1);
        }
        try {
            if (StringUtils.isEmpty(formChangelog.getChangelog())) {
                changeLogFile.append(ChangeLogGeneratorUtils.generateChangeLog(tableInfo, null));
            } else {
                changeLogFile.append(ChangeLogGeneratorUtils.generateChangeLog(tableInfo, formChangelog.getChangelog()));
            }
            ChangeLogGeneratorUtils.update(tableInfo, changeLogFile.toString());
            formChangelog.setChangelog(changeLogFile.toString());
        } catch (Exception e) {
            log.error("保存changelog文件失败!!", e);
            return e.getMessage();
        }
        changelogService.saveOrUpdate(formChangelog);
        return changeLogFile.toString();
    }

    public String tableChnagelog(String tableName) {
        FormChangelog formChangelog = changelogService.getById(tableName);
        if (formChangelog == null) {
            return null;
        }
        return formChangelog.getChangelog();
    }


}
