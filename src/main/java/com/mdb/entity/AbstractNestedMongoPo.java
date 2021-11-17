package com.mdb.entity;

import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.utils.ZStringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public abstract class AbstractNestedMongoPo extends BaseMongoPo implements NestedMongoPo {


    @Override
    public Document saveDocument() {
        Document document = super.document();
        if (this.rooter().isAssignableFrom(NestedMongoPo.class)) {
            System.out.println("嵌入式文档");
        }
        Document root = new Document();
        List<MongoId> ids = this.mongoIds();
        ids.forEach(item -> root.put(item.name(), data().get(item.name())));
        String table = nestedTable();
        if (ZStringUtils.isEmpty(table)) {
            table = "info";
        }
        root.put(table, document);
        return root;
    }

    @Override
    public Class<NestedMongoPo> rooter() {
        MongoDocument document = this.getClass().getAnnotation(MongoDocument.class);
        return (Class<NestedMongoPo>) document.rooter();
    }

    @Override
    public Bson rootFilter() {
        return null;
    }

    @Override
    public String collection() {
        return super.collection();
    }


    @Override
    public String nestedTable() {
        MongoDocument document = this.getClass().getAnnotation(MongoDocument.class);
        return document.nested();
    }
}
