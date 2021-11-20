package com.mdb.entity;

import org.bson.Document;

abstract public class AbstractMongoPo extends BaseMongoPo {


    @Override
    public Document saveDocument() {
        Document document = this.document();
        document.put("_id", makeMongoId(mongoIds()));
        return document;
    }
}
