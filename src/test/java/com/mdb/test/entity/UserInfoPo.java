package com.mdb.test.entity;

import com.mdb.entity.AbstractMongoPo;
import com.mdb.enums.CompoundIndexed;
import com.mdb.enums.Indexed;
import com.mdb.enums.MongoDocument;

@CompoundIndexed(value = {@Indexed(name = "uid", field = "field"), @Indexed(name = "name")})
@MongoDocument(database = "mdb", collection = "user_name")
public class UserInfoPo extends AbstractMongoPo {

    @Indexed(name = "_uid", field = "uid")
    private long uid;
    @Indexed(name = "_name")
    private String name;
    private byte age;
    private String job;

}
