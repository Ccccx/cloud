package com.example.mybatis.rest.persistence.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mybatis.rest.model.BaseModel;
import com.github.benmanes.caffeine.cache.CacheLoader;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 表元信息
 * </p>
 *
 * @author chengjz
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("onl_table")
@ApiModel(value="Table对象", description="表元信息")
public class Table1 extends BaseModel {

    @ApiModelProperty(value = "id")
    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "表名")
    @TableField("table_name")
    private String tableName;

    @ApiModelProperty(value = "表注释")
    @TableField("table_comment")
    private String tableComment;

    @ApiModelProperty(value = "表单展示名称")
    @TableField("form_name")
    private String formName;

    @ApiModelProperty(value = "JAVA实体类名")
    @TableField("java_name")
    private String javaName;

    @ApiModelProperty(value = "表类型 1 单表 2 附表 3 树表 4 左树")
    @TableField("table_type")
    private Integer tableType;

    @ApiModelProperty(value = "数据源")
    @TableField("database_conf")
    private String databaseConf;

    @ApiModelProperty(value = "描述")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "版本号")
    @TableField("version_code")
    private Integer versionCode;

    @ApiModelProperty(value = "Model全类名")
    @TableField("model_class")
    private String modelClass;

    @ApiModelProperty(value = "Model源码")
    @TableField("model_code")
    private String modelCode;

    @ApiModelProperty(value = "Mapper全类名")
    @TableField("mapper_class")
    private String mapperClass;

    @ApiModelProperty(value = "Mapper源码")
    @TableField("mapper_code")
    private String mapperCode;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;

//
//
//    @Override
//    protected Serializable pkVal() {
//        return this.id;
//    }
//
//
//    @Override
//    public IPage<Table1> pageQuery(StringMap req) {
//        LambdaQueryWrapper<Table1> queryWrapper = buildBaseWrapper(req);
//        Page<Table1> page =
//                new Page<>(Integer.parseInt(Optional.ofNullable(req.get("number")).orElse("1")),
//                        Integer.parseInt(Optional.ofNullable(req.get("size")).orElse("10")));
//        return super.selectPage(page, queryWrapper);
//    }
//
//    @Override
//    public List<Table1> list(StringMap req) {
//        return super.selectList(buildBaseWrapper(req));
//    }
//
//    /**
//     * 保存数据时需要校验的参数
//     */
//    protected static List<String> SAVE_NONNULL = Arrays.asList(
//            "id",
//            "tableName"
//    );
//
//    protected static Log log = LogFactory.getLog(Table1.class);
//
//    @Override
//    public List<Table1> save(List<StringMap> req) {
//        List<Table1> lists = new ArrayList<>();
//        for (Map<String, String> map : req) {
//            BeanWrapper wrapper = new BeanWrapperImpl(new Table1());
//             map.forEach((k ,v) -> {
//                 if (SAVE_NONNULL.contains(k)) {
//                     Assert.isTrue(Objects.nonNull(v), k + "不能为空！");
//                 }
//                 if (Objects.nonNull(v)) {
//                     wrapper.setPropertyValue(k, v);
//                 }
//             });
//            lists.add((Table1)wrapper.getWrappedInstance());
//        }
//        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
//        SqlHelper.executeBatch(getClass(), log, lists, 1000, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
//        return lists;
//    }
//
//
//    /**
//     * 更新数据时非空列表
//     */
//    protected static List<String> UPDATE_NONNULL = Arrays.asList(
//            "id"
//    );
//
//    @Override
//    public List<Table1> update(List<StringMap> req) {
//        List<Table1> lists = new ArrayList<>();
//        for (Map<String, String> map : req) {
//            BeanWrapper wrapper = new BeanWrapperImpl(new Table1());
//            map.forEach((k ,v) -> {
//                if (SAVE_NONNULL.contains(k)) {
//                    Assert.isTrue(Objects.nonNull(v), k + "不能为空！");
//                }
//                if (Objects.nonNull(v)) {
//                    wrapper.setPropertyValue(k, v);
//                }
//            });
//            lists.add((Table1)wrapper.getWrappedInstance());
//        }
//        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
//        SqlHelper.executeBatch(getClass(), log, lists, 1000,
//                (sqlSession, entity) -> {
//                    MapperMethod.ParamMap<Table1> param = new MapperMethod.ParamMap<>();
//                    param.put(Constants.ENTITY, entity);
//                    sqlSession.update(sqlStatement, param);
//                });
//        return lists;
//    }
//
//    @Override
//    public void delete(List<String> req) {
////        for (String id : req) {
////            super.deleteById(id);
////        }
//
////        Map<String, Object> map = CollectionUtils.newHashMapWithExpectedSize(1);
////        map.put(Constants.COLLECTION, req);
////        SqlSession sqlSession = sqlSession();
////        try {
////            SqlHelper.retBool(sqlSession.delete(sqlStatement(SqlMethod.DELETE_BATCH_BY_IDS), map));
////        } finally {
////            closeSqlSession(sqlSession);
////        }
//
//        String sqlStatement = sqlStatement(SqlMethod.DELETE_BY_ID);
//        SqlHelper.executeBatch(getClass(), log, req, 1000,
//                (sqlSession, id) -> sqlSession.update(sqlStatement, id));
//    }
//
//    @SuppressWarnings("all")
//    protected LambdaQueryWrapper<Table1> buildBaseWrapper(Map<String, String> req) {
//        final LambdaQueryWrapper<Table1> queryWrapper = Wrappers.lambdaQuery(Table1.class);
//        if (MapUtils.isEmpty(req)) {
//            return queryWrapper;
//        }
//        if (req.containsKey("id")) {
//            queryWrapper.eq(Table1::getId, req.get("id"));
//        }
//        if (req.containsKey("tableName")) {
//            queryWrapper.ne(Table1::getTableName, req.get("tableName"));
//        }
//        if (req.containsKey("tableComment")) {
//            queryWrapper.gt(Table1::getTableComment, req.get("tableComment"));
//        }
//        if (req.containsKey("formName")) {
//            queryWrapper.ge(Table1::getFormName, req.get("formName"));
//        }
//        if (req.containsKey("javaName")) {
//            queryWrapper.lt(Table1::getJavaName, req.get("javaName"));
//        }
//        if (req.containsKey("tableType")) {
//            queryWrapper.le(Table1::getTableType, req.get("tableType"));
//        }
//        if (req.containsKey("databaseConf")) {
//            queryWrapper.like(Table1::getDatabaseConf, req.get("databaseConf"));
//        }
//        if (req.containsKey("remark")) {
//            final String[] split = req.get("remark").split("::");
//            queryWrapper.between(Table1::getRemark, split[0], split[1]);
//        }
//        if (req.containsKey("orderBy")) {
//            final String[] split = req.get("orderBy").split("::");
//            queryWrapper.orderBy(true,   "asc".equals(split[1]) ,
//                    (SFunction<Table1, Object>) table1 -> split[0]);
//        }
//        return queryWrapper;
//    }



    private void t1() {
        final CacheLoader<Table1, Long> getId = Table1::getId;
    }
}
