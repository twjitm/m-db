package com.mdb.test.entity;

import com.mdb.entity.AbstractNestedMongoPo;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;

import java.util.List;

@MongoDocument(table = "user_info", nested = "cat")
public class Cat extends AbstractNestedMongoPo {
    @MongoId(name = "uid", root = true)
    private long uid;
    private String name;
    private List<Dog> friend;

    public List<Dog> getFriend() {
        return friend;
    }

    public long getUid() {
        return uid;
    }

    public void setFriend(List<Dog> friend) {
        this.friend = friend;
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

    @Override
    public String toString() {
        return "Cat{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", friend=" + friend +
                '}';
    }
}
