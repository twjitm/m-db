package com.mdb.entity;

import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import com.mdb.helper.MongoHelper;
import com.mdb.utils.ZClassUtils;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

abstract public class AbstractMongoPo extends BaseMongoPo {


    @Override
    public Document saveDocument() {
        Document document = this.document();
        document.put("_id", MongoHelper.makeMongoIdVal(this));
        return document;
    }

    @Override
    public List<IndexModel> index() {
        List<IndexModel> ids = new ArrayList<>();
        List<Indexed> indexedList = ZClassUtils.getFieldAnnotations(this, Indexed.class);
        indexedList.forEach(item -> {
                    long expire = item.expireAfterSeconds();
                    IndexOptions op = new IndexOptions().unique(item.unique());
                    if (expire > 0) {
                        op.expireAfter(expire, TimeUnit.SECONDS);
                    }
                    ids.add(new IndexModel(item.order() == 1 ? Indexes.ascending(item.name()) : Indexes.descending(item.name()), op));
                }
        );
        CompoundIndexed compoundIndex = ZClassUtils.getClassAnnotations(this.getClass(), CompoundIndexed.class);
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
}
