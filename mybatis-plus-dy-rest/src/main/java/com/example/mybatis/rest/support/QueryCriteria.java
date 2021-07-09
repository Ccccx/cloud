package com.example.mybatis.rest.support;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-06 13:36
 */
@Data
@AllArgsConstructor
public class QueryCriteria {
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
