package com.mdb.entity;

import com.mdb.utils.ZClassUtils;
import org.bson.Document;

import java.util.Map;

abstract public class AbstractMongoPo implements MongoPo {

    private final Document document;

    public AbstractMongoPo() {
        document = new Document();
        this.document();
    }

    @Override
    public Document document() {
        Map<String, Object> kv = ZClassUtils.getClassFiledKv(this);
        kv.forEach(document::put);
        return document;
    }

    public void getIndex() {

    }

}
