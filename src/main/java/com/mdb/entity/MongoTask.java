package com.mdb.entity;

import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.WriteModel;

/**
 *
 * @param <T>
 */
public class MongoTask<T> {
    private WriteModel<T> model;
    private BulkWriteOptions options;


    public void setModel(WriteModel<T> model) {
        this.model = model;
    }

    public BulkWriteOptions getOptions() {
        return options;
    }

    public WriteModel<T> getModel() {
        return model;
    }

    public void setOptions(BulkWriteOptions options) {
        this.options = options;
    }
}
