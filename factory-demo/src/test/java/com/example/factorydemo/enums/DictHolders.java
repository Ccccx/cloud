package com.example.factorydemo.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 17:47
 */
public class DictHolders {
    public static final Map<String, DictHolder> MAPPER_HOLDERS = new ConcurrentHashMap<>();

    private DictHolders() {
    }

    public static DictHolder getDictHolder(String dictName) {
        return MAPPER_HOLDERS.get(dictName);
    }
}
