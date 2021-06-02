package com.example.factorydemo.enums;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 16:26
 */
@Data
public class DictHolder {
    private DictInfo dictInfo;
    private List<DictOption> list;

    public DictHolder(String dictName, String dictDesc) {
        dictInfo = new DictInfo(dictName, dictDesc);
        this.list = new ArrayList<>();
    }

    public DictHolder addDictOption(DictOption dictOption) {
        list.add(dictOption);
        return this;
    }

    public String getDictName() {
        return dictInfo.getDictName();
    }

    public String getDictDesc() {
        return dictInfo.getDictValue();
    }

}
