package com.mdb.enums.index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 失效时间索引：本docment 具有时效性
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpireIndex {

    boolean unique() default false;

    String name() default "";

    int order() default -1;
}
