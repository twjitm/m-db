package com.mdb.base.query;

import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

public class QueryOptions {

    private int batchSize;
    private int limit;
    private Bson modifiers;
    private Bson projection;
    private long maxTimeMS;
    private long maxAwaitTimeMS;
    private int skip;
    private Bson sort;
    private CursorType cursorType = CursorType.NonTailable;
    private boolean noCursorTimeout;
    private boolean oplogReplay;
    private boolean partial;
    private Collation collation;
    private String comment;
    private Bson hint;
    private Bson max;
    private Bson min;
    private long maxScan;
    private boolean returnKey;
    private boolean showRecordId;
    private boolean snapshot;


    QueryOptions() {

    }

    public static QueryOptions builder() {

        return new QueryOptions();
    }

    public <T> void merge(FindIterable<T> finder) {
        if (this.skip > 0) {
            finder.skip(this.skip);
        }
        if (this.limit > 0) {
            finder.limit(this.limit);
        }
        if (this.modifiers != null) {
            finder.modifiers(this.modifiers);
        }
        if (this.sort != null) {
            finder.sort(this.sort);
        }
        if (this.skip > 0) {
            finder.skip(this.skip);
        }
    }

    public QueryOptions setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public QueryOptions setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryOptions setModifiers(Bson modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public QueryOptions setProjection(Bson projection) {
        this.projection = projection;
        return this;
    }

    public QueryOptions setMaxTimeMS(long maxTimeMS) {
        this.maxTimeMS = maxTimeMS;
        return this;
    }

    public QueryOptions setMaxAwaitTimeMS(long maxAwaitTimeMS) {
        this.maxAwaitTimeMS = maxAwaitTimeMS;
        return this;
    }

    public QueryOptions setSkip(int skip) {
        this.skip = skip;
        return this;
    }

    public QueryOptions setSort(Bson sort) {
        this.sort = sort;
        return this;
    }

    public QueryOptions setCursorType(CursorType cursorType) {
        this.cursorType = cursorType;
        return this;
    }

    public QueryOptions setNoCursorTimeout(boolean noCursorTimeout) {
        this.noCursorTimeout = noCursorTimeout;
        return this;
    }

    public QueryOptions setOplogReplay(boolean oplogReplay) {
        this.oplogReplay = oplogReplay;
        return this;
    }

    public QueryOptions setPartial(boolean partial) {
        this.partial = partial;
        return this;
    }

    public QueryOptions setCollation(Collation collation) {
        this.collation = collation;
        return this;
    }

    public QueryOptions setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public QueryOptions setHint(Bson hint) {
        this.hint = hint;
        return this;
    }

    public QueryOptions setMax(Bson max) {
        this.max = max;
        return this;
    }

    public QueryOptions setMin(Bson min) {
        this.min = min;
        return this;
    }

    public QueryOptions setMaxScan(long maxScan) {
        this.maxScan = maxScan;
        return this;
    }

    public QueryOptions setReturnKey(boolean returnKey) {
        this.returnKey = returnKey;
        return this;
    }

    public QueryOptions setShowRecordId(boolean showRecordId) {
        this.showRecordId = showRecordId;
        return this;
    }

    public QueryOptions setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
        return this;
    }
}
