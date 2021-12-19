package com.mdb.base.query;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌入式文档查询option
 *
 * @author twjitm
 */
public class NestedOptions {

    private NestedOptions() {

    }

    private List<Bson> aggregates = new ArrayList<>();

    public static NestedOptions builder() {

        return new NestedOptions();
    }

    public NestedOptions limit(int limit) {
        aggregates.add(Aggregates.limit(limit));
        return this;
    }

    public NestedOptions projection(Bson projection) {
        aggregates.add(Aggregates.project(projection));
        return this;
    }

    public NestedOptions skip(int skip) {
        aggregates.add(Aggregates.skip(skip));

        return this;
    }

    public NestedOptions sort(Bson sort) {
        aggregates.add(Aggregates.sort(sort));
        return this;
    }

    public Bson toAggregates() {
        return Filters.and(aggregates);
    }
}
