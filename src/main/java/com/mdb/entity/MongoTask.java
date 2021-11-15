package com.mdb.entity;

import com.mdb.enums.MongoDocument;
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

    public static <T> MongoTask<T> builder(MongoDocument document, WriteModel<T> model) {
        MongoTask<T> task = new MongoTask<T>();
        task.setKey(document.database() + ":" + document.collection());
        task.setModel(model);
        return task;
    }

    public static <T, E extends MongoPo> MongoTask<T> builder(Class<E> clazz, WriteModel<T> model) {
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        MongoTask<T> task = new MongoTask<T>();
        task.setKey(document.database() + ":" + document.collection());
        task.setModel(model);
    }
}
