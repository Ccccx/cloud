package com.cjz.webmvc.liquibase.utils;


import com.cjz.webmvc.liquibase.support.DbConf;
import com.cjz.webmvc.liquibase.support.TableInfo;
import liquibase.integration.commandline.Main;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Liquibase工具类
 * <p>
 * 实例1, 完全通过参数形式使用:
 * <pre>
 *     {@code
 *      ChangeLogGeneratorUtils.executor(ConfigBuilder.ParamsConf()
 *      .changeLogFile("classpath:changelog.xml")
 *      .driver("com.mysql.cj.jdbc.Driver")
 *      .url("jdbc:mysql://localhost:13306/m1_onl_form?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8")
 *      .username("root")
 *      .password("root")
 *      .commandConf()
 *      .update().builder());
 *      }
 * </pre>
 * </p>
 * <p>
 * 实例2, 完全通过配置文件形式使用:
 * <pre>
 *      {@code
 *       ChangeLogGeneratorUtils.executor(ConfigBuilder.PropertiesConf()
 *      .commandConf()
 *      .generator()
 *      .customThenBuilder(map -> map.put("logLevel", "debug")));
 *      }
 *  </pre>
 * </p>
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-09-01 8:41
 */
@Slf4j
public class ChangeLogGeneratorUtils {
	private static final CustomClassLoader CUSTOM_CLASS_LOADER = CustomClassLoader.getInstance();

	public static String CREATE_TEMPLATE_FILE = "templates/table/changelog-createTable.ftl";

	public static String MODIFY_COLUMN_FILE = "templates/table/changelog-modifyColumn.ftl";

	public static String CUSTOM_CHANGESET_FILE = "templates/table/changelog-customChangeSet.ftl";

	public static Path TMP_PATH;

	static {
		try {
			final Path clientDelegate = Files.createTempFile("changelog-utils", ".init");
			TMP_PATH = clientDelegate.getParent().toAbsolutePath();
			Files.deleteIfExists(clientDelegate);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用执行器,通常情况下方法是没有返回结果的,当使用了{@link ConfigBuilder.Command#updateSQL()}时才有返回结果,
	 * 返回内容为具体要执行的SQL
	 *
	 * @param builder 构建器
	 * @return command为updateSQL时才有返回值, 返回值为要执行的SQL
	 */
	@SneakyThrows
	public static String executor(ConfigBuilder builder) {
		Thread.currentThread().setContextClassLoader(CUSTOM_CLASS_LOADER);
		final Map<String, String> map = builder.getMap();
		final LinkedHashSet<String> parseParam = parseParam(map);
		PrintStream old = System.out;
		boolean redirectOutStream = "updateSQL".equals(map.get("command"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if (redirectOutStream) {
			PrintStream ps = new PrintStream(bos);
			System.setOut(ps);
		}
		final String[] params = parseParam.toArray(new String[0]);
		Main.run(params);
		if (redirectOutStream) {
			System.out.flush();
			System.setOut(old);
		}
		return bos.toString();
	}

	/**
	 * 更新数据库
	 *
	 * @param tableInfo 数据库信息
	 * @param changelog 变更记录
	 */
	@SneakyThrows
	public static void update(TableInfo tableInfo, String changelog) {
		final Path changeLogFile = TMP_PATH.resolve("changelog-" + tableInfo.getTableName() + ".xml");
		log.debug("\nchangelogFile temp path: {} ", changeLogFile.toUri().getPath());
		log.debug("\nchangelog: {}", changelog);
		final File tempFile = new File(changeLogFile.toUri());
		final FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
		IOUtils.write(changelog.getBytes(), fileOutputStream);
		fileOutputStream.close();
		try {
			final DbConf dbConf = tableInfo.getDbConf();
			if (dbConf != null) {
				ChangeLogGeneratorUtils.executor(ConfigBuilder.paramsConf()
						.changeLogPath(tempFile.getParent())
						.changeLogFile(tempFile.getName())
						.createUser(tableInfo.getCreateUser())
						.url(dbConf.getUrl())
						.username(dbConf.getUser())
						.password(dbConf.getPassword())
						.driver(dbConf.getDriver())
						.commandConf()
						.update().builder());
			} else {
				ChangeLogGeneratorUtils.executor(ConfigBuilder.propertiesConf()
						.changeLogPath(tempFile.getParent())
						.changeLogFile(tempFile.getName())
						.commandConf()
						.update().builder());
			}
		} finally {
			Files.delete(tempFile.toPath());
		}
	}

	/**
	 * 生成changelog
	 *
	 * @param tableInfo 表信息
	 * @param changeLog 历史的changelog, 没有则为null
	 * @return changelog记录
	 */
	public static String generateChangeLog(TableInfo tableInfo, String changeLog) {
		if (StringUtils.isEmpty(tableInfo.getVersion())) {
			tableInfo.setVersion(tableInfo.getTableName() + "-" + DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss"));
		}
		StringBuilder changelogFile = new StringBuilder();
		try {
			Document document;
			if (StringUtils.isEmpty(changeLog)) {
				// 第一次生成
				final String createTable = FreemarkerHelper.buildTemplate(CREATE_TEMPLATE_FILE, tableInfo);
				document = DocumentHelper.parseText(createTable);
			} else {
				// 非初次,进行追加
				document = DocumentHelper.parseText(changeLog);
				if (CollectionUtils.isNotEmpty(tableInfo.getColumns())) {
					final String modifyColumn = FreemarkerHelper.buildTemplate(MODIFY_COLUMN_FILE, tableInfo);
					buildDoc(document, DocumentHelper.parseText(modifyColumn));
				}
			}
			if (CollectionUtils.isNotEmpty(tableInfo.getChangesets())) {
				// 自定义changeSet非空时,进行追加
				final String customChangeset = FreemarkerHelper.buildTemplate(CUSTOM_CHANGESET_FILE, tableInfo);
				buildDoc(document, DocumentHelper.parseText(customChangeset));
			}
			StringWriter stringWriter = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
			xmlWriter.write(document);
			xmlWriter.close();
			changelogFile.append(stringWriter.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return changelogFile.toString();
	}

	private static void buildDoc(Document originalDoc, Document freshDoc) {
		final Element rootElement = originalDoc.getRootElement();
		freshDoc.getRootElement().elements().forEach(element -> {
			rootElement.add(element.createCopy());
		});
	}


	private static LinkedHashSet<String> parseParam(Map<String, String> map) {
		final LinkedHashSet<String> configs = map.keySet().stream().filter(key -> !"command".equals(key))
				.map(key -> String.format("--%s=%s", key, map.get(key))).collect(Collectors.toCollection(LinkedHashSet::new));
		configs.add(map.get("command"));
		return configs;
	}

}
