package com.mdb.enums;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Field {

    String name() default "";

    boolean readOnly() default false;

    int order() default Integer.MAX_VALUE;
}
