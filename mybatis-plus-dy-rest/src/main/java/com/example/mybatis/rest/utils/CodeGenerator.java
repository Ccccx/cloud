package com.example.mybatis.rest.utils;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.support.CustomInjectionConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Data;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengjz
 * @version 1.0
 * @since 2019-11-25 17:03
 */
public class CodeGenerator {

    public static final String AUTH = "Dynamic Generator";

    /**
     * 父包全限定类名
     */
    public static final String PARENT = "cjz.generator.mybatis";

    /**
     * 模块名称
     */
    public static final String MODULE_NAME = "dynamic";

    /**
     * Entity包名
     */
    private static final String ENTITY = "model";
    /**
     * Mapper包名
     */
    private static final String MAPPER = "dao";
    /**
     * 规范的mapper包名
     */
    public static final String MAPPER_CANONICAL_NAME = PARENT + "." + MODULE_NAME + "." + MAPPER;
    /**
     * 规范的实体包名
     */
    public static final String ENTITY_CANONICAL_NAME = MAPPER_CANONICAL_NAME + "." + ENTITY;
    /**
     * service包名
     */
    private static final String SERVICE = "service";
    /**
     * 规范的service包名
     */
    public static final String SERVICE_CANONICAL_NAME = PARENT + "." + MODULE_NAME + "." + SERVICE;
    /**
     * serviceImpl包名
     */
    private static final String SERVICE_IMPL = "impl";
    /**
     * 规范的serviceImpl包名
     */
    public static final String SERVICE_IMPL_CANONICAL_NAME = SERVICE_CANONICAL_NAME + "." + SERVICE_IMPL;

    public static final String TEMPLATE_DIR = "/dy";

    public static final String  TEMPLATE_ENTITY_JAVA = TEMPLATE_DIR +  "/entity.java";
    public static final String  TEMPLATE_MAPPER = TEMPLATE_DIR + "/mapper.java";
    public static final String  TEMPLATE_SERVICE_IMPL = TEMPLATE_DIR + "/serviceImpl.java";

    public static CodeResult run(String tableName, DataSourceConfig dsc) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();

        gc.setAuthor(AUTH);
        gc.setOpen(false);
        // gc.setActiveRecord(true);
        long time =  System.currentTimeMillis();
        gc.setEntityName("%s" +time);
        gc.setMapperName("%sMapper" + time);
        gc.setServiceImplName("%sServiceImpl" + time);
        //实体属性 Swagger2 注解
        gc.setSwagger2(true);

        gc.setDateType(DateType.ONLY_DATE);
        mpg.setGlobalConfig(gc);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(PARENT);
        pc.setModuleName(MODULE_NAME);
        pc.setEntity(MAPPER + "." + ENTITY);
        pc.setMapper(MAPPER);
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new CustomInjectionConfig();
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 不配置对应模板,就不会生成对应类
        templateConfig.setController(null);
        templateConfig.setService(null);
        templateConfig.setEntity(TEMPLATE_ENTITY_JAVA);
        templateConfig.setMapper(TEMPLATE_MAPPER);
        templateConfig.setServiceImpl(TEMPLATE_SERVICE_IMPL);
        templateConfig.setXml(null);

        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntitySerialVersionUID(false);
        strategy.setSuperEntityClass(BaseModel.class);
        strategy.setInclude(tableName.split(","));
        // 是否为链式模型（默认 false
        strategy.setChainModel(false);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        final StringFreemarkerTemplateEngine freemarkerTemplateEngine = new StringFreemarkerTemplateEngine();
        mpg.setTemplateEngine(freemarkerTemplateEngine);
        // 生成代码
        mpg.execute();
        final List<TableInfo> tableInfoList = freemarkerTemplateEngine.getConfigBuilder().getTableInfoList();
        final TableInfo tableInfo = tableInfoList.get(0);
        final CodeResult codeResult = new CodeResult();
        codeResult.setTableInfo(tableInfo);
        codeResult.setEntityName(tableInfo.getEntityName());
        codeResult.setEntityCode(freemarkerTemplateEngine.getSourceCode(tableInfo.getEntityName()));
        codeResult.setMapperName(tableInfo.getMapperName());
        codeResult.setMapperCode(freemarkerTemplateEngine.getSourceCode(tableInfo.getMapperName()));
        codeResult.setServiceImplName(tableInfo.getServiceImplName());
        codeResult.setServiceImplCode(freemarkerTemplateEngine.getSourceCode(tableInfo.getServiceImplName()));
        return codeResult;
    }


    public static class StringFreemarkerTemplateEngine extends AbstractTemplateEngine {

        private Configuration configuration;

        private Map<String, String> sourceMap;

        @Override
        public StringFreemarkerTemplateEngine init(ConfigBuilder configBuilder) {
            super.init(configBuilder);
            configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            configuration.setDefaultEncoding(ConstVal.UTF8);
            configuration.setClassForTemplateLoading(StringFreemarkerTemplateEngine.class, StringPool.SLASH);
            sourceMap = new HashMap<>(8);
            return this;
        }


        @Override
        public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
            Template template = configuration.getTemplate(templatePath);
            StringWriter stringWriter = new StringWriter();
            template.process(objectMap, stringWriter);
            logger.debug("\n {}", stringWriter);
            final String javaName = outputFile.substring(outputFile.replace("\\", "/").lastIndexOf("/") + 1);
            sourceMap.put(javaName.substring(0, javaName.indexOf(".")), stringWriter.toString());
        }

        /**
         * 构建模板引擎参数
         *
         * @param tableInfo 表信息
         * @return 配置参数
         */
        @Override
        public Map<String, Object> getObjectMap(TableInfo tableInfo) {
            return super.getObjectMap(tableInfo);
        }

        @Override
        public AbstractTemplateEngine mkdirs() {
            return this;
        }

        @Override
        public String templateFilePath(String filePath) {
            return filePath + ".ftl";
        }

        public String getSourceCode(String javaName) {
            return sourceMap.getOrDefault(javaName, "");
        }
    }

    @Data
    public static class CodeResult {
        private String entityName;
        private String entityCode;
        private String mapperName;
        private String mapperCode;
        private String serviceImplName;
        private String serviceImplCode;
        private TableInfo tableInfo;
    }


}
