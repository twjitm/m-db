package com.mdb.test.entity;

import com.mdb.entity.AbstractMongoPo;
import com.mdb.enums.*;
import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import org.bson.codecs.pojo.annotations.BsonProperty;

@CompoundIndexed(value = {@Indexed(name = "uid"), @Indexed(name = "name")})
@MongoDocument(database = "mdb", collection = "user_info")
public class UserInfoPo extends AbstractMongoPo {

    @Indexed(name = "uid", unique = true)
    @MongoId(name = "uid", tick = true)
    private long uid;

    @Indexed(name = "name", unique = true)
    private String name;

    private int age;

    private String job;

    @BsonProperty(value = "job_type")
    private long jobType;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public long getJobType() {
        return jobType;
    }

    public void setJobType(long jobType) {
        this.jobType = jobType;
    }

    @Override
    public String toString() {
        return "UserInfoPo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", job='" + job + '\'' +
                ", jobType=" + jobType +
                '}';
    }
}
