package com.mdb.manager;

import com.mdb.base.query.Query;
import com.mdb.entity.MongoPo;
import com.mdb.entity.MongoPrimaryKey;
import com.mdb.enums.MongoDocument;
import com.mdb.utils.ZCollectionUtil;
import com.mongodb.MongoClient;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MongoManager<T extends MongoPo> {

    private MongoClient mongoClient = null;
    private final Map<String, MongoCollection<Document>> collections = new HashMap<>();
    @SuppressWarnings("rawtypes")
    private final static MongoManager instance = new MongoManager<>();
    @SuppressWarnings("rawtypes")
    public static MongoManager getInstance() {
        return instance;
    }

    public void load(String url) {
        if (mongoClient == null) {
            mongoClient = new MongoClient(url);
        }
    }

    public void scanIndex(Class<T> clazz) {

        try {
            T v = clazz.newInstance();
            MongoDocument doc = clazz.getAnnotation(MongoDocument.class);
            MongoCollection<Document> db = this.getCollection(doc);
            List<IndexModel> list = v.getIndex();
            if (ZCollectionUtil.isEmpty(list)) {
                return;
            }
            db.createIndexes(list);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public final void load(MongoClient client) {


    }

    public void init(String cmd) {

    }

    public void close() {
        mongoClient.close();
    }

    private MongoCollection<Document> getCollection(String database, String collection) {
        String key = database + ":" + collection;
        MongoCollection<Document> db = collections.get(key);
        if (db == null) {
            db = mongoClient.getDatabase(database).getCollection(collection);
            collections.put(key, db);
        }
        return db;
    }

    private MongoCollection<Document> getCollection(MongoDocument document) {
        return this.getCollection(document.database(), document.collection());
    }

    public boolean add(T t) {
        if (t == null) {
            return false;
        }
        Class<? extends MongoPo> clazz = t.getClass();
        MongoDocument mongoDocument = clazz.getAnnotation(MongoDocument.class);
        MongoCollection<Document> db = this.getCollection(mongoDocument);
        db.insertOne(t.document());
        return false;
    }


    public boolean addMany(List<T> list) {
        BulkWriteOptions op = new BulkWriteOptions().ordered(true);
        List<InsertOneModel<Document>> ins = new ArrayList<>();
        T t = list.get(0);
        Class<? extends MongoPo> clazz = t.getClass();
        MongoDocument mongoDocument = clazz.getAnnotation(MongoDocument.class);
        list.forEach(item -> ins.add(new InsertOneModel<>(item.document())));
        this.getCollection(mongoDocument).bulkWrite(ins, op);
        return true;
    }

    public boolean update(T t) {

        return false;
    }

    public boolean updateMany(List<T> list) {

        return true;
    }


    public T get(Class<T> clazz, MongoPrimaryKey... keys) {

        List<Bson> filters = new ArrayList<>();
        for (MongoPrimaryKey key : keys) {
            Bson filter = Filters.eq(key.getName(), key.getValue());
            filters.add(filter);
        }
        Bson filter = Filters.and(filters);
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        return this.getCollection(document).find(filter, clazz).first();
    }


    public List<T> getAll(Class<T> clazz, MongoPrimaryKey... keys) {
        List<Bson> filters = new ArrayList<>();
        for (MongoPrimaryKey key : keys) {
            Bson filter = Filters.eq(key.getName(), key.getValue());
            filters.add(filter);
        }
        Bson filter = Filters.and(filters);
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        MongoCollection<Document> db = this.getCollection(document);

        MongoCursor<T> items = db.find(filter, clazz).iterator();
        List<T> result = new ArrayList<>();
        while (items.hasNext()) {
            T t = items.next();
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }

    public T findOne(Query query) {
        return null;
    }

    public T findOne(Aggregates aggregates) {
        return null;
    }


}
