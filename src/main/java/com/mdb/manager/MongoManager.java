package com.mdb.manager;

import com.mdb.base.query.QueryOptions;
import com.mdb.entity.MongoPo;
import com.mdb.entity.MongoTask;
import com.mdb.entity.PrimaryKey;
import com.mdb.entity.TickId;
import com.mdb.enums.MongoDocument;
import com.mdb.exception.MException;
import com.mdb.thread.ThreadManager;
import com.mdb.utils.ZClassUtils;
import com.mdb.utils.ZCollectionUtil;
import com.mdb.utils.ZStringUtils;
import com.mongodb.*;

import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;


public class MongoManager {

    private MongoSyncManager mongoSyncManager;
    private MongoCollectionManager mongoCollectionManager;

    /**
     * 是否开启异步操作：针对于更新，删除，插入操作开启异步过程
     */
    private boolean async;

    private final static MongoManager instance = new MongoManager();

    public static MongoManager getInstance() {
        return instance;
    }


    public MongoManager() {
        this.async = false;
        String DEFAULT_URL = "127.0.0.1:27017";
        initClient(DEFAULT_URL);

    }

    public MongoManager(String url) {
        this.async = false;
        initClient(url);
    }

    public MongoManager(String url, boolean async) {
        this.async = async;
        initClient(url);
        initAsync();
    }


    public void setAsync(boolean async) {
        this.async = async;
        initAsync();
    }

    private void initClient(String url) {
        if (mongoCollectionManager != null) {
            return;
        }
        mongoCollectionManager = new MongoCollectionManager(url);
    }

    public void shutdown() {
        if (mongoSyncManager == null) {
            return;
        }
        mongoSyncManager.shutdown();
    }


    /**
     * 初始化异步执行环境
     */
    private void initAsync() {
        if (mongoSyncManager != null) {
            return;
        }
        if (!this.async) {
            return;
        }
        mongoSyncManager = new MongoSyncManager(true, mongoCollectionManager);
    }


