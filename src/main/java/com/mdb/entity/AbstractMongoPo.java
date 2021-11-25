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

abstract public class AbstractMongoPo extends BaseMongoPo {


    @Override
    public Document saveDocument() {
        Document document = this.document();
        document.put("_id", MongoHelper.makeMongoId(mongoIds(),this));
        return document;
    }

    @Override
    public List<IndexModel> index() {
        List<IndexModel> ids = new ArrayList<>();
        List<Indexed> indexedList = ZClassUtils.getFieldAnnotations(this, Indexed.class);
        indexedList.forEach(item ->
                ids.add(new IndexModel(item.order() == 1 ? Indexes.ascending(item.name()) : Indexes.descending(item.name()),
                        new IndexOptions().unique(item.unique()))));
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
