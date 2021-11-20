package com.mdb.test;

import com.mdb.manager.MongoManager;
import com.mdb.test.entity.AddressPo;
import com.mdb.test.entity.BuildPo;
import com.mdb.test.entity.UserInfoPo;

public class IndexTest {

    public static void main(String[] args) {
        createIndex();
    }

    public static void createIndex() {
        MongoManager.getInstance().createIndex(UserInfoPo.class);
        MongoManager.getInstance().createIndex(BuildPo.class);
        MongoManager.getInstance().createIndex(AddressPo.class);
    }

}
