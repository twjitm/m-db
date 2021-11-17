package com.mdb.entity;

import org.bson.Document;

abstract public class AbstractMongoPo extends BaseMongoPo {


    @Override
    public Document saveDocument() {
        return this.document();
    }
}
