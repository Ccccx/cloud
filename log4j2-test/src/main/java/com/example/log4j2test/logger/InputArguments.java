package com.example.log4j2test.logger;

import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-23 15:24
 */
@Data
public class InputArguments {

    private int number;
    private Class<?> type;
    private String value;

    public InputArguments(int number) {
        this(number, null, null);
    }

    public InputArguments(int number, Class<?> type) {
        this(number, type, null);
    }

    public InputArguments(int number, Class<?> type, String value) {
        this.number = number;
        this.type = type;
        this.value = value;
    }
}
