package com.cjz.webmvc;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2019-11-25 17:03
 */
public class CodeGenerator {

	/**
	 * 项目根路径
	 */
	public static final String PROJECT_PATH = "E:\\IDEA\\cloud\\web-mvc";
	public static final String AUTH = "chengjz";

	/**
	 * 父包全限定类名
	 */
	public static final String PARENT = "com.cjz.webmvc";

	/**
	 * 业务包名，会拼接上 {@link #PARENT}
	 */
	public static final String MODEL_NAME = "user";

	/**
	 * 忽略的表前缀
	 */
	public static final String[] IGNORE_TABLE_PFX = new String[]{"t_", "T_"};

	/**
	 * 要生成的表名
	 */
	public static final String TABLE_NAMES = "T_USERS";

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

	public static void main(String[] args) {
		// 代码生成器
		AutoGenerator mpg = new AutoGenerator();

		// 全局配置
		GlobalConfig gc = new GlobalConfig();

		System.out.println("projectPath: " + PROJECT_PATH);
		gc.setOutputDir(PROJECT_PATH + "/src/main/java");
		gc.setAuthor(AUTH);
		gc.setOpen(false);
		//实体属性 Swagger2 注解
		gc.setSwagger2(true);
		// 开启AR
		gc.setActiveRecord(true);
		// 是否覆盖文件
		gc.setFileOverride(true);

		gc.setDateType(DateType.ONLY_DATE);

		mpg.setGlobalConfig(gc);


		mpg.setDataSource(DSC);

		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setParent(PARENT);
		pc.setModuleName(MODEL_NAME);
		pc.setMapper("persistence");
		pc.setEntity("persistence.model");
		mpg.setPackageInfo(pc);

		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				// to do nothing
			}
		};

		// 如果模板引擎是 freemarker
		String templatePath = "/templates/mapper.xml.ftl";
		// 如果模板引擎是 velocity
		//String templatePath = "/templates/mapper.xml.vm";

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				// 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
				return PROJECT_PATH + "/src/main/resources/" + PARENT.replace(".", "/") + "/" + pc.getModuleName()
						+ "/persistence/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
			}
		});

		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();

		// 配置自定义输出模板
		//指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
		// templateConfig.setEntity("templates/entity2.java");
		// templateConfig.setService();
		// templateConfig.setController();

		templateConfig.setXml(null);
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setEntitySerialVersionUID(false);

		strategy.setInclude(TABLE_NAMES.split(","));
		strategy.setRestControllerStyle(true);
		// 是否为链式模型（默认 false
		strategy.setChainModel(false);
		strategy.setEntityLombokModel(true);
		strategy.setEntityTableFieldAnnotationEnable(true);
		strategy.setControllerMappingHyphenStyle(true);
		strategy.setTablePrefix(IGNORE_TABLE_PFX);
		mpg.setStrategy(strategy);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.execute();
	}


}
