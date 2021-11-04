package com.mdb.test;

import com.mdb.entity.MongoPrimaryKey;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.UserInfoPo;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        MongoManager.getInstance().load("127.0.0.1:27017");
        get();
    }

    public static void createIndex() {
        MongoManager.getInstance().createIndex(UserInfoPo.class);

    }

    public static void add() {

        for (int i = 1; i < 100; i++) {
            UserInfoPo user = new UserInfoPo();
            user.setUid(i);
            user.setAge((byte) 26);
            user.setName("twj_" + new Random(1).nextLong());
            user.setJob("developer");
            boolean successful = MongoManager.getInstance().add(user);
        }
    }

    public static void get() {
        UserInfoPo user = MongoManager.getInstance().get(UserInfoPo.class,MongoPrimaryKey.builder("uid",1));
        System.out.println(user.getAge());

    }
}
