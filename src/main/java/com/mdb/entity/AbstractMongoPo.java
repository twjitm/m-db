package com.mdb.entity;

import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import com.mdb.enums.MongoId;
import com.mdb.exception.MException;
import com.mdb.utils.ZClassUtils;
import com.mdb.utils.ZTimeUtils;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

abstract public class AbstractMongoPo implements MongoPo {

    protected long ctime;
    protected long mtime;
    private final Document document = new Document();


    public AbstractMongoPo() {
        this.ctime = ZTimeUtils.Now();
        this.mtime = ZTimeUtils.Now();
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public void setMtime(long mtime) {
        this.mtime = mtime;
    }

    public long getCtime() {
        return ctime;
    }

    public long getMtime() {
        return mtime;
    }

    @Override
    public Document document() {
        if (document.size() > 0) {
            return document;
        }
        Map<String, Object> kv = ZClassUtils.getClassFiledKv(this);
        kv.forEach(document::put);
        return document;
    }

    @Override
    public List<IndexModel> getIndex() {
        List<IndexModel> ids = new ArrayList<>();
        List<Indexed> indexedList = ZClassUtils.getFieldAnnotations(this, Indexed.class);
        indexedList.forEach(item ->
                ids.add(new IndexModel(item.order() == 1 ? Indexes.ascending(item.name()) : Indexes.descending(item.name()),
                        new IndexOptions().unique(item.unique()))));
        CompoundIndexed compoundIndex = ZClassUtils.getClassAnnotations(this, CompoundIndexed.class);
        if (compoundIndex != null) {
            Indexed[] array = compoundIndex.value();
            int order = compoundIndex.order();
            Bson[] bs = new Bson[array.length];
            for (int i = array.length - 1; i >= 0; i--) {
                Indexed index = array[i];
                bs[i] = order == 1 ? Indexes.ascending(index.name()) : Indexes.descending(index.name());
            }
            ids.add(new IndexModel(Indexes.compoundIndex(bs), new IndexOptions().unique(compoundIndex.unique())));
        }
        return ids;

    }

    @Override
    public Map<String, ?> data() {
        return ZClassUtils.getClassFiledKv(this);
    }

    @Override
    public Document modify() throws MException {
        Map<String, Object> kv = ZClassUtils.getClassFiledKv(this);
        Document modify = new Document();
        for (Map.Entry<String, Object> entry : kv.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            Object ov = document.get(k);
            if (ov != v) {
                if (ZClassUtils.isReadOnlyField(this, k)) {
                    throw new MException("write read only field name = " + k);
                }
                modify.put(k, v);
            }
        }
        return modify;
    }

    @Override
    public Bson primaryKeys() {
        List<MongoId> pks = ZClassUtils.getFieldAnnotations(this, MongoId.class);
        Map<String, ?> data = this.data();
        List<Bson> list = new ArrayList<>();
        pks.forEach(item -> {
            String name = item.name();
            Bson bq = Filters.eq(name, data.get(name));
            list.add(bq);
        });
        return Filters.and(list);
    }

    @Override
    public String toJsonString() {
        return this.document().toJson();
    }
}
