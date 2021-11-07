package com.mdb.enums;

import java.lang.annotation.*;

/**
 * 文档标记：给一个实体类标记上对应一个文档
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MongoDocument {

    /**
     * 获取数据库名字
     *
     * @return 返回一个数据库的名字
     */
    String database() default "mdb";

    /**
     * 文档名字
     *
     * @return 返回数据库中一个简单的文档的文档名字
     */
    String collection();

}
