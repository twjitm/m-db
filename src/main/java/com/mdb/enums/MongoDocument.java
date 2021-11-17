package com.mdb.enums;

import com.mdb.entity.NestedMongoPo;

import java.lang.annotation.*;

/**
 * 文档标记：给一个实体类标记上对应一个文档
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MongoDocument {

    /**
     * 嵌入在那个父文档
     *
     * @return 父文档实体
     */
    Class<? extends NestedMongoPo> rooter() default NestedMongoPo.class;

    /**
     * 嵌入字段名字
     *
     * @return 返回要嵌入的字段名字
     */
    String nested() default "";

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
