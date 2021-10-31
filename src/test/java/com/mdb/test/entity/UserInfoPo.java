package com.mdb.test.entity;

import com.mdb.entity.AbstractMongoPo;
import com.mdb.enums.CompoundIndexed;
import com.mdb.enums.Indexed;
import com.mdb.enums.MongoDocument;

@CompoundIndexed(name = "",value = {@Indexed(name = "uid", field = "field"), @Indexed(name = "name")})
@MongoDocument(database = "mdb", collection = "user_name")
public class UserInfoPo extends AbstractMongoPo {

    @Indexed(name = "uid", unique = true)
    private long uid;
    @Indexed(name = "name")
    private String name;
    private byte age;
    private String job;

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

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
