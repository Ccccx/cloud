package com.cjz.webmvc.chinese;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.junit.jupiter.api.Test;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-20 10:57
 */
@Slf4j
  class ChineseTest {

    @Test
    void t1() {
        log.info("{}", getPinYinHeadChar("程金周"));
        log.info("{}", getPinYinHeadChar("程金周", false));
        log.info("{}", getPinYinHeadChar("饕餮", false));
        log.info("{}", getPinYinHeadChar("系统管理员"));
        log.info("{}", getPinYinHeadChar("系统管理员", false));
        log.info("{}", getPinYin('空'));
    }
    public static String getPinYinHeadChar(String str) {
        return getPinYinHeadChar(str, true);
    }
    /**
     * 传入中文获取首字母 （小写）
     * 如：小超人 -> xcr
     *
     * @param str 需要转化的中文字符串
     * @param abbr 是否缩写
     * @return 缩写
     */
    public static String getPinYinHeadChar(String str, boolean abbr) {
        StringBuilder convert = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                if (abbr) {
                    convert.append(pinyinArray[0].charAt(0));
                } else {
                    convert.append(pinyinArray[0]);
                }
            } else {
                convert.append(word);
            }
        }
        return convert.toString();
    }

    /**
     * 获取中文字的拼音（多音字，拼音后的数字代表第几声）
     * 如：空 -> kong1 kong4
     *
     * @param word 汉子
     * @return 拼音
     */
    public static String[] getPinYin(char word) {
        return PinyinHelper.toHanyuPinyinStringArray(word);
    }

}
