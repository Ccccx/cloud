package com.example.factorydemo.tree;

import liquibase.pro.packaged.F;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;
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
    void t1() {
        //Collections.shuffle(foo);
        Map<String, FullKey> pKeys = new HashMap<>();
        foo.forEach(v -> {
            final FullKey pidKey = pKeys.getOrDefault(v.getPid(), new FullKey(v.getPid(), null));
            pKeys.put(v.getPid(), pidKey);
            final FullKey key = pKeys.getOrDefault(v.getId(), new FullKey(v.getId(), null));
            key.setParentKey(pidKey);
            pKeys.put(v.getId(), key);
        });
        foo.forEach(v -> {
            // 构建当前节点全路径
            // v.setPKey(pKeys.get(v.getId()).getKey());
            // 构建当前节点服路径
            v.setPKey(pKeys.get(v.getPid()).getKey());
            log.info("{}", v);
        });
        log.info("---------------");
    }

    @Test
    void t2() {
        final Set<Foo> result = filter(foo, "B2", Foo::getId, Foo::getPKey);
        log.info("{}", result);
    }

    @Test
    void t3() {
        log.info("{}", new Date());

    }

    public <T> Set<T> filter(List<T> target, String queryStr, Function<T, String> idFc, Function<T, String> pFullKey) {
        // 查询参数映射
        this.getClass().getCanonicalName();
        Map<String, T> paramMap = new HashMap<>();
        // ID 映射
        Map<String, T> idMap = target.stream().collect(Collectors.toMap(idFc, v->v));
        // 完整key映射
        final MultiValueMap<String, T> fullKeyMap =  new LinkedMultiValueMap<>();
        for (T f : target) {
            fullKeyMap.add(pFullKey.apply(f), f);
        }

        // 结果集
        Set<T> result = new HashSet<>();
        // 过滤包含请求参数的对象
        for (T f : target) {
            if (idFc.apply(f).contains(queryStr)) {
                paramMap.put(idFc.apply(f), f);
            }
        }


        // 对查到的数据进行字父级数据查询
        for (T f : paramMap.values()) {
            // 获取到完整父Key
            final String pKey = pFullKey.apply(f);
            final StringTokenizer tokenizer = new StringTokenizer(pKey, "-");
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                result.add(idMap.get(token));
            }
            // 放入当前层级
            result.add(f);
            // 查询子级
            for (String key : fullKeyMap.keySet()) {
                if (Objects.nonNull(key) && key.startsWith(pFullKey.apply(f))) {
                    final List<T> foos = fullKeyMap.get(key);
                    result.addAll(foos);
                }
            }
        }
        return result;
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
        private String key;
        private FullKey parentKey;

        public String getKey() {
            if (Objects.isNull(parentKey) || StringUtils.isEmpty(parentKey.getKey())) {
                return key;
            }
            return parentKey.getKey() +"-" + key;
        }
    }
}
