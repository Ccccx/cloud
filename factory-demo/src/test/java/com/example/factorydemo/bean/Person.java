package com.example.factorydemo.bean;

import com.example.factorydemo.proxy.IPeople;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 11:32
 */
@Data
@AllArgsConstructor
public class Person implements IPeople {
    private String name;
    private int age;

    @Override
    public String say(String name) {
        return this.name + ":" + name;
    }
}
