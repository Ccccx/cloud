package ${package.ServiceImpl};

<#list modelPackages as pkg>
 import ${pkg};
</#list>
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
@Transactional(rollbackFor = Exception.class)
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements IOperationService<${entity}> {


    /**
    * 分隔符
    * between：  createTime=2021 7/7:: 2021 7/8
    * order：orderBy=createTime::desc
    */
    private static final String SPLIT = "::";


    /**
    * model字段列表
    */
    protected static final List<String> fields = Arrays.asList(
        <#list table.fields as field>
            <#if field_has_next>
                "${field.propertyName}",
            <#else >
                "${field.propertyName}"
            </#if>
        </#list>
        );


    /**
    * 保存数据时非空列表
    */
    <#if saveNonnull?? && (saveNonnull?size > 0) >
    protected static Set<String> saveNonnull =  Sets.newHashSet(
        <#list saveNonnull as k>
            <#if k_has_next>
                "${k}",
            <#else >
                "${k}"
            </#if>
        </#list>
        );
        <#else >
    protected static Set<String> saveNonnull = Collections.emptySet();
    </#if>


    /**
    * 更新数据时非空列表
    */
    <#if saveNonnull?? && (saveNonnull?size > 0) >
    protected static Set<String> updateNonnull =  Sets.newHashSet(
        <#list saveNonnull as k>
            <#if k_has_next>
                "${k}",
            <#else >
                "${k}"
            </#if>
        </#list>
        );
        <#else >
    protected static Set<String> updateNonnull = Collections.emptySet();
    </#if>


    @Override
    public IPage<${entity}> pageQuery(StringMap req) {
        final LambdaQueryWrapper<${entity}> queryWrapper = buildBaseWrapper(req);
        Page<${entity}> page =
            new Page<>(Integer.parseInt(Optional.ofNullable(req.get("number")).orElse("1")),
                                Integer.parseInt(Optional.ofNullable(req.get("size")).orElse("10")));
        return super.page(page, queryWrapper);
    }


    @Override
    public List<${entity}> list(StringMap req) {
        return super.list(buildBaseWrapper(req));
    }


    @Override
    public List<${entity}> batchSave(List<StringMap> req) {
        List<${entity}> lists = checkAndGet(req, saveNonnull);
        super.saveBatch(lists);
        return lists;
    }




     @Override
     public List<${entity}> batchUpdate(List<StringMap> req) {
        List<${entity}> lists = checkAndGet(req, updateNonnull);
        super.updateBatchById(lists);
        return lists;
    }


      @Override
    public void batchDelete(List<String> req) {
        super.removeByIds(req);
    }

   @SuppressWarnings("all")
    protected LambdaQueryWrapper<${entity}> buildBaseWrapper(StringMap req) {
        final LambdaQueryWrapper<${entity}> queryWrapper = Wrappers.lambdaQuery(${entity}.class);
    <#if criteriaList?? && (criteriaList?size > 0) >
    <#list criteriaList as field>
        if (req.containsKey("${field.field}")) {
     <#if (field.criteria!"") == "between">
            final String[] split = req.get("${field.field}").split(SPLIT);
            queryWrapper.between(${entity}::get${field.capitalName}, split[0], split[1]);
     <#else>
            queryWrapper.${field.criteria}(${entity}::get${field.capitalName}, req.get("${field.field}"));
     </#if>
        }
    </#list>
    </#if>
        if (req.containsKey("orderBy")) {
            final String[] split = req.get("orderBy").split(SPLIT);
            queryWrapper.orderBy(true,  "asc".equals(split[1]) ,
            (SFunction<${entity}, Object>) table1 -> split[0]);
        }
        return queryWrapper;
    }

    private List<${entity}> checkAndGet(List<StringMap> req, Set<String> requireList) {
         List<${entity}> lists = new ArrayList<>();
         for (StringMap map : req) {
                BeanWrapper wrapper = new BeanWrapperImpl(new ${entity}());
                requireList.forEach(requireField -> {
                    Assert.isTrue(map.containsKey(requireField), requireField + "为必传参数！");
                    final String val = map.get(requireField);
                    Assert.isTrue(StringUtils.isNotEmpty(val), val + "不能为空！");
                    wrapper.setPropertyValue(requireField, val);
                });
                for (String field : fields) {
                    if (requireList.contains(field)) {
                        continue;
                    }
                    final String val = map.get(field);
                    if (StringUtils.isNotEmpty(val)) {
                        wrapper.setPropertyValue(field, val);
                    }
                }
                lists.add((${entity}) wrapper.getWrappedInstance());
         }
         return lists;
    }
}
</#if>
