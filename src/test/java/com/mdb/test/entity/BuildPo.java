package com.mdb.test.entity;

import com.mdb.entity.AbstractMongoPo;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * 一个简单文档
 */
@MongoDocument(table = "user_build")
@CompoundIndexed(value = {@Indexed(name = "uid"), @Indexed(name = "build_id")})
public class BuildPo extends AbstractMongoPo {

    @MongoId(name = "uid", root = true)
    private long uid;
    @MongoId(name = "build_id", tick = true, root = true)
    @BsonProperty("build_id")
    private long buildId;
    private int x;
    private int y;
    private String type;
    private String name;


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getBuildId() {
        return buildId;
    }

    public void setBuildId(long buildId) {
        this.buildId = buildId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
