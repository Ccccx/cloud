package com.example.factorydemo.tree;

import com.alibaba.fastjson.JSONObject;
import com.example.factorydemo.bean.Org;
import com.google.common.collect.Maps;
import com.tiamaes.cloud.m1.core.tree.TreeFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-14 16:14
 */
@Slf4j
 class TreeTest {

    public static   List<Foo> foo = new ArrayList<>();
    public static   List<Org> orgs = new ArrayList<>();
    static {
        final Foo a = new Foo("A", null, null);
        final Foo b = new Foo("B", null, null);
        final Foo a1 = new Foo("A1", "A", "A");
        final Foo a2 = new Foo("A2", "A1", "A-A1");
        final Foo a3 = new Foo("A3", "A2", "A-A1-A2");
        final Foo a11 = new Foo("A11", "A", "A");
        final Foo a22 = new Foo("A22", "A11", "A-A11");
        final Foo a33 = new Foo("A33", "A22", "A-A11-A22");
        final Foo b1 = new Foo("B1", "B", "B");
        final Foo b2 = new Foo("B2", "B1", "B-B1");
        final Foo b3 = new Foo("B3", "B2", "B-B1-B2");

        orgs.add(new Org("1111", "1111", null, 1, null));
        orgs.add(new Org("YgW06azM", "天迈科技", null, 1, null));
        orgs.add(new Org("JsQdTpIA", "研发中心", "YgW06azM", 2, "YgW06azM"));
        orgs.add(new Org("tlouU3k4", "软件部", "JsQdTpIA", 3, "YgW06azM-JsQdTpIA"));
        orgs.add(new Org("45NSxb6g", "VE前端组", "tlouU3k4", 4, "YgW06azM-JsQdTpIA-tlouU3k4"));
        orgs.add(new Org("49nt9ifh", "M1平台组", "tlouU3k4", 4, "YgW06azM-JsQdTpIA-tlouU3k4"));
        orgs.add(new Org("1405081704241737730", "测试部", "JsQdTpIA", 3, "YgW06azM-JsQdTpIA"));
        orgs.add(new Org("1405081763050074113", "测试1", "1405081704241737730", 4, "YgW06azM-JsQdTpIA-1405081704241737730"));

        foo.add(a);
        foo.add(b);
        foo.add(a1);
        foo.add(a2);
        foo.add(a3);
        foo.add(a11);
        foo.add(a22);
        foo.add(a33);
        foo.add(b1);
        foo.add(b2);
        foo.add(b3);
    }


    @Test
    void t2() {
        final List<Foo> result = filter(foo, "A22",  Foo::getId, Foo::getId, Foo::getPKey, ArrayList::new);
        log.info("{}", result);
    }

    @Test
    void t3() {
        for (Org org : orgs) {
            org.setKeys(null);
        }
        List<Org> result = buildTreeFullKeys(orgs, Org::getId, Org::getPId, Org::setKeys);
        log.info("{}", JSONObject.toJSON(result));
        List<Org> filterList = filter(result, "天迈科技", Org::getOrg, Org::getId, Org::getKeys, (form) -> form.stream().sorted(Comparator.comparing(Org::getId).thenComparing(Org::getPId)).collect(Collectors.toList()));
        log.info("{}", JSONObject.toJSON(TreeFactory.build(filterList, Org::getId, Org::getPId, Org::setChildren)));
    }

    public <T> List<T> filter(List<T> target, String queryStr, Function<T, String> queryFc, Function<T, String> idFc, Function<T, String> pFullKey, Function<Collection<T>, List<T>> sort) {
        // 结果集
        Set<T> result = new HashSet<>();
        if (StringUtils.isEmpty(queryStr)) {
            return target;
        }
        try {
            // 查询参数映射
            Map<String, T> paramMap = new HashMap<>(8);
            // ID 映射
            Map<String, T> idMap = target.stream().collect(Collectors.toMap(idFc, v->v));
            // 完整key映射
            final MultiValueMap<String, T> fullKeyMap =  new LinkedMultiValueMap<>();
            for (T f : target) {
                fullKeyMap.add(pFullKey.apply(f), f);
            }
            // 过滤包含请求参数的对象
            for (T f : target) {
                if (queryFc.apply(f).contains(queryStr)) {
                    paramMap.put(queryFc.apply(f), f);
                }
            }
            // 对查到的数据进行字父级数据查询
            for (T f : paramMap.values()) {
                // 对完整路径拆分出父级
                final String pKey = pFullKey.apply(f);
                boolean isTop = true;
                if (StringUtils.isNotEmpty(pKey)) {
                    isTop = false;
                    final StringTokenizer tokenizer = new StringTokenizer(pKey, "-");
                    while (tokenizer.hasMoreTokens()) {
                        final String token = tokenizer.nextToken();
                        result.add(idMap.get(token));
                    }
                }
                // 放入当前层级
                result.add(f);
                // 放入子级
                String fixKey = isTop ? idFc.apply(f) : pFullKey.apply(f) + "-" + idFc.apply(f);
                if (StringUtils.isNotEmpty(fixKey)) {
                    for (String key : fullKeyMap.keySet()) {
                        if (Objects.nonNull(key)  && key.startsWith(fixKey)) {
                            result.addAll(fullKeyMap.get(key));
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.info("过滤数据发生异常， 过滤参数： {}", queryStr, e);
        }
        return sort.apply(new ArrayList<>(result));
    }


    /**
     *
     * @param target 平铺数据
     * @param idFc id字段
     * @param pIdFc  父ID字段
     * @param fullParentKeys     存储父KEY完整路径
     * @param <T>  结构数据
     * @return ig
     */
    public static <T>List<T> buildTreeFullKeys(List<T> target, Function<T, Object> idFc, Function<T, Object> pIdFc, BiConsumer<T, String> fullParentKeys) {
        Map<Object, FullKey> pKeys =  Maps.newHashMapWithExpectedSize(target.size());
        // 构建父级Key
        target.forEach(v -> {
            final Object pId = pIdFc.apply(v);
            final FullKey pidKey = pKeys.getOrDefault(pId, new FullKey(pId, null));
            pKeys.put(pId, pidKey);
            final Object id = idFc.apply(v);
            final FullKey key = pKeys.getOrDefault(id, new FullKey(id, null));
            key.setParentKey(pidKey);
            pKeys.put(id, key);
        });

        target.forEach(v -> {
            final Object pId = pIdFc.apply(v);
            if (Objects.nonNull(pId)) {
                final FullKey fullKey = pKeys.get(pId);
                fullParentKeys.accept(v, fullKey.getKey());
            }
        });
        return target;
    }

    @Data
    @AllArgsConstructor
    public static class Foo {
        private String id;
        private String pid;
        private String pKey;
    }




    @Data
    @AllArgsConstructor
    public static class FullKey {
        private Object key;
        private FullKey parentKey;

        public String  getKey() {
            if (Objects.isNull(parentKey) || Objects.isNull(parentKey.getKey())) {
                if (Objects.isNull(key)) {
                    return null;
                } else {
                    return key.toString();
                }
            }
            return parentKey.getKey() +"-" + key;
        }
    }
}
