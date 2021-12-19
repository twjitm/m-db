package com.mdb.base.query;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;

/**
 * 标准化一些查询方式
 */
public class Query extends QueryBuilder {


    private Query() {
        super();
    }

    public static Query builder() {
        return new Query();
    }

    public Query and(String key, Object value) {
        this.and(key).is(value);
        return this;
    }

    public Query or(String key, Object value) {
        this.or(new BasicDBObject(key, value));
        return this;
    }

    public Query gt(String key, Object value) {
        this.and(key).greaterThan(value);
        return this;
    }

    public Query gte(String key, Object value) {
        this.and(key).greaterThanEquals(value);
        return this;
    }


    public Query lt(String key, Object value) {
        this.and(key).lessThan(value);
        return this;
    }


    public Query lte(String key, Object value) {
        this.and(key).lessThanEquals(value);
        return this;
    }


    public Query ne(String key, Object value) {
        this.and(key).notEquals(value);
        return this;
    }

    public Query in(String key, Object... value) {
        this.and(key).in(value);
        return this;
    }

    public Query notIn(String key, Object... value) {
        this.and(key).notIn(value);
        return this;
    }

    public Query mod(String key, Object... value) {
        this.and(key).mod(value);
        return this;
    }

    public Query all(String key, Object value) {
        this.and(key).all(value);
        return this;
    }

    public Query size(String key, Object value) {
        this.and(key).size(value);
        return this;
    }

    public Query exists(String key, Object value) {
        this.and(key).exists(value);
        return this;
    }

    public Query elemMarch(String key, Object value) {
        this.and(key).elemMatch(new BasicDBObject(key, value));
        return this;
    }

}
