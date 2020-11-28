package com.cjz.webmvc.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cglib.core.internal.Function;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * 更高效的树形数据构建器
 *
 * @author chengjz
 * @version 1.0
 * @since 2019-11-07 9:57
 */
@Slf4j
public class TreeFactory {


    /**
     * 构建树形数据，此方法不需要实现接口。
     *
     * @param target  元数据列表
     * @param idFc    主键标识字段
     * @param pidFc   父标识字段
     * @param childFc 子节点保存位置
     * @param <T>     原始对象
     * @return 树
     */
    public static <T> List<T> build(List<T> target, Function<T, String> idFc, Function<T, String> pidFc, BiConsumer<T, List<T>> childFc) {
        long start = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(target)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(target.size() / 2);
        Map<Object, List<T>> idMap = Maps.newHashMapWithExpectedSize(target.size());
        Set<Object> parent = new HashSet<>();

        target.forEach(obj -> {
            String pid = pidFc.apply(obj);
            if (idMap.containsKey(pid)) {
                List<T> list = idMap.get(pid);
                list.add(obj);
            } else {
                List<T> list = new ArrayList<>();
                list.add(obj);
                idMap.put(pid, list);
                parent.add(pid);
            }
            String id = idFc.apply(obj);
            if (idMap.containsKey(id)) {
                childFc.accept(obj, idMap.get(id));
                parent.remove(id);
            } else {
                List<T> list = new ArrayList<>();
                childFc.accept(obj, list);
                idMap.put(id, list);
            }
        });
        idMap.values().removeIf(List::isEmpty);
        parent.forEach(item -> result.addAll(idMap.get(item)));
        log.info("build tree cast: {}ms", System.currentTimeMillis() - start);
        return result;
    }

}
