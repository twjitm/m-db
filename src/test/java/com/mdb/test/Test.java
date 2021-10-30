package com.mdb.test;

import com.mdb.manager.MongoManager;
import com.mdb.test.entity.UserInfoPo;

public class Test {
    public static void main(String[] args) {
        MongoManager.getInstance().load("127.0.0.1:27017");
        get();
    }

    public static void get() {
                MongoManager.getInstance().scanIndex(UserInfoPo.class);

    }
}
