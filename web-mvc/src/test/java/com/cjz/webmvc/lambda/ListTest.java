package com.cjz.webmvc.lambda;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-07 11:34
 */
@Slf4j
public class ListTest {

    @Test
    public void t1() {
        String[] array = {"1", "2", "3", "4", "5"};
        final String[] newArray = Arrays.copyOf(array, 10);
        int index = 2;
        String element = "4";
        array[index] = element;
        log.info("{}", Arrays.asList(newArray));

        System.arraycopy(newArray, index, newArray, index + 1, array.length - index);
        newArray[index] = element;
        log.info("{}", Arrays.asList(newArray));

    }
}
