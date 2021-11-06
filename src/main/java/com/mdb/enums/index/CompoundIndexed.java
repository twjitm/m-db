package com.mdb.enums.index;

import java.lang.annotation.*;

/**
 * 联合索引：多个字段组合索引构成
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CompoundIndexed {

    Indexed[] value();

    int order() default -1;

    boolean unique() default true;
}
