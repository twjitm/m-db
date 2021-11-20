package com.mdb.enums;

import com.mdb.entity.AbstractNestedMongoPo;
import com.mdb.entity.MongoPo;
import com.mdb.entity.NestedMongoPo;

import java.lang.annotation.*;

/**
 * 文档标记：给一个实体类标记上对应一个文档
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MongoDocument {

    Class<? extends NestedMongoPo> rooter() default AbstractNestedMongoPo.class;

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
    String table();

    /**
     * 内嵌文档名字
     *
     * @return 返回一个内嵌文档中的列名
     */
    String nested() default "";

}
