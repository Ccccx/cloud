package com.example.mybatis.rest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.example.mybatis.rest.model.BaseModel;
import com.example.mybatis.rest.service.IOperationService;
import com.example.mybatis.rest.support.StringMap;
import com.google.common.collect.Sets;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2019-11-25 17:03
 */
public class CodeGenerator {
    // D:\software\apache-ant-1.9.15\bin
    /**
     * 项目根路径
     */
    public static final String PROJECT_PATH = "E:\\IDEA\\cloud\\mybatis-plus-dy-rest";
    public static final String AUTH = "chengjz";

    /**
     * 父包全限定类名
     */
    public static final String PARENT = "com.example.mybatis";

    /**
     * 业务包名，会拼接上 {@link #PARENT}
     */
    public static final String MODEL_NAME = "rest";

    /**
     * 忽略的表前缀
     */
    public static final String[] IGNORE_TABLE_PFX = new String[]{"t_", "T_", "onl_"};

    /**
     * 要生成的表名
     */
    // public static final String TABLE_NAMES = "sys_menu";
    public static final String TABLE_NAMES = "onl_table,onl_table_ref,onl_field";
    /**
     * 数据源配置
     */
    public static final DataSourceConfig DSC = new DataSourceConfig();

    static {
        DSC.setUrl("jdbc:mysql://cx:13306/cloud?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8");
        DSC.setDriverName("com.mysql.cj.jdbc.Driver");
        DSC.setUsername("root");
        DSC.setPassword("meiyoumima.0");

//        DSC.setUrl("jdbc:mysql://192.168.240.185:3306/demo-cjz?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8");
//        DSC.setDriverName("com.mysql.cj.jdbc.Driver");
//        DSC.setUsername("root");
//        DSC.setPassword("tmkj@zgb123");

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
        gc.setActiveRecord(false);
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
        InjectionConfig cfg = new CustomInjectionConfig();

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/entity.java.ftl";
        // 如果模板引擎是 velocity
        //String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
/*        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return PROJECT_PATH + "/src/main/resources/" + PARENT.replace(".", "/") + "/" + pc.getModuleName()
                        + "/persistence/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });*/

        // cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        // 不配置对应模板,就不会生成对应类
        templateConfig.setController(null);
        templateConfig.setService(null);
        templateConfig.setServiceImpl(null);
        templateConfig.setXml(null);
        //templateConfig.setMapper(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntitySerialVersionUID(false);
        strategy.setInclude(TABLE_NAMES.split(","));
        strategy.setSuperEntityClass(BaseModel.class);
        // 生成rest controller
        //strategy.setRestControllerStyle(true);
        // 是否为链式模型（默认 false
        strategy.setChainModel(false);
        // 启用lombok
        strategy.setEntityLombokModel(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(IGNORE_TABLE_PFX);
        mpg.setStrategy(strategy);
        //  以文件方式输出
        final AbstractTemplateEngine freemarkerTemplateEngine = new FreemarkerTemplateEngine();
        mpg.setTemplateEngine(freemarkerTemplateEngine);
        mpg.execute();

        // 以字符串方式输出
//        final StringFreemarkerTemplateEngine freemarkerTemplateEngine = new StringFreemarkerTemplateEngine();
//        mpg.setTemplateEngine(freemarkerTemplateEngine);
//        mpg.execute();
//        final List<TableInfo> tableInfoList = freemarkerTemplateEngine.getConfigBuilder().getTableInfoList();
//        for (TableInfo tableInfo : tableInfoList) {
//            System.out.println(tableInfo.getEntityName() + "\t" + freemarkerTemplateEngine.getSourceCode(tableInfo.getEntityName()));
//            System.out.println(tableInfo.getMapperName() + "\t" + freemarkerTemplateEngine.getSourceCode(tableInfo.getMapperName()));
//        }

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
            sourceMap = new HashMap<>();
            return this;
        }


        @Override
        public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
            Template template = configuration.getTemplate(templatePath);
            StringWriter stringWriter = new StringWriter();
            template.process(objectMap, stringWriter);
            logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
            logger.debug("\n {}", stringWriter.toString() );
            final String substring = outputFile.substring(outputFile.lastIndexOf("\\") + 1);
            sourceMap.put(substring.substring(0, substring.indexOf(".")), stringWriter.toString());
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

    public static class CustomInjectionConfig extends InjectionConfig {


        @Override
        public void initMap() {

        }

        @Override
        public Map<String, Object> prepareObjectMap(Map<String, Object> objectMap) {
            final String entity = (String) objectMap.get("entity");

            final TableInfo tableInfo = (TableInfo) objectMap.get("table");
            final Set<String> importPackages = new HashSet<>();
            importPackages.add(Constants.class.getName());
            importPackages.add(SFunction.class.getName());
            importPackages.add(LambdaQueryWrapper.class.getName());
            importPackages.add(IPage.class.getName());
            importPackages.add(Wrappers.class.getName());
            importPackages.add(SqlMethod.class.getName());
            importPackages.add(SqlHelper.class.getName());
            importPackages.add(BeanWrapper.class.getName());
            importPackages.add(BeanWrapperImpl.class.getName());
            importPackages.add(Page.class.getName());
            importPackages.add(MapUtils.class.getName());
            importPackages.add(MapperMethod.class.getName());
            importPackages.add(Log.class.getName());
            importPackages.add(LogFactory.class.getName());
            importPackages.add(Assert.class.getName());
            importPackages.add(StringMap.class.getName());
            importPackages.add(SqlSession.class.getName());
            importPackages.add("java.util.*");
            importPackages.add(CollectionUtils.class.getName());
            importPackages.add(StringUtils.class.getName());
            importPackages.add(Sets.class.getName());
            importPackages.add(IOperationService.class.getName());

            importPackages.removeAll(tableInfo.getImportPackages());
            objectMap.put("modelPackages", importPackages);

            if (Objects.equals(entity, "SysMenu")) {
                List<com.example.mybatis.rest.support.QueryCriteria> list = new ArrayList<>();
                list.add(new com.example.mybatis.rest.support.QueryCriteria("eq", "menuId"));
                list.add(new com.example.mybatis.rest.support.QueryCriteria("ge", "orderNum"));
                list.add(new com.example.mybatis.rest.support.QueryCriteria("like", "name"));
                objectMap.put("criteriaList", list);

                objectMap.put("saveNonnull", Arrays.asList("type", "url", "name"));

                objectMap.put("updateNonnull", Arrays.asList("id"));
            }
            return objectMap;
        }
    }

    @Data
    @AllArgsConstructor
    public static class QueryCriteria {
        private String criteria;
        private String field;

        public String getCapitalName() {
            if (field.length() <= 1) {
                return field.toUpperCase();
            }
            String setGetName = field;
            // 第一个字母 小写、 第二个字母 大写 ，特殊处理
            String firstChar = setGetName.substring(0, 1);
            if (Character.isLowerCase(firstChar.toCharArray()[0])
                    && Character.isUpperCase(setGetName.substring(1, 2).toCharArray()[0])) {
                return firstChar.toLowerCase() + setGetName.substring(1);
            }
            return firstChar.toUpperCase() + setGetName.substring(1);
        }

    }
}
