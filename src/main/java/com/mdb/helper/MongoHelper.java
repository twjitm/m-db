package com.mdb.helper;

import com.mdb.entity.MongoPo;
import com.mdb.entity.NestedMongoPo;
import com.mdb.entity.PrimaryKey;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.exception.MException;
import com.mdb.utils.ZClassUtils;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author twjitm
 */
public class MongoHelper {

    /**
     * C.class.isAssignableFrom(B.class)
     * B 是不是 C 的实现或者是子类
     */


    /**
     * 是否位内嵌式
     */
    public static <T extends MongoPo> boolean isNested(Class<T> clazz) {
        return NestedMongoPo.class.isAssignableFrom(clazz);
    }

    /**
     * 内嵌基础文档
     */
    public static <T extends MongoPo> boolean isNestedBase(Class<T> clazz) {
        Class<? extends NestedMongoPo> rooter = clazz.getAnnotation(MongoDocument.class).rooter();
        boolean isabstract = Modifier.isAbstract(rooter.getModifiers());
        return isNested(clazz) && isabstract;
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

    /**
     * [rootFilter,nestedFilter]
     */
    public static <T extends MongoPo> Bson[] split(Class<T> clazz, PrimaryKey[] keys) throws MException {
        Bson[] result = new Bson[2];
        if (isNestedBase(clazz)) {
            StringBuilder val = new StringBuilder();
            Bson[] roots = new Bson[keys.length + 1];
            for (int i = 0; i < keys.length; i++) {
                roots[i] = Filters.eq(keys[i].getName(), keys[i].getValue());
                val.append("_").append(keys[i].getValue());
            }
            Bson _id = Filters.eq("_id", val.substring(1));
            roots[keys.length] = _id;
            result[0] = Filters.and(roots);
            result[1] = null;
            return result;
        }

        List<Bson> rootFilter = new ArrayList<>(8);
        Map<String, Object> map = new LinkedHashMap<>();
        for (PrimaryKey key : keys) {
            if (isRootKey(clazz, key.getName())) {
                rootFilter.add(Filters.eq(key.getName(), key.getValue()));
            } else {
                map.put(key.getName(), key.getValue());
            }
        }
        if (!rootFilter.isEmpty()) {
            result[0] = Filters.and(rootFilter);
        } else {
            result[0] = null;
        }
        if (!map.isEmpty()) {
            result[1] = wrapperNestedPathFilter(clazz, map);
        } else {
            result[1] = null;
        }
        return result;
    }


    public static <T extends MongoPo> Bson wrapperNestedPathFilter(Class<T> clazz, Map<String, Object> map) throws MException {
        StringBuilder nestedVal = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!isMongoIdKey(clazz, entry.getKey())) {
                throw new MException("nested key filed");
            }
            nestedVal.append(".").append(entry.getValue());
        }
        MongoDocument doc = mongoDocument(clazz);
        return Aggregates.project((eq(doc.nested(), "$" + doc.nested() + "." + nestedVal.substring(1))));
    }

    public static <T extends MongoPo> Bson wrapperNestedFilter(Class<T> clazz, Map<String, Object> map) throws MException {
        List<Bson> array = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            array.add(Filters.eq("newRoot." + entry.getKey(), entry.getValue()));
        }
        return Filters.and(array);
    }


    public static <T extends MongoPo> boolean isRootKey(Class<T> clazz, String keyName) {
        if (isNestedBase(clazz)) {
            return true;
        }
        MongoDocument base = mongoDocument(clazz);
        Class<? extends NestedMongoPo> rootClazz = base.rooter();
        List<MongoId> mongoIdList = ZClassUtils.getFieldAnnotations(rootClazz, MongoId.class);
        for (MongoId mongoId : mongoIdList) {
            if (mongoId.name().equals(keyName)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends MongoPo> boolean isMongoIdKey(Class<T> clazz, String key) {
        if (isRootKey(clazz, key)) {
            return true;
        }
        MongoId id = ZClassUtils.getFieldAnnotation(clazz, key, MongoId.class);
        return id != null;
    }


//    public static <T extends MongoPo> void build(Class<T> clazz,) {
//        if (!NestedMongoPo.class.isAssignableFrom(clazz)) {
//            return;
//        }
//        MongoDocument base = mongoDocument(clazz);
//        Class<? extends NestedMongoPo> rooter = base.rooter();
//
//        //base nested class
//        if (rooter.isAssignableFrom(AbstractNestedMongoPo.class)) {
//
//        }
//
//
//    }

}
