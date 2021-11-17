package com.mdb.test.entity;

import com.mdb.entity.AbstractNestedMongoPo;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.index.Indexed;

/**
 * 把addressPo这个对象嵌入到user这个对象中
 */
@MongoDocument(rooter = UserInfoPo.class, database = "mdb", collection = "address")
public class AddressPo extends AbstractNestedMongoPo {
    @Indexed(name = "uid")
    private long uid;
    @Indexed(name = "aid")
    private long aid;
    private String address;


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}