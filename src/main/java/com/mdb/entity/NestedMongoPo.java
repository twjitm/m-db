package com.mdb.entity;

import org.bson.conversions.Bson;

/**
 * 一个嵌入式文档
 */
public interface NestedMongoPo extends MongoPo {

    Class<? extends NestedMongoPo> rooterPath();

    String nestedTable();

    Bson rootFilter();
}
