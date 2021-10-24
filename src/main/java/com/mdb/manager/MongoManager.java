package com.mdb.manager;

import com.mdb.base.query.Query;
import com.mdb.config.MongoConfig;
import com.mdb.entity.MongoPo;
import com.mdb.entity.MongoPrimaryKey;
import com.mdb.enums.MongoDocument;
import com.mongodb.MongoClient;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class MongoManager<T extends MongoPo> {

    private MongoClient mongoClient;
    private MongoConfig config;

    public final void load(MongoConfig config) {
        if (mongoClient == null) {
            mongoClient = new MongoClient(config.getUrl());
        }
    }

    public final void load(MongoClient client) {

    }

    public boolean add(T t) {
        if (t == null) {
            return false;
        }
        Class<? extends MongoPo> clazz = t.getClass();
        MongoDocument mongoDocument = clazz.getAnnotation(MongoDocument.class);
        mongoClient.getDatabase(mongoDocument.database()).
                getCollection(mongoDocument.collection()).insertOne(t.document());
        mongoClient.close();
        return false;
    }

    public boolean addMany(List<T> list) {
        BulkWriteOptions op = new BulkWriteOptions().ordered(true);
        List<InsertOneModel<Document>> ins = new ArrayList<>();
        T t = list.get(0);
        Class<? extends MongoPo> clazz = t.getClass();
        MongoDocument mongoDocument = clazz.getAnnotation(MongoDocument.class);
        list.forEach(item -> ins.add(new InsertOneModel<>(item.document())));
        mongoClient.getDatabase(mongoDocument.database()).getCollection(mongoDocument.collection())
                .bulkWrite(ins, op);
        mongoClient.close();
        return true;
    }

    public boolean update(T t) {

        return false;
    }

    public boolean updateMany(List<T> list) {

        return true;
    }

    public T get(Class<T> clazz,)

    public T findOne(Query query) {
        return null;
    }

    public T findOne(Aggregates aggregates){
        return  null;
    }

    public T get(Class<T> clazz, MongoPrimaryKey... key) {
        return null;
    }


}
