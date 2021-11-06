package com.mdb.test.entity;

import com.mdb.entity.AbstractMongoPo;
import com.mdb.enums.*;
import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;

@CompoundIndexed(value = {@Indexed(name = "uid"), @Indexed(name = "name")})
@MongoDocument(database = "mdb", collection = "user_info")
public class UserInfoPo extends AbstractMongoPo {

    @Indexed(name = "uid", unique = true)
    @Field(name = "uid",readOnly = true)
    @PrimaryKey()
    private long uid;
    @Indexed(name = "name", unique = true)
    private String name;
    private byte age;
    private String job;
    @Field(name = "job_type")
    private long jobTime;

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

    public long getJobTime() {
        return jobTime;
    }

    public void setJobTime(long jobTime) {
        this.jobTime = jobTime;
    }

    @Override
    public String toString() {
        return "UserInfoPo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", job='" + job + '\'' +
                ", jobTime=" + jobTime +
                '}';
    }
}
