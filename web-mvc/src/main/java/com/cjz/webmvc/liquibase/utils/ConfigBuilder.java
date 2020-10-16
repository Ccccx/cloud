package com.cjz.webmvc.liquibase.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Liquibase参数构建器对象,通过导航的形式引导构建参数
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-09-01 8:42
 */
public class ConfigBuilder {

	/**
	 * 最终的参数列表
	 */
	private Map<String, String> map = new LinkedHashMap<>();

	private ConfigBuilder() {

	}

	/**
	 * 通过配置会文件构建参数
	 *
	 * @return 配置文件构建器
	 */
	public static Properties propertiesConf() {
		return new Properties();
	}

	/**
	 * 通过编码形式构建参数
	 *
	 * @return 参数构建器
	 */
	public static Params paramsConf() {
		return new Params();
	}

	public Map<String, String> getMap() {
		return map;
	}

	/**
	 * 配置文件参数
	 */
	public static class Properties {

		private String defaultsFile = "classpath:generator/liquibase.properties";
		private String changeLogPath = "changelog";
		private String changeLogFile = "changelog.xml";

		private Properties() {
		}

		/**
		 * 默认配置文件路径为类路径下: classpath:generator/liquibase.properties
		 * 配置文件的内容为:
		 * <p>
		 * <pre>
		 *       {@code
		 *      driver=com.mysql.cj.jdbc.Driver
		 *      url=jdbc:mysql://local:13306/m1_onl_form?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
		 *      username=root
		 *      password=root
		 *       }
		 *     </pre>
		 * </p>
		 *
		 * @param defaultsFile 配置文件位置
		 * @return this
		 */
		public Properties defaultsFile(String defaultsFile) {
			this.defaultsFile = defaultsFile;
			return this;
		}

		/**
		 * 默认changelog文件路径为根目录下: changelog
		 *
		 * @param changeLogPath 配置文件位置
		 * @return this
		 */
		public Properties changeLogPath(String changeLogPath) {
			this.changeLogPath = changeLogPath;
			return this;
		}

		/**
		 * 默认changeLogFile文件路径为根目录下: {@link #changeLogPath}/changelog.xml
		 *
		 * @param changeLogFile changeLogFile文件名称
		 * @return this
		 */
		public Properties changeLogFile(String changeLogFile) {
			this.changeLogFile = changeLogFile;
			return this;
		}

		/**
		 * 执行指令构建器
		 *
		 * @return 指令配置
		 */
		public Command commandConf() {
			return new Command(this);
		}

	}

	public static class Params {
		private String changeLogPath = "changelog";
		private String changeLogFile = "changelog.xml";
		private String createUser;
		private String driver;
		private String username;
		private String password;
		private String url;

		/**
		 * 默认changelog文件路径为根目录下: changelog
		 *
		 * @param changeLogPath 配置文件位置
		 * @return this
		 */
		public Params changeLogPath(String changeLogPath) {
			this.changeLogPath = changeLogPath;
			return this;
		}

		/**
		 * 默认changeLogFile文件路径为根目录下: {@link #changeLogPath}/changelog.xml
		 *
		 * @param changeLogFile changeLogFile文件名称
		 * @return this
		 */
		public Params changeLogFile(String changeLogFile) {
			this.changeLogFile = changeLogFile;
			return this;
		}

		/**
		 * 创建用户,对应每行changeSet里的auth
		 *
		 * @param createUser 创建用户
		 * @return this
		 */
		public Params createUser(String createUser) {
			this.createUser = createUser;
			return this;
		}


		/**
		 * 驱动名称
		 *
		 * @param driver 驱动
		 * @return this
		 */
		public Params driver(String driver) {
			this.driver = driver;
			return this;
		}

		/**
		 * 用户名
		 *
		 * @param username 用户名
		 * @return this
		 */
		public Params username(String username) {
			this.username = username;
			return this;
		}

		/**
		 * 数据库密码
		 *
		 * @param password 数据库密码
		 * @return this
		 */
		public Params password(String password) {
			this.password = password;
			return this;
		}

		/**
		 * 数据库连接
		 *
		 * @param url 数据库连接
		 * @return this
		 */
		public Params url(String url) {
			this.url = url;
			return this;
		}

		/**
		 * 执行指令构建器
		 *
		 * @return 指令配置
		 */
		public Command commandConf() {
			return new Command(this);
		}
	}

