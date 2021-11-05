package com.mdb.base.query;

import com.mongodb.QueryBuilder;

public class Query extends QueryBuilder {


    public Query() {
        super();
    }

    public static Query builder() {
        return new Query();
    }

    public Query add(String key, Object value) {
        this.and(key).is(value);
        return this;
    }
}
