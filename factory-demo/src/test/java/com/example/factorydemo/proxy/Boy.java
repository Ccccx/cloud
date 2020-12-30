package com.example.factorydemo.proxy;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-28 15:09
 */
public class Boy implements IPeople {
    @Override
    public String say(String name) {
        return "Boy  name is " + name;
    }
}
