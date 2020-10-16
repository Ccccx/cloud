<?xml version="1.1" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">
    <changeSet author="${createUser}" id="${version}">
        <#if columns?? && (columns?size > 0) >
            <addColumn tableName="${tableName}">
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
            </addColumn>
        </#if>
        <#if dropColumns??  && (dropColumns?size > 0) >
            <dropColumn tableName="${tableName}">
                <#list dropColumns as column>
                    <column name="${column}"/>
                </#list>
            </dropColumn>
        </#if>
    </changeSet>
</databaseChangeLog>

