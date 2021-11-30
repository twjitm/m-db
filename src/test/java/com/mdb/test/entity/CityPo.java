package com.mdb.test.entity;

import com.mdb.entity.AbstractNestedMongoPo;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;

@MongoDocument(database = "mdb", table = "user_info", nested = "address")
public class CityPo extends AbstractNestedMongoPo {

    @MongoId(name = "uid", root = true)
    private long uid;

    private String name;
    private int x;
    private int y;
    private int level;


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
