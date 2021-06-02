package com.example.factorydemo.enums;

import java.lang.annotation.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 16:25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Inherited
public @interface DictMapper {
    /**
     * @return 字典名称
     */
    String dictName() default "default";

    /**
     * @return 字典描述
     */
    String dictDesc() default "default";

    String  key() default   "key";

    String  val() default "val";
}
