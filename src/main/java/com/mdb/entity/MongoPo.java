package com.mdb.entity;

import com.mdb.exception.MException;
import com.mongodb.client.model.IndexModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

/**
 * 一个简单mongodb 实体类
 */
public interface MongoPo {

    /**
     * 存储文档
     *
     * @return 返回mongo存储文档
     */
    Document document();

    /**
     * 文档索引
     *
     * @return 返回文档索引，包括简单索引和联合索引
     */
    public List<IndexModel> getIndex();

    /**
     * 元数据
     *
     * @return 元数据集合
     */
    Map<String, ?> data();

    /**
     * 改变字段
     *
     * @return 返回发生改变后的字段
     * @throws MException 抛出不可更改字段异常
     */
    Document modify() throws MException;

    /**
     * 条件过滤器
     *
     * @return 返回条件过滤器
     */
    Bson filters();

    /**
     * 文档自增字段
     *
     * @return 返回自增字段名称
     * @throws MException 不可重复异常
     */
    String tick() throws MException;

    /**
     * json 数据
     *
     * @return bson 数据变为json
     */
    String toJsonString();

    /**
     * db
     *
     * @return 返回db的名称
     */
    String database();

    /**
     * 表名称
     *
     * @return mongodb 中的表名称
     */
    String collection();


}
