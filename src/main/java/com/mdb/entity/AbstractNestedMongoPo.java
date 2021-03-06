package com.mdb.entity;

import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import com.mdb.helper.MongoHelper;
import com.mdb.utils.ZClassUtils;
import com.mdb.utils.ZStringUtils;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNestedMongoPo extends BaseMongoPo implements NestedMongoPo {

    @Override
    public Document saveDocument() {
        Document document = this.document();
        Document root = new Document();
        Document nest = new Document();
        String nested = MongoHelper.nestedPathVal(this);
        nest.put(nested, document);
        root.put("$set", nest);
        return root;
    }

    @Override
    public boolean checkPrimary() {
        return false;
    }

    @Override
    public Document document() {
        Map<String, ?> kv = data();
        kv.forEach(document::put);
        return document;
    }

    @Override
    public List<IndexModel> index() {
        List<IndexModel> ids = new ArrayList<>();
        Class<? extends NestedMongoPo> clazz = this.getClass();
        List<Indexed> indexedList = ZClassUtils.getFieldAnnotations(this, Indexed.class);
        String nested = MongoHelper.nested(clazz);
        indexedList.forEach(item -> {
                    String field = item.name();
                    if (!ZStringUtils.isEmpty(nested)) {
                        field = nested + "." + field;
                    }
                    Bson bson = item.order() == 1 ? Indexes.ascending(field) : Indexes.descending(field);
                    IndexOptions op = new IndexOptions().unique(item.unique());
                    long expire = item.expireAfterSeconds();
                    if (expire > 0) {
                        op.expireAfter(expire, TimeUnit.SECONDS);
                    }
                    IndexModel index = new IndexModel(bson, op);
                    ids.add(index);
                }
        );
        CompoundIndexed compoundIndex = ZClassUtils.getClassAnnotations(clazz, CompoundIndexed.class);
        if (compoundIndex != null) {
            Indexed[] array = compoundIndex.value();
            int order = compoundIndex.order();
            Bson[] bs = new Bson[array.length];
            for (int i = array.length - 1; i >= 0; i--) {
                Indexed index = array[i];
                String field = index.name();
                if (!ZStringUtils.isEmpty(nested)) {
                    field = nested + "." + index.name();
                }
                Bson bson = order == 1 ? Indexes.ascending(field) : Indexes.descending(field);
                bs[i] = bson;
            }
            ids.add(new IndexModel(Indexes.compoundIndex(bs), new IndexOptions().unique(compoundIndex.unique())));
        }
        return ids;
    }
}
