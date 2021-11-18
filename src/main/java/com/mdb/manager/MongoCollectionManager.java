package com.mdb.manager;

import com.mdb.entity.MongoPo;
import com.mdb.enums.MongoDocument;
import com.mdb.utils.ZClassUtils;
import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import org.bson.Document;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.HashMap;
import java.util.Map;

public class MongoCollectionManager {
    private final static String DEFAULT_URL = "127.0.0.1:27017";
    private MongoClient mongoClient = null;

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


    public MongoCollectionManager(String url) {
        init(url);
    }

    public void init(String url) {
        if (mongoClient == null) {
            if (url == null) {
                url = DEFAULT_URL;
            }
            mongoClient = new MongoClient(url);
        }
    }

    public MongoCollection<Document> getCollection(String database, String collection) {
        String key = this.getDbKey(database, collection);
        MongoCollection<Document> db = collections.get(key);
        if (db == null) {
            MongoDatabase dbs = mongoClient.getDatabase(database);
            db = dbs.withCodecRegistry(DEFAULT_CODEC_REGISTRY).getCollection(collection);
            collections.put(key, db);
        }
        return db;
    }

    public MongoCollection<Document> getCollection(String key) {

        MongoCollection<Document> db = collections.get(key);
        if (db == null) {
            String[] sp = key.split(":");
            MongoDatabase dbs = mongoClient.getDatabase(sp[0]);
            db = dbs.withCodecRegistry(DEFAULT_CODEC_REGISTRY).getCollection(sp[1]);
            collections.put(key, db);
        }
        return db;
    }

    public <T extends MongoPo> MongoCollection<Document> getCollection(T t) {
        return this.getCollection(t.database(), t.table());
    }

    public <T extends MongoPo> MongoCollection<Document> getCollection(Class<T> clazz) {
        T empty = ZClassUtils.create(clazz);
        return this.getCollection(empty.database(), empty.table());
    }

    public String getDbKey(String database, String collection) {
        return database + ":" + collection;
    }

}
