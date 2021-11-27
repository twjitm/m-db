package com.mdb.base.query;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌入式文档查询option
 * @author twjitm
 */
public class QueryNestedOptions {

    private List<Bson> aggregates = new ArrayList<>();

    public static QueryNestedOptions builder() {

        return new QueryNestedOptions();
    }
    public QueryNestedOptions limit(int limit) {
        aggregates.add(Aggregates.limit(limit));
        return this;
    }
    public QueryNestedOptions projection(Bson projection) {
        aggregates.add(Aggregates.project(projection));
        return this;
    }
    public QueryNestedOptions skip(int skip) {
        aggregates.add(Aggregates.skip(skip));

        return this;
    }

    public QueryNestedOptions sort(Bson sort) {
        aggregates.add(Aggregates.sort(sort));
        return this;
    }
    public Bson toAggregates() {
        return Filters.and(aggregates);
    }
}
