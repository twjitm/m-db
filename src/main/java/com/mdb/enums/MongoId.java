package com.mdb.enums;

import java.lang.annotation.*;

/**
 * 给某个字段为标记为一个主键：注意此主键非传统意义上的主键
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface MongoId {

    String name();
    int order() default 0;

}
