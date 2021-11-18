package com.mdb.entity;

import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import com.mdb.utils.ZClassUtils;
import com.mdb.utils.ZStringUtils;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNestedMongoPo extends BaseMongoPo implements NestedMongoPo {

    @Override
    public Document saveDocument() {
        Document document = this.document();
        String nested = nested();
        Document root = new Document();
        root.put(nested, document);
        List<MongoId> ids = this.mongoIds();
        //fixme 这个地方有问题
        ids.forEach(item -> root.put(item.name(), data().get(item.name())));
        return root;
    }

    @Override
    public List<IndexModel> getIndex() {
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
        CompoundIndexed compoundIndex = ZClassUtils.getClassAnnotations(this, CompoundIndexed.class);
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
            if (ZStringUtils.eq(id.name(), name)
                    && ZStringUtils.eq("", rooter())) {
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
    public Bson rootFilter() {
        return null;
    }

    @Override
    public String table() {
        String root = rooter();
        if (!ZStringUtils.isEmpty(root)) {
            return root;
        }
        return super.table();
    }

    @Override
    public String nested() {
        MongoDocument document = this.getClass().getAnnotation(MongoDocument.class);
        return document.nested();
    }
}
