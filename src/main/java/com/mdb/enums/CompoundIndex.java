package com.mdb.enums;

public @interface CompoundIndex {
    String name() default "";
    String def() default "";
}
