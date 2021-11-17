package com.mdb.entity;

import org.bson.conversions.Bson;

/**
 * 一个嵌入式文档
 */
public interface NestedMongoPo extends MongoPo {

    public Class<NestedMongoPo> rooter();

    public String nestedTable();

    public Bson rootFilter();
}
