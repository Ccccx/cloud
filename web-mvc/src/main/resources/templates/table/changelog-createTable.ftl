<?xml version="1.1" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">
    <changeSet author="${createUser}" id="${version}">
        <preConditions onFail="MARK_RAN">
            <not>
                <tableExists tableName="${tableName}"/>
            </not>
        </preConditions>
        <#if columns?? && (columns?size > 0) >
            <createTable tableName="${tableName}">
                <#list columns as column>
                    <#if column.nonNull || column.pk>
                        <column name="${column.name}" <#if column.comment??>remarks="${column.comment}" </#if>
                                type="${column.type}" <#if column.defaultValue??> defaultValue="${column.defaultValue}"</#if>>
                            <constraints<#if column.nonNull> nullable="false" </#if><#if column.pk> primaryKey="true" </#if>/>
                        </column>
                    <#else>
                        <column name="${column.name}" <#if column.comment??>remarks="${column.comment}" </#if>
                                type="${column.type}" <#if column.defaultValue??> defaultValue="${column.defaultValue}"</#if>/>
                    </#if>
                </#list>
            </createTable>
        </#if>
        <rollback>
            <dropTable tableName="${tableName}"/>
        </rollback>
    </changeSet>
</databaseChangeLog>
