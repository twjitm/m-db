package com.mdb.enums;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Field {

    String value() default "";

    String name() default "";

    int order() default Integer.MAX_VALUE;
}
