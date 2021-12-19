package com.mdb.enums.index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 失效时间索引：本document 具有时效性
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpireIndex {

    boolean unique() default false;

    String name() default "";

    /**
     *
     * @return 过期失效时间
     */
    long ttl() default  -1;

    int order() default -1;
}
