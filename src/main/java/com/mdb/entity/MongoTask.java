package com.mdb.entity;

import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.WriteModel;

/**
 * @param <T>
 */
public class MongoTask<T> {
    private WriteModel<T> model;
    private BulkWriteOptions options;
    private String key;


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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static <T> MongoTask<T> builder(String database, String collection, WriteModel<T> model) {
        MongoTask<T> task = new MongoTask<T>();
        task.setKey(database + ":" + collection);
        task.setModel(model);
        return task;

    }
}
