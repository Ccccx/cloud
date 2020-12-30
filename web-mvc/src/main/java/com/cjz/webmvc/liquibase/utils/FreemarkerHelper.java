package com.cjz.webmvc.liquibase.utils;

import com.cjz.webmvc.liquibase.constant.ColumnType;
import com.cjz.webmvc.liquibase.support.ColumnInfo;
import com.cjz.webmvc.liquibase.support.DbConf;
import com.cjz.webmvc.liquibase.support.TableInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-03-06 16:01
 */
@Slf4j
public class FreemarkerHelper {

    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);

    static {
        configuration.setNumberFormat("0.#####################");
        configuration.setClassForTemplateLoading(FreemarkerHelper.class, "/");
    }


    public static String buildTemplate(String templatePath, String charSet, Object paramMap) {
        try {
            StringWriter stringWriter = new StringWriter();
            Template template = configuration.getTemplate(templatePath, charSet);
            template.process(paramMap, stringWriter);
            return stringWriter.toString();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return exception.toString();
        }
    }


    public static String buildTemplate(String templatePath, Map<String, Object> paramMap) {
        return buildTemplate(templatePath, "utf-8", paramMap);
    }

    public static String buildTemplate(String templatePath, TableInfo tableInfo) {
        return buildTemplate(templatePath, "utf-8", tableInfo);
    }

    public static void main(String[] args) throws Exception {
//		String str = buildTemplate("template/table/tableTemplate.ftl", null);
//		System.out.println(str);
        final TableInfo create = new TableInfo();
        create.setTableName("test_cjz");
        create.setVersion("m1-onlform-2.2.2.0-202009081139");
        create.setCreateUser("ChengJinzhou");
        DbConf properties = new DbConf();
        properties.setUrl("jdbc:mysql://cx:13306/m1_onl_form?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8");
        properties.setUser("root");
        properties.setPassword("meiyoumima.0");
        properties.setDriver("com.mysql.cj.jdbc.Driver");
        create.setDbConf(properties);

        LinkedHashSet<ColumnInfo> columns = new LinkedHashSet<>();
        final ColumnInfo c1 = new ColumnInfo();
        c1.setName("id");
        c1.setType(ColumnType.stringType().length(32));
        c1.setPk(true);
        c1.setComment("主键");
        columns.add(c1);

        final ColumnInfo c2 = new ColumnInfo();
        c2.setName("t_desc");
        c2.setType(ColumnType.stringType().length(32));
        c2.setNonNull(true);
        c2.setComment("描述");
        columns.add(c2);
        create.setColumns(columns);

        final ColumnInfo c3 = new ColumnInfo();
        c3.setType(ColumnType.stringType().length(32));
        c3.setName("t_name");
        c3.setComment("名字");
        columns.add(c3);

        final ColumnInfo c4 = new ColumnInfo();
        c4.setType(ColumnType.doubleType().length(10).precision(2));
        c4.setName("t_price");
        c4.setComment("价格");
        columns.add(c4);
        create.setColumns(columns);

        final ColumnInfo c5 = new ColumnInfo();
        c5.setType(ColumnType.datetimeType());
        c5.setName("createTime");
        c5.setComment("创建时间");
        columns.add(c5);
        create.setColumns(columns);

        StringWriter stringWriter = new StringWriter();
        Template template = configuration.getTemplate("template/table/changelog-createTable.ftl", "utf-8");
        template.process(create, stringWriter);
        String createStr = stringWriter.toString();
        System.out.println(createStr);
        final File tempFile = File.createTempFile(create.getTableName() + "-" + create.getVersion(), ".xml");
        System.out.println("路径: " + tempFile.getParent() + "\t 文件名:" + tempFile.getName());
        final FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        IOUtils.write(createStr.getBytes(), fileOutputStream);
        fileOutputStream.close();
        //System.out.println("TableInfo() : " + JSONObject.toJSONString(create));
        try {
//			ChangeLogGeneratorUtils.executor(ConfigBuilder.paramsConf()
//					.changeLogPath(tempFile.getParent())
//					.changeLogFile(tempFile.getName())
//					.driver("com.mysql.cj.jdbc.Driver")
//					.url("jdbc:mysql://192.168.240.185:3306/demo_cjz?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8")
//					.username("root")
//					.password("tmkj@zgb123")
//					.commandConf()
//					.update().builder());


            final TableInfo modify = new TableInfo();
            modify.setTableName("test_cjz");
            modify.setVersion("m1-onlform-2.2.2.0-202009091139");
            modify.setCreateUser("ChengJinzhou");

            LinkedHashSet<ColumnInfo> modifyColumn = new LinkedHashSet<>();
            final ColumnInfo m1 = new ColumnInfo();
            m1.setName("t_age");
            m1.setType(ColumnType.intType().length(5));
            m1.setPk(true);
            m1.setComment("主键");
            modifyColumn.add(c1);
            modify.setColumns(modifyColumn);

            String changeSetStr = "\n<changeSet id=\"m1-demo-2.2.0.0-202005300919\" author=\"ChenLili\">\n" +
                    "        <update tableName=\"M1_RESOURCES\">\n" +
                    "            <column name=\"ORDERNO\" valueNumeric=\"999\"/>\n" +
                    "            <where>ID = 'jw1MUnwu'</where>\n" +
                    "        </update>\n" +
                    "    </changeSet>";
            modify.setChangesets(Arrays.asList(changeSetStr));
            System.out.println(ChangeLogGeneratorUtils.generateChangeLog(modify, createStr));
        } finally {
            tempFile.delete();
        }
    }

}
