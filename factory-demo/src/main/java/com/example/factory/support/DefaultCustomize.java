package com.example.factory.support;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-03-01 11:46
 */
@Component
public class DefaultCustomize implements Customize{
    private boolean testEnabled = false;
    private String str = "cjz";

    @Override
    public void init(Environment environment) {
        // TODO
    }
}
