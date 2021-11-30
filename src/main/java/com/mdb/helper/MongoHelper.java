package com.mdb.helper;

import com.mdb.entity.MongoPo;
import com.mdb.entity.NestedMongoPo;
import com.mdb.entity.PrimaryKey;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.exception.MException;
import com.mdb.utils.ZClassUtils;
import com.mdb.utils.ZStringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;


/**
 * @author twjitm
 */
public class MongoHelper {

    /**
     * 是否位内嵌式
     */
    public static <T extends MongoPo> boolean isNested(Class<T> clazz) {
        return NestedMongoPo.class.isAssignableFrom(clazz);
    }

    public static <T extends MongoPo> String nested(Class<T> clazz) {
        MongoDocument document = clazz.getAnnotation(MongoDocument.class);
        return document.nested();
    }

    public static <T extends MongoPo> MongoDocument mongoDocument(Class<T> clazz) {
        return clazz.getAnnotation(MongoDocument.class);
    }


    public static <T extends MongoPo> T create(Class<T> clazz, Document document) {
        T t = ZClassUtils.create(clazz, document);
        t.document();
        return t;
    }

    //全路径
    public static <T extends MongoPo> Bson deepFilter(T obj) {

        Class<? extends MongoPo> clazz = obj.getClass();
        List<MongoId> ids = getMongoIds(clazz);
        Bson _id = makeMongoId(obj);
        String nestedPath = nestedPath(ids, obj);
        Bson nested = Filters.exists(nestedPath, false);
        return Filters.and(_id, wrapperMongoIds(ids, obj), nested);
    }

    public static <T extends MongoPo> List<MongoId> getMongoIds(Class<T> clazz) {
        return ZClassUtils.getFieldAnnotations(clazz, MongoId.class);
    }

    // 1_3
    public static <T extends MongoPo> Bson makeMongoId(T obj) {
        return Filters.eq("_id", makeMongoIdVal(obj));
    }

    public static <T extends MongoPo> String makeMongoIdVal(T obj) {
        Map<String, ?> data = obj.data();
        StringBuilder val = new StringBuilder();
        List<MongoId> mongoIds = getMongoIds(obj.getClass());
        for (MongoId id : mongoIds) {
            if (!id.root()) {
                continue;
            }
            val.append("_").append(data.get(id.name()).toString());
        }
        return val.substring(1);
    }

    public static <T extends NestedMongoPo> String nestedPathVal(T obj) {
        Class<? extends NestedMongoPo> clazz = obj.getClass();
        String nested = MongoHelper.nested(clazz);
        Map<String, ?> data = obj.data();
        return nested + parse(clazz, data);
    }

    private static <T extends NestedMongoPo> String parse(Class<T> clazz, Map data) {
        StringBuilder val = new StringBuilder();
        List<MongoId> ids = getNestedMongoIds(clazz);
        for (MongoId id : ids) {
            val.append(".").append(data.get(id.name()).toString());
        }
        return val.toString();
    }


    public static <T extends MongoPo> Bson makeMongoId(PrimaryKey... keys) {
        StringBuilder val = new StringBuilder();
        for (PrimaryKey key : keys) {
            val.append("_").append(key.getValue());
        }
        return Filters.eq("_id", val.substring(1));
    }

    public static <T extends MongoPo> String nestedPath(List<MongoId> ids, T t) {
        Class<? extends MongoPo> clazz = t.getClass();
        Map<String, ?> data = t.data();
        StringBuilder and = new StringBuilder();
        for (MongoId item : ids) {
            and.append(item.name()).append("=").append(data.get(item.name()));
        }
        if (ZStringUtils.isEmpty(and.toString())) {
            return nested(clazz);
        }
        return nested(clazz) + "." + and;
    }

    private static <T extends MongoPo> Bson wrapperMongoIds(List<MongoId> ids, T obj) {
        List<Bson> list = new ArrayList<>();
        Map<String, ?> data = obj.data();
        for (MongoId id : ids) {
            if (!id.root()) {
                continue;
            }
            Object val = data.get(id.name());
            list.add(Filters.eq(id.name(), val));
        }
        return Filters.and(list);
    }

    /**
     * [rootFilter,nestedFilter]
     */
    public static <T extends MongoPo> Bson[] adaptPrimaryKey(Class<T> clazz, PrimaryKey[] keys) throws MException {
        List<Bson> rootFilter = new ArrayList<>(8);
        BasicDBObject bo = new BasicDBObject();
        for (PrimaryKey key : keys) {
            String k = key.getName();
            Object v = key.getValue();
            if (isMongoRootIdKey(clazz, k)) {
                rootFilter.add(Filters.eq(k, v));
            } else {
                bo.append(k, v);
            }
        }
        Bson[] result = new Bson[2];
        result[0] = !rootFilter.isEmpty() ? Filters.and(rootFilter) : null;
        result[1] = !bo.isEmpty() ? bo : null;
        return result;
    }


    public static <T extends MongoPo> Bson wrapperNestedPathFilter(Class<T> clazz, Map<String, Object> map) throws MException {
        StringBuilder nestedVal = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            nestedVal.append(".").append(entry.getValue());
        }
        MongoDocument doc = mongoDocument(clazz);
        return Aggregates.project((Filters.eq(doc.nested(), "$" + doc.nested() + nestedVal)));
    }

    public static <T extends MongoPo> Bson wrapperNestedFilter(Class<T> clazz, Map<String, Object> map) throws MException {
        List<Bson> array = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            array.add(Filters.eq("newRoot." + entry.getKey(), entry.getValue()));
        }
        return Filters.and(array);
    }


    public static <T extends MongoPo> boolean isMongoRootIdKey(Class<T> clazz, String keyName) {
        List<MongoId> mongoIdList = getMongoIds(clazz);
        for (MongoId mongoId : mongoIdList) {
            if (mongoId.name().equals(keyName) && mongoId.root()) {
                return true;
            }
        }
        return false;
    }

    public static <T extends NestedMongoPo> String table(T obj) {
        return mongoDocument(obj.getClass()).table();
    }


    public static <T extends MongoPo> List<String> getRootKey(Class<T> clazz) {
        List<String> list = new ArrayList<>();
        List<MongoId> ids = getMongoIds(clazz);
        ids.forEach(item -> {
            if (item.root()) list.add(item.name());
        });
        return list;
    }

    public static <T extends MongoPo> List<String> getNestedKey(Class<T> clazz) {
        List<String> list = new ArrayList<>();
        List<MongoId> ids = getMongoIds(clazz);
        ids.forEach(item -> {
            if (!isMongoRootIdKey(clazz, item.name())) {
                list.add(item.name());
            }
        });
        return list;
    }

    public static <T extends MongoPo> List<MongoId> getNestedMongoIds(Class<T> clazz) {
        List<MongoId> list = new ArrayList<>();
        List<MongoId> ids = getMongoIds(clazz);
        for (MongoId id : ids) {
            if (!id.root()) {
                list.add(id);
            }
        }
        return list;
    }

}
