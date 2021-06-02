package com.example.factorydemo.enums;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 17:45
 */
public class TestEnumDictBeanFactory implements EnumDictFactory{
    @Override
    public DictHolder getDictHolder() {
        final DictHolder dictHolder = new DictHolder("test2", "测试2");
        dictHolder.addDictOption(new DictOption("aaa", "aaa"));
        return dictHolder;
    }
}
