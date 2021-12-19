package com.mdb.enums.index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 普通索引：为某个字段建立索引
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {

    boolean unique() default false;

    String name();

    int order() default -1;

    /**
     * 过期时间
     *
     * @return 返回一个过期时间，当value 小于等于0时，无效
     */
    long expireAfterSeconds() default 0;
}
