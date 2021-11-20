package com.mdb.entity;

import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import com.mdb.utils.ZClassUtils;
import com.mdb.utils.ZStringUtils;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractNestedMongoPo extends BaseMongoPo implements NestedMongoPo {

    @Override
    public Document saveDocument() {
        Document document = this.document();
        String nested = nested();
        Document root = new Document();
        Document nest = new Document();
        List<MongoId> ids = this.mongoIds();
        if (!this.isBase()) {
            MongoId id = ids.get(0);
            String idv = data().get(id.name()).toString();
            // Document doc = new Document(idv, document);
            nest.put(nested + "." + idv, document);
        } else {
            nest.put(nested, document);
        }
        root.put("$set", nest);
        return root;
    }

    @Override
    public List<IndexModel> index() {
        List<IndexModel> ids = new ArrayList<>();
        List<Indexed> indexedList = ZClassUtils.getFieldAnnotations(this, Indexed.class);
        String nested = nested();
        indexedList.forEach(item -> {
                    String field = item.name();
                    if (!this.isRootKey(item.name())) {
                        field = nested + "." + field;
                    }
                    Bson bson = item.order() == 1 ? Indexes.ascending(field) : Indexes.descending(field);
                    IndexModel index = new IndexModel(bson, new IndexOptions().unique(item.unique()));
                    ids.add(index);
                }
        );
        CompoundIndexed compoundIndex = ZClassUtils.getClassAnnotations(this.getClass(), CompoundIndexed.class);
        if (compoundIndex != null) {
            Indexed[] array = compoundIndex.value();
            int order = compoundIndex.order();
            Bson[] bs = new Bson[array.length];
            for (int i = array.length - 1; i >= 0; i--) {
                Indexed index = array[i];
                String field = nested + "." + index.name();
                Bson bson = order == 1 ? Indexes.ascending(field) :
                        Indexes.descending(field);
                bs[i] = bson;
            }
            ids.add(new IndexModel(Indexes.compoundIndex(bs), new IndexOptions().unique(compoundIndex.unique())));
        }
        return ids;
    }

    public boolean isRootKey(String name) {
        List<MongoId> mongoIds = this.mongoIds();
        for (MongoId id : mongoIds) {
            if (ZStringUtils.eq(id.name(), name) && ZStringUtils.eq("", rooter())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String rooter() {
        MongoDocument document = this.getClass().getAnnotation(MongoDocument.class);
        Class<?> root = document.rooter();
        if (root.isAssignableFrom(AbstractNestedMongoPo.class)) {
            return "";
        }
        return root.getAnnotation(MongoDocument.class).table();
    }

    @Override
    public <T extends NestedMongoPo> Bson rootFilter() {

        MongoDocument document = this.getClass().getAnnotation(MongoDocument.class);
        Class<T> root = (Class<T>) document.rooter();
        Map<String, ?> data = data();
        List<MongoId> ids = this.mongoIds();
        if (!AbstractNestedMongoPo.class.isAssignableFrom(root)) {
            List<MongoId> rootIds = ZClassUtils.getFieldAnnotations(root, MongoId.class);
            Bson rootFilter = builderFilter(rootIds, data);
            Bson nested = Filters.exists(nestedPath(ids.get(0)), false);
            return Filters.and(Filters.eq("_id", makeMongoId(rootIds)), rootFilter, nested);
        }
        return Filters.and(Filters.eq("_id", makeMongoId(ids)), builderFilter(ids, data));
    }

    private Bson builderFilter(List<MongoId> ids, Map<String, ?> data) {
        Bson[] as = new Bson[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            MongoId id = ids.get(i);
            Object val = data.get(id.name());
            as[i] = Filters.eq(id.name(), val);
        }
        return Filters.and(as);
    }

    public String nestedPath(MongoId id) {
        if (isBase()) {
            return "";
        }
        String and = id.name() + "=" + data().get(id.name());
        return nested() + "." + and;
    }

    public boolean isBase() {
        String root = rooter();
        return ZStringUtils.isEmpty(root);
    }

    @Override
    public String table() {
        String root = rooter();
        if (!ZStringUtils.isEmpty(root)) {
            return root;
        }
        return super.table();
    }

    String nestedPath() {
        return "";
    }

    @Override
    public String nested() {
        MongoDocument document = this.getClass().getAnnotation(MongoDocument.class);
        return document.nested();
    }
}
