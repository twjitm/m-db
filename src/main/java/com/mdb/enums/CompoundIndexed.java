package com.mdb.enums;

public @interface CompoundIndexed {
    String name() default "";

    Indexed[] value();
}
