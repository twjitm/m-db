package com.mdb.entity;


import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.enums.index.Indexed;

@MongoDocument(table = "mdb_tick")
public class TickId extends AbstractMongoPo {

    @Indexed(unique = true, name = "key")
    @MongoId(name = "key")
    private String key;

    private long value;


    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