	public static class Command {
		private Properties properties;
		private Params params;
		private String command;

		private Command() {
		}

		private Command(Properties properties) {
			this.properties = properties;
		}

		private Command(Params params) {
			this.params = params;
		}

		/**
		 * 同步指令: 会读取changeLogFile文件,然后数据库同,这个会自动执行SQL
		 *
		 * @return 构建器
		 * @see #updateSQL 与当前方法相比,不会自动执行SQL
		 */
		public Builder update() {
			this.command = "update";
			return new Builder(this);
		}

		/**
		 * 同步指令,但不会执行SQL,会读取changeLogFile文件.
		 * 同时{@link ChangeLogGeneratorUtils#executor(ConfigBuilder)}会返回生成的SQL.
		 *
		 * @return 构建器
		 * @see #update  与当前方法相比,会自动执行SQL
		 */
		public Builder updateSQL() {
			this.command = "updateSQL";
			return new Builder(this);
		}

		/**
		 * 更多参考地址: https://docs.liquibase.com/commands/community/home.html
		 *
		 * @param command 其他命令
		 * @return 构建器
		 */
		public Builder command(String command) {
			this.command = command;
			return new Builder(this);
		}

		/**
		 * 生成数据库的changeLogFile文件,文件的位置changeLogPath的参数,文件名为changeLogFile的配置参数
		 *
		 * @return 构建器
		 */
		public Builder generator() {
			this.command = "generateChangeLog";
			return new Builder(this);
		}
	}

	public static class Builder {
		private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		private Command command;

		private Builder() {
		}

		private Builder(Command command) {
			this.command = command;
		}

		/**
		 * 完成构建
		 *
		 * @return 最终结果
		 */
		public ConfigBuilder builder() {
			final ConfigBuilder configBuilder = baseInfo();
			validateAndSet("command", this.command.command, configBuilder.map);
			return configBuilder;
		}

		/**
		 * 运行添加一些额外参数.
		 * 比如,调整下日志级别
		 * <p>
		 * {@code  ConfigBuilder.PropertiesConf().commandConf().generator().customThenBuilder(map -> map.put("logLevel", "debug"))}
		 * </p>
		 *
		 * @param callback 额外操作的参数回调
		 * @return 最终结果
		 */
		public ConfigBuilder customThenBuilder(Consumer<Map<String, String>> callback) {
			final ConfigBuilder configBuilder = baseInfo();
			callback.accept(configBuilder.map);
			validateAndSet("command", this.command.command, configBuilder.map);
			return configBuilder;
		}

		private ConfigBuilder baseInfo() {
			ConfigBuilder configBuilder = new ConfigBuilder();
			Map<String, String> paramMap = new LinkedHashMap<>();
			final Properties properties = command.properties;
			final Params params = command.params;
			if (properties != null) {
				validateAndSet("defaultsFile", parsePropsFile(properties.defaultsFile), paramMap);
				validateAndSet("changeLogFile", changeLogFile(properties.changeLogPath, properties.changeLogFile), paramMap);
			} else if (params != null) {
				validateAndSet("changeLogFile", changeLogFile(params.changeLogPath, params.changeLogFile), paramMap);
				validateAndSet("driver", params.driver, paramMap);
				validateAndSet("url", params.url, paramMap);
				validateAndSet("username", params.username, paramMap);
				validateAndSet("password", params.password, paramMap);
				setIfNonnull("changeSetAuthor", params.createUser, paramMap);
			}
			configBuilder.map = paramMap;
			return configBuilder;
		}


		private String parsePropsFile(String filePath) {
			final Resource file = resolver.getResource(filePath);
			Assert.isTrue(file.exists(), filePath + "配置文件不存在");
			try {
				URI uri = file.getURI();
				return uri.getPath();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "";
		}

		private String changeLogFile(String changeLogPath, String changeLogFile) {
			final Path path = Paths.get(changeLogPath);
			try {
				Files.createDirectories(path);
				return path.resolve(changeLogFile).toUri().getPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}

		private <T> void validateAndSet(String key, String value, Map<String, String> paramMap) {
			Assert.isTrue(value != null, "参数不合法");
			paramMap.put(key, value);
		}

		private <T> void setIfNonnull(String key, String value, Map<String, String> paramMap) {
			if (StringUtils.isNotEmpty(value)) {
				paramMap.put(key, value);
			}
		}
	}


}
