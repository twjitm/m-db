package com.mdb.test.entity;

import com.mdb.entity.AbstractMongoPo;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.enums.index.CompoundIndexed;
import com.mdb.enums.index.Indexed;
import org.bson.codecs.pojo.annotations.BsonProperty;

@MongoDocument(collection = "user_build")
@CompoundIndexed(value = {@Indexed(name = "uid"), @Indexed(name = "build_id")})
public class BuildPo extends AbstractMongoPo {

    @MongoId(name = "uid")
    private long uid;
    @MongoId(name = "build_id", tick = true)
    @BsonProperty("build_id")
    private long buildId;
    private int x;
    private int y;


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
}
