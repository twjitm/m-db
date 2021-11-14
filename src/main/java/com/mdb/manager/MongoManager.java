package com.mdb.manager;

import com.mdb.base.query.Query;
import com.mdb.base.query.QueryOptions;
import com.mdb.entity.MongoPo;
import com.mdb.entity.MongoTask;
import com.mdb.entity.PrimaryKey;
import com.mdb.entity.TickId;
import com.mdb.enums.MongoDocument;
import com.mdb.exception.MException;
import com.mdb.utils.ZCollectionUtil;
import com.mongodb.*;

import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MongoManager {

    private String url;
    private final static String DEFAULT_URL = "127.0.0.1:27017";
    private MongoClient mongoClient = null;
    private final BlockingQueue<MongoTask<Document>> taskPoll = new LinkedBlockingQueue<MongoTask<Document>>();

    private boolean sync;

    static final CodecProvider[] array = new CodecProvider[]{
            new ValueCodecProvider(),
            new DBRefCodecProvider(),
            new DocumentCodecProvider(new DocumentToDBRefTransformer()),
            new DBObjectCodecProvider(),
            new BsonValueCodecProvider(),
            new GeoJsonCodecProvider(),
            PojoCodecProvider.builder().automatic(true).build(),
    };
    private static final CodecRegistry DEFAULT_CODEC_REGISTRY = CodecRegistries.fromProviders(array);

    private final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    private final Map<String, MongoCollection<Document>> collections = new HashMap<>();

    private final static MongoManager instance = new MongoManager();

    public static MongoManager getInstance() {
        return instance;
    }


    public MongoManager() {
        this.url = DEFAULT_URL;
        initClient(url);
    }

    public MongoManager(String url) {
        this.url = url;
        initClient(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void load(String url) {
        initClient(url);
    }

    private void initClient(String url) {
        if (mongoClient == null) {
            if (url == null) {
                url = DEFAULT_URL;
            }
            mongoClient = new MongoClient(url);
        }
    }

    public <T extends MongoPo> void createIndex(Class<T> clazz) {

        try {
            MongoPo v = clazz.newInstance();
            MongoCollection<Document> db = this.getCollection(clazz);
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


    public void close() {
        mongoClient.close();
    }

    private MongoCollection<Document> getCollection(String database, String collection) {
        String key = database + ":" + collection;
        MongoCollection<Document> db = collections.get(key);
        if (db == null) {
            MongoDatabase dbs = mongoClient.getDatabase(database);
            db = dbs.withCodecRegistry(DEFAULT_CODEC_REGISTRY).getCollection(collection);
            collections.put(key, db);
        }
        return db;
    }

    private MongoCollection<Document> getCollection(MongoDocument document) {
        return this.getCollection(document.database(), document.collection());
    }

    public <T extends MongoPo> MongoCollection<Document> getCollection(T t) {
        MongoDocument document = t.getClass().getAnnotation(MongoDocument.class);
        return this.getCollection(document.database(), document.collection());
    }

    public <T extends MongoPo> MongoCollection<Document> getCollection(Class<T> clazz) {
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        return this.getCollection(document.database(), document.collection());
    }


    public <T extends MongoPo> boolean add(T t) {
        if (t == null) {
            return false;
        }
        Class<? extends MongoPo> clazz = t.getClass();
        MongoDocument mongoDocument = clazz.getAnnotation(MongoDocument.class);
        MongoCollection<Document> db = this.getCollection(mongoDocument);
        db.insertOne(t.document());
        return false;
    }


    public <T extends MongoPo> boolean addMany(List<T> list) {
        BulkWriteOptions op = new BulkWriteOptions().ordered(true);
        List<InsertOneModel<Document>> ins = new ArrayList<>();
        T t = list.get(0);
        Class<? extends MongoPo> clazz = t.getClass();
        MongoDocument mongoDocument = clazz.getAnnotation(MongoDocument.class);
        list.forEach(item -> ins.add(new InsertOneModel<>(item.document())));
        this.getCollection(mongoDocument).bulkWrite(ins, op);
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
        UpdateResult result = this.getCollection(t).updateOne(t.filters(), up);
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
                    ups.add(new UpdateOneModel<>(item.filters(), new Document("$set", modify)));
                }
            } catch (MException e) {
                e.printStackTrace();
            }
        });
        if (ZCollectionUtil.isEmpty(ups)) {
            return false;
        }
        MongoCollection<Document> collection = getCollection(list.get(0));
        BulkWriteResult result = collection.bulkWrite(ups);
        return result.wasAcknowledged();
    }

    public <T extends MongoPo> boolean delete(T t) {
        if (t == null) {
            return  false;
        }
        MongoCollection<Document> collection = getCollection(t);
        DeleteResult result = collection.deleteOne(t.filters());
        return result.getDeletedCount() == 1;
    }

    public <T extends MongoPo> boolean delete(Class<T> clazz, PrimaryKey... keys) {
        MongoCollection<Document> collection = getCollection(clazz);
        List<Bson> filters = new ArrayList<>();
        for (PrimaryKey key : keys) {
            Bson filter = Filters.eq(key.getName(), key.getValue());
            filters.add(filter);
        }
        Bson filter = Filters.and(filters);
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
        T t = this.getCollection(document).find(filter, clazz).first();
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
        MongoCollection<Document> db = this.getCollection(document);

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
        MongoCollection<Document> collection = this.getCollection(clazz);
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
        MongoCollection<Document> collection = this.getCollection(clazz);
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

    public <T extends MongoPo> long nextId(Class<T> tick) {
        String key = tick.getSimpleName();
        Document document = new Document();
        document.put("key", key);
        Document up = new Document();
        up.put("$inc", new Document("value", 1));
        MongoCollection<Document> collection = this.getCollection(TickId.class);
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
        return this.getCollection(clazz).countDocuments(filter);
    }

}
