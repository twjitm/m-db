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

    public QueryOptions batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public QueryOptions limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryOptions modifiers(Bson modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public QueryOptions projection(Bson projection) {
        this.projection = projection;
        return this;
    }

    public QueryOptions maxTimeMS(long maxTimeMS) {
        this.maxTimeMS = maxTimeMS;
        return this;
    }

    public QueryOptions maxAwaitTimeMS(long maxAwaitTimeMS) {
        this.maxAwaitTimeMS = maxAwaitTimeMS;
        return this;
    }

    public QueryOptions skip(int skip) {
        this.skip = skip;
        return this;
    }

    public QueryOptions sort(Bson sort) {
        this.sort = sort;
        return this;
    }

    public QueryOptions cursorType(CursorType cursorType) {
        this.cursorType = cursorType;
        return this;
    }

    public QueryOptions noCursorTimeout(boolean noCursorTimeout) {
        this.noCursorTimeout = noCursorTimeout;
        return this;
    }

    public QueryOptions qplogReplay(boolean oplogReplay) {
        this.oplogReplay = oplogReplay;
        return this;
    }

    public QueryOptions partial(boolean partial) {
        this.partial = partial;
        return this;
    }

    public QueryOptions collation(Collation collation) {
        this.collation = collation;
        return this;
    }

    public QueryOptions comment(String comment) {
        this.comment = comment;
        return this;
    }

    public QueryOptions hint(Bson hint) {
        this.hint = hint;
        return this;
    }

    public QueryOptions max(Bson max) {
        this.max = max;
        return this;
    }

    public QueryOptions min(Bson min) {
        this.min = min;
        return this;
    }

    public QueryOptions maxScan(long maxScan) {
        this.maxScan = maxScan;
        return this;
    }

    public QueryOptions returnKey(boolean returnKey) {
        this.returnKey = returnKey;
        return this;
    }

    public QueryOptions showRecordId(boolean showRecordId) {
        this.showRecordId = showRecordId;
        return this;
    }

    public QueryOptions snapshot(boolean snapshot) {
        this.snapshot = snapshot;
        return this;
    }
}
