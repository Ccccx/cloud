package com.example.factory.support;

import lombok.Data;
import org.springframework.core.env.Environment;


/**
 * 应用配置信息
 * @author chengjz
 * @version 1.0
 * @since 2021-03-01 11:33
 */
public interface Customize {


    /**
     * 初始化一些引用对象
     *
     * @param environment
     * @author ChenLili
     * @date 2021/2/6 19:06
     */
      void init(Environment environment);
}
