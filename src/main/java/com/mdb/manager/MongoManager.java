package com.mdb.manager;

import com.mdb.base.query.NestedOptions;
import com.mdb.base.query.Options;
import com.mdb.entity.*;
import com.mdb.enums.MongoDocument;
import com.mdb.exception.MException;
import com.mdb.helper.MongoHelper;
import com.mdb.utils.ZClassUtils;
import com.mdb.utils.ZCollectionUtil;
import com.mdb.utils.ZJsonUtils;
import com.mdb.utils.ZStringUtils;
import com.mongodb.*;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
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

    public static MongoManager getInstance() {
        return Mongo.MONGO.getInstance();
    }

    enum Mongo {
        MONGO;

        Mongo() {
            manager = new MongoManager();
        }

        private final MongoManager manager;

        public MongoManager getInstance() {
            return manager;
        }
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
            List<IndexModel> list = v.index();
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

        MongoCollection<Document> db = mongoCollectionManager.getCollection(t);

        String tickName = t.tick();
        if (!ZStringUtils.isEmpty(tickName)) {
            Class<? extends MongoPo> clazz = t.getClass();
            long id = this.nextId(clazz);
            ZClassUtils.setField(t, tickName, id);
        }
        if (t instanceof AbstractNestedMongoPo) {
            return _fetch((AbstractNestedMongoPo) t);
        }
        Document saveDocument = t.saveDocument();
        if (async) {
            return mongoSyncManager.put(MongoTask.builder(t.database(), t.table(), new InsertOneModel<>(saveDocument)));
        }
        db.insertOne(saveDocument);
        return true;
    }

    public <T extends MongoPo> boolean addMany(List<T> list) throws MException {
        BulkWriteOptions op = new BulkWriteOptions().ordered(true);
        List<InsertOneModel<Document>> ins = new ArrayList<>();
        T t = list.get(0);
        Class<? extends MongoPo> clazz = t.getClass();
        for (T item : list) {
            String tickName = t.tick();
            if (!ZStringUtils.isEmpty(tickName)) {
                long id = this.nextId(clazz);
                ZClassUtils.setField(item, tickName, id);
            }
            Document document = item.saveDocument();
            if (async) {
                mongoSyncManager.put(MongoTask.builder(item.database(), item.table(), new InsertOneModel<>(document)));
            } else {
                ins.add(new InsertOneModel<>(document));
            }
        }
        if (!ZCollectionUtil.isEmpty(ins) && !async) {
            mongoCollectionManager.getCollection(clazz).bulkWrite(ins, op);
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
            return mongoSyncManager.put(MongoTask.builder(t.database(), t.table(), new UpdateOneModel<>(t.filters(), up)));
        }
        UpdateResult result = mongoCollectionManager.getCollection(t).updateOne(t.filters(), up);
        return result.wasAcknowledged();
    }

    public <T extends AbstractNestedMongoPo> boolean update(T t) throws MException {
        if (t == null) {
            return false;
        }
        return this._fetch(t);
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
                        mongoSyncManager.put(MongoTask.builder(item.database(), item.table(), new UpdateOneModel<>(item.filters(),
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
            return mongoSyncManager.put(MongoTask.builder(t.database(), t.table(), new DeleteOneModel<>(t.filters())));
        }
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(t);
        DeleteResult result = collection.deleteOne(t.filters());
        return result.getDeletedCount() == 1;
    }

    public <T extends MongoPo> boolean delete(Class<T> clazz, PrimaryKey... keys) {
        MongoCollection<Document> collection = mongoCollectionManager.getCollection(clazz);
        Bson filter = MongoHelper.parseFilters(keys);
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
        if (MongoHelper.isNested(clazz)) {
            Bson[] filters = MongoHelper.adaptPrimaryKey(clazz, keys);
            Bson root = filters[0];
            BasicDBObject nested = (BasicDBObject) filters[1];
            List<String> nestKeys = MongoHelper.getNestedKey(clazz);
            if (nested != null && nestKeys.size() != nested.size()) {
                throw new MException("[error][mdb][nested path filter not match]");
            }
            MongoIterable<Document> result = this.findNested(clazz, root, nested, null, Aggregates.limit(1));
            return parseOneNestedResult(clazz, result);
        }
        Bson filter = MongoHelper.parseFilters(keys);
        FindIterable<T> findIterable = mongoCollectionManager.getCollection(clazz).find(filter, clazz);
        return parseOneResult(findIterable);
    }

    public <T extends MongoPo> List<T> getAll(Class<T> clazz, PrimaryKey... keys) throws MException {

        if (ZCollectionUtil.isEmpty(keys)) {
            throw new MException("[error][mdb][conduction is empty]");
        }
        if (MongoHelper.isNested(clazz)) {
            Bson[] filters = MongoHelper.adaptPrimaryKey(clazz, keys);
            MongoIterable<Document> result = this.findNested(clazz, filters[0], filters[1], null, null);
            return parseAllNestedResult(clazz, result);
        }
        List<Bson> filters = new ArrayList<>();
        for (PrimaryKey key : keys) {
            Bson filter = Filters.eq(key.getName(), key.getValue());
            filters.add(filter);
        }
        Bson filter = Filters.and(filters);
        MongoCollection<Document> db = mongoCollectionManager.getCollection(clazz);
        FindIterable<T> findIterable = db.find(filter, clazz);
        return parseAllResult(findIterable);
    }

    public <T extends AbstractMongoPo> T findOne(Class<T> clazz, QueryBuilder query) throws MException {
        FindIterable<T> result = this.findSimple(clazz, query, Options.builder().limit(1));
        return parseOneResult(result);
    }

    public <T extends AbstractMongoPo> List<T> findAll(Class<T> clazz, QueryBuilder query, Options options) throws MException {
        FindIterable<T> result = this.findSimple(clazz, query, options);
        return parseAllResult(result);
    }

    public <T extends MongoPo> long nextId(Class<T> tick) {
        MongoDocument document = tick.getAnnotation(MongoDocument.class);
        String name = ZStringUtils.isEmpty(document.nested()) ? document.table() : document.nested();
        return _nextId(name);
    }

    public <T extends MongoPo> long nextId(String collection) {
        return this._nextId(collection);
    }


    public <T extends AbstractMongoPo> long count(Class<T> clazz, QueryBuilder query) {
        BasicDBObject filter = (BasicDBObject) query.get();
        return mongoCollectionManager.getCollection(clazz).countDocuments(filter);
    }

    public <T extends AbstractNestedMongoPo> long count(Class<T> clazz, QueryBuilder rootPathFilter, QueryBuilder nestedPathFilter, QueryBuilder nestedFilter) throws MException {
        if (rootPathFilter == null) {
            throw new MException("[error][mdb][query is empty]");
        }
        Bson nested = nestedFilter == null ? null : (Bson) nestedFilter.get();
        BasicDBObject bson = (BasicDBObject) rootPathFilter.get();
        Bson wrapperNested = nestedPathFilter == null ? null : (BasicDBObject) nestedPathFilter.get();
        MongoIterable<Document> result = findNested(clazz, bson, wrapperNested, nested, Aggregates.count());
        Document document = result.first();
        if (document == null) {
            return 0;
        }
        return Long.parseLong(document.get("count").toString());
    }


    //嵌入式文档

    public <T extends AbstractNestedMongoPo> T findOne(Class<T> clazz, QueryBuilder rootPathFilter, QueryBuilder nestedPathFilter,
                                                       QueryBuilder nestedFilter) throws MException {
        if (rootPathFilter == null) {
            throw new MException("[error][mdb][query is empty]");
        }

        Bson nested = nestedFilter == null ? null : (Bson) nestedFilter.get();
        BasicDBObject bson = (BasicDBObject) rootPathFilter.get();
        Bson wrapperNested = nestedPathFilter == null ? null : (BasicDBObject) nestedPathFilter.get();
        MongoIterable<Document> result = findNested(clazz, bson, wrapperNested, nested, Aggregates.limit(1));
        return parseOneNestedResult(clazz, result);
    }

    public <T extends AbstractNestedMongoPo> List<T> findAll(Class<T> clazz, QueryBuilder rootPathFilter, QueryBuilder nestedPathFilter,
                                                             QueryBuilder nestedFilter, NestedOptions options) throws MException {
        if (rootPathFilter == null) {
            throw new MException("[error][mdb][query is empty]");
        }
        BasicDBObject bson = (BasicDBObject) rootPathFilter.get();
        Bson wrapperNested = nestedPathFilter == null ? null : (BasicDBObject) nestedPathFilter.get();
        Bson nested = nestedFilter == null ? null : (Bson) nestedFilter.get();
        Bson op = options == null ? null : options.toAggregates();
        MongoIterable<Document> result = findNested(clazz, bson, wrapperNested, nested, op);
        return parseAllNestedResult(clazz, result);
    }

    private <T extends AbstractMongoPo> FindIterable<T> findSimple(Class<T> clazz, QueryBuilder query, Options options) throws MException {
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
        return result;
    }

    private synchronized long _nextId(String name) {
        Document document = new Document();
        document.put("key", name);
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

    private <T extends MongoPo> T parseOneResult(FindIterable<T> findIterable) {
        T t = findIterable.first();
        if (t == null) {
            return null;
        }
        t.document();
        return t;
    }

    private <T extends MongoPo> List<T> parseAllResult(FindIterable<T> findIterable) {
        MongoCursor<T> items = findIterable.iterator();
        List<T> result = new ArrayList<>();
        while (items.hasNext()) {
            T t = items.next();
            t.document();
            result.add(t);
        }
        return result;
    }

    private <T extends MongoPo> T parseOneNestedResult(Class<T> clazz, MongoIterable<Document> result) {
        Document document = result.first();
        if (document == null || document.size() == 0) {
            return null;
        }
        Document newRoot = document.get("newRoot", Document.class);
        String json = JSON.serialize(newRoot);
        T t = ZJsonUtils.loads(json, clazz);
        if (t != null) {
            t.document();
        }
        return t;
    }

    private <T extends MongoPo> List<T> parseAllNestedResult(Class<T> clazz, MongoIterable<Document> iterable) {
        MongoCursor<Document> iterator = iterable.iterator();
        List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            Document newRoot = doc.get("newRoot", Document.class);
            if (newRoot == null || newRoot.size() == 0) {
                continue;
            }
            String json = JSON.serialize(newRoot);
            T t = ZJsonUtils.loads(json, clazz);
            if (t != null) {
                t.document();
                result.add(t);
            }
        }
        return result;
    }

    private <T extends AbstractNestedMongoPo> boolean _fetch(T t) {
        MongoCollection<Document> db = mongoCollectionManager.getCollection(t);
        Document saveDocument = t.saveDocument();
        Bson filter = MongoHelper.deepFilter(t);
        UpdateOptions ops = new UpdateOptions();
        ops.upsert(true);
        if (async) {
            return mongoSyncManager.put(MongoTask.builder(t.database(), t.table(), new UpdateOneModel<>(filter, saveDocument, ops)));
        }
        db.updateOne(filter, saveDocument, ops);
        return true;
    }


    private <E extends MongoPo> MongoIterable<Document> findNested(Class<E> clazz, Bson rootPath, Bson nestedPath, Bson nested, Bson options) throws MException {

        String nestedName = MongoHelper.nested(clazz);
        List<Bson> pipeline = new ArrayList<>();
        if (rootPath != null) {
            Bson rootMatch = Aggregates.match(rootPath);
            pipeline.add(rootMatch);
        }
        int deep = 0;
        if (nestedPath != null) {
            BasicDBObject map = (BasicDBObject) nestedPath;
            deep = map.size();
            nestedPath = MongoHelper.wrapperNestedPathFilter(clazz, map);
            pipeline.add(nestedPath);
        }
        List<String> nestKeyList = MongoHelper.getNestedKey(clazz);
        int size = nestKeyList.size() - deep;
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Bson objectToArray = Filters.eq("$objectToArray", "$" + nestedName);
                Bson input = Filters.eq("input", objectToArray);
                Bson in = Filters.eq("in", "$$this.v");
                Bson map = Filters.and(input, in);
                Bson project = Filters.eq(nestedName, Filters.eq("$map", map));
                pipeline.add(Aggregates.project(project));
                pipeline.add(Aggregates.unwind("$" + nestedName));
            }
        }
        Bson replaceRoot = Aggregates.replaceRoot(Filters.eq("newRoot", "$" + nestedName));
        pipeline.add(replaceRoot);
        if (nested != null) {
            Bson wrapperNested = MongoHelper.wrapperNestedFilter(clazz, (BasicDBObject) nested);
            Bson rootMatch = Aggregates.match(wrapperNested);
            pipeline.add(rootMatch);
        }

        if (options != null) {
            pipeline.add(options);
        }
        return mongoCollectionManager.getCollection(clazz).aggregate(pipeline);
    }

}
