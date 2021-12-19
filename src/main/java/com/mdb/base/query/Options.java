package com.mdb.base.query;

import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

public class Options {

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


    Options() {

    }

    public static Options builder() {

        return new Options();
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
        //todo
    }

    public Options batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public Options limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Options modifiers(Bson modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public Options projection(Bson projection) {
        this.projection = projection;
        return this;
    }

    public Options maxTimeMS(long maxTimeMS) {
        this.maxTimeMS = maxTimeMS;
        return this;
    }

    public Options maxAwaitTimeMS(long maxAwaitTimeMS) {
        this.maxAwaitTimeMS = maxAwaitTimeMS;
        return this;
    }

    public Options skip(int skip) {
        this.skip = skip;
        return this;
    }

    public Options sort(Bson sort) {
        this.sort = sort;
        return this;
    }

    public Options cursorType(CursorType cursorType) {
        this.cursorType = cursorType;
        return this;
    }

    public Options noCursorTimeout(boolean noCursorTimeout) {
        this.noCursorTimeout = noCursorTimeout;
        return this;
    }

    public Options qplogReplay(boolean oplogReplay) {
        this.oplogReplay = oplogReplay;
        return this;
    }

    public Options partial(boolean partial) {
        this.partial = partial;
        return this;
    }

    public Options collation(Collation collation) {
        this.collation = collation;
        return this;
    }

    public Options comment(String comment) {
        this.comment = comment;
        return this;
    }

    public Options hint(Bson hint) {
        this.hint = hint;
        return this;
    }

    public Options max(Bson max) {
        this.max = max;
        return this;
    }

    public Options min(Bson min) {
        this.min = min;
        return this;
    }

    public Options maxScan(long maxScan) {
        this.maxScan = maxScan;
        return this;
    }

    public Options returnKey(boolean returnKey) {
        this.returnKey = returnKey;
        return this;
    }

    public Options showRecordId(boolean showRecordId) {
        this.showRecordId = showRecordId;
        return this;
    }

    public Options snapshot(boolean snapshot) {
        this.snapshot = snapshot;
        return this;
    }
}