    public <T extends MongoPo> void createIndex(Class<T> clazz) {

        try {
            MongoPo v = clazz.newInstance();
            MongoCollection<Document> db = mongoCollectionManager.getCollection(clazz);
            List<IndexModel> list = v.getIndex();
            if (ZCollectionUtil.isEmpty(list)) {
                return;
            }
            db.createIndexes(list);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public <T extends MongoPo> List<IndexModel> getIndexList(Class<T> clazz) {
        return null;
    }

    public <T extends MongoPo> boolean add(T t) throws MException {
        if (t == null) {
            return false;
        }
        Class<? extends MongoPo> clazz = t.getClass();
        MongoCollection<Document> db = mongoCollectionManager.getCollection(t);
        Document document = t.document();
        String tickName = t.tick();
        if (!ZStringUtils.isEmpty(tickName)) {
            long id = this.nextId(clazz);
            document.put(tickName, this.nextId(clazz));
            try {
                ZClassUtils.setField(t, tickName, id);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (async) {
            return mongoSyncManager.put(MongoTask.builder(t.database(), t.collection(), new InsertOneModel<>(document)));
        }
        db.insertOne(document);
        return true;
    }


    public <T extends MongoPo> boolean addMany(List<T> list) throws MException {
        BulkWriteOptions op = new BulkWriteOptions().ordered(true);
        List<InsertOneModel<Document>> ins = new ArrayList<>();
        T t = list.get(0);
        Class<? extends MongoPo> clazz = t.getClass();
        MongoDocument mongoDocument = clazz.getAnnotation(MongoDocument.class);
        for (T item : list) {
            Document document = item.document();
            String tickName = t.tick();
            if (!ZStringUtils.isEmpty(tickName)) {
                long id = this.nextId(clazz);
                document.put(tickName, id);
                try {
                    ZClassUtils.setField(t, tickName, id);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (async) {
                mongoSyncManager.put(MongoTask.builder(item.database(), item.collection(), new InsertOneModel<>(document)));
            } else {
                ins.add(new InsertOneModel<>(document));
            }
        }
        if (!ZCollectionUtil.isEmpty(ins) && !async) {
            mongoCollectionManager.getCollection(mongoDocument).bulkWrite(ins, op);
        }
        return true;
    }

    public <T extends MongoPo> boolean update(T t) throws MException {
        if (t == null) {
            return false;
        }
        Document modify = t.modify();
        if (ZCollectionUtil.isEmpty(modify)) {
            return false;
        }
        Document up = new Document();
        up.put("$set", modify);
        if (async) {
            return mongoSyncManager.put(MongoTask.builder(t.database(), t.collection(), new UpdateOneModel<>(t.filters(), up)));
        }
        UpdateResult result = mongoCollectionManager.getCollection(t).updateOne(t.filters(), up);
        return result.wasAcknowledged();
    }

    public <T extends MongoPo> boolean updateMany(List<T> list) {

        if (ZCollectionUtil.isEmpty(list)) {
            return false;
        }
        List<UpdateOneModel<Document>> ups = new ArrayList<>();
        list.forEach(item -> {
            try {
                Document modify = item.modify();
                if (!ZCollectionUtil.isEmpty(modify)) {
                    if (async) {
                        mongoSyncManager.put(MongoTask.builder(item.database(), item.collection(), new UpdateOneModel<>(item.filters(),
                                new Document("$set", modify))));
                    } else {
                        ups.add(new UpdateOneModel<>(item.filters(), new Document("$set", modify)));
                    }
                }
            } catch (MException e) {
                e.printStackTrace();
            }
        });
        if (ZCollectionUtil.isEmpty(ups)) {
            return false;
        }
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(list.get(0));
        BulkWriteResult result = collection.bulkWrite(ups);
        return result.wasAcknowledged();
    }

    public <T extends MongoPo> boolean delete(T t) {
        if (t == null) {
            return false;
        }
        if (async) {
            return mongoSyncManager.put(MongoTask.builder(t.database(), t.collection(), new DeleteOneModel<>(t.filters())));
        }
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(t);
        DeleteResult result = collection.deleteOne(t.filters());
        return result.getDeletedCount() == 1;
    }

    public <T extends MongoPo> boolean delete(Class<T> clazz, PrimaryKey... keys) {
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(clazz);
        List<Bson> filters = new ArrayList<>();
        for (PrimaryKey key : keys) {
            Bson filter = Filters.eq(key.getName(), key.getValue());
            filters.add(filter);
        }
        Bson filter = Filters.and(filters);
        if (async) {
            return mongoSyncManager.put(MongoTask.builder(clazz, new DeleteOneModel<>(filter)));
        }
        DeleteResult result = collection.deleteOne(filter);
        return result.getDeletedCount() == keys.length;
    }

    public <T extends MongoPo> T get(Class<T> clazz, PrimaryKey... keys) throws MException {

        if (ZCollectionUtil.isEmpty(keys)) {
            throw new MException("[error][mdb][conduction is empty]");
        }
        List<Bson> filters = new ArrayList<>();
        for (PrimaryKey key : keys) {
            Bson filter = Filters.eq(key.getName(), key.getValue());
            filters.add(filter);
        }
        Bson filter = Filters.and(filters);
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        T t = mongoCollectionManager.getCollection(document).find(filter, clazz).first();
        if (t != null) {
            t.document();
        }
        return t;
    }


    public <T extends MongoPo> List<T> getAll(Class<T> clazz, PrimaryKey... keys) throws MException {
        List<Bson> filters = new ArrayList<>();
        if (!ZCollectionUtil.isEmpty(keys)) {
            throw new MException("[error][mdb][conduction is empty]");
        }
        for (PrimaryKey key : keys) {
            Bson filter = Filters.eq(key.getName(), key.getValue());
            filters.add(filter);
        }
        Bson filter = Filters.and(filters);
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        MongoCollection<Document> db = mongoCollectionManager.getCollection(document);

        MongoCursor<T> items = db.find(filter, clazz).iterator();
        List<T> result = new ArrayList<>();
        while (items.hasNext()) {
            T t = items.next();
            t.document();
            result.add(t);
        }
        return result;
    }

    public <T extends MongoPo> T findOne(Class<T> clazz, QueryBuilder query) throws MException {
        if (query == null) {
            throw new MException("[error][mdb][query is empty]");
        }
        BasicDBObject ob = (BasicDBObject) query.get();
        if (ob.size() == 0) {
            throw new MException("[error][mdb][conduction is empty]");
        }
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(clazz);
        FindIterable<T> result = collection.find(ob, clazz);
        T t = result.first();
        if (t != null) {
            t.document();
        }
        return t;
    }

    public <T extends MongoPo> List<T> findAll(Class<T> clazz, QueryBuilder query, QueryOptions options) throws MException {
        if (query == null) {
            throw new MException("[error][mdb][query is empty]");
        }
        BasicDBObject ob = (BasicDBObject) query.get();
        if (ob.size() == 0) {
            throw new MException("[error][mdb][conduction is empty]");
        }
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(clazz);
        FindIterable<T> result = collection.find(ob, clazz);
        if (options != null) {
            options.merge(result);
        }
        MongoCursor<T> it = result.iterator();
        List<T> list = new ArrayList<>();
        while (it.hasNext()) {
            T t = it.next();
            t.document();
            list.add(t);
        }
        return list;
    }

    public <T extends MongoPo> T findOne(Aggregates aggregates) throws MException {
        if (aggregates == null) {
            throw new MException("[error][mdb][query is empty]");
        }
        return null;
    }

    public <T extends MongoPo> T findOne(Class<T> clazz, Filters filters, QueryOptions options) {
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(clazz);
        return null;
    }

    private <T extends MongoPo> List<T> find() {
        return null;
    }


    public <T extends MongoPo> long nextId(Class<T> tick) {
        String key = tick.getSimpleName();
        Document document = new Document();
        document.put("key", key);
        Document up = new Document();
        up.put("$inc", new Document("value", 1));
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(TickId.class);
        Document result = collection.findOneAndUpdate(document, up);
        if (result == null) {
            document.put("value", 1);
            collection.insertOne(document);
            return 1;
        }
        return result.getInteger("value");
    }


    public <T extends MongoPo> long count(Class<T> clazz, QueryBuilder query) {
        BasicDBObject filter = (BasicDBObject) query.get();
        return mongoCollectionManager.getCollection(clazz).countDocuments(filter);
    }

}
