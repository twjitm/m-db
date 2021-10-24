package com.mdb.enums;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MongoDocument {

    String database() default "";

    String collection() default "";

}
