package com.mdb.enums;

import java.lang.annotation.*;

/**
 * 给某个字段为标记为一个主键：注意此主键非传统意义上的主键
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface MongoId {

    /**
     * 名字
     * @return
     */
    String name();

    /**
     * 是否为自增字段
     * @return true 为自增，false 不自增
     */
    boolean tick() default false;

    /**
     * 排序，在json 中的位置
     * @return
     */
    int order() default 0;

}
