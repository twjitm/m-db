package com.mdb.entity;

import com.mdb.enums.Indexed;
import com.mdb.utils.ZClassUtils;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.util.*;

abstract public class AbstractMongoPo implements MongoPo {

    Document document = new Document();

    @Override
    public Document document() {
        Map<String, Object> kv = ZClassUtils.getClassFiledKv(this);
        kv.forEach(document::put);
        return document;
    }

    @Override
    public List<IndexModel> getIndex() {
        List<IndexModel> ids = new ArrayList<>();
        List<Indexed> indexedList = ZClassUtils.getFieldAnnotations(this, Indexed.class);
        indexedList.forEach(item ->
                ids.add(new IndexModel(Indexes.ascending(item.name()),
                        new IndexOptions().unique(item.unique()))));
        return ids;

    }

    @Override
    public Map<String, ?> data() {
        return null;
    }

    @Override
    public Map<String, ?> modify() {
        return null;
    }

    @Override
    public SortedSet<MongoPrimaryKey> primaryKeys() {
        return null;
    }

    @Override
    public void ticker(String filed, long id) {

    }
}
