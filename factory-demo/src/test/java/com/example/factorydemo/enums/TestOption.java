package com.example.factorydemo.enums;

import java.io.Serializable;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 16:25
 */
@DictMapper(dictName = "test", dictDesc = "测试")
public enum TestOption {
    T1("t1", "t1"),
    T2("t2", "t2");
    private String key;
    private String val;

    TestOption(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public String getVal() {
        return val;
    }
}
