package com.mdb.entity;

import com.mdb.enums.MongoId;
import com.mdb.utils.ZStringUtils;
import com.mongodb.client.model.IndexModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public abstract class AbstractNestedMongoPo extends BaseMongoPo implements NestedMongoPo {


    @Override
    public Document document() {
        return super.document();
    }

    @Override
    public Document saveDocument() {
        Document document = this.document();
        String table = nestedTable();
        Document root = new Document();
        root.put(table, document);
        List<MongoId> ids = this.mongoIds();
        ids.forEach(item -> root.put(item.name(), data().get(item.name())));
        return root;
    }

    @Override
    public List<IndexModel> getIndex() {
        return null;
    }

    @Override
    public Bson rootFilter() {
        return null;
    }

    @Override
    public String collection() {
        return super.collection();
    }

}
