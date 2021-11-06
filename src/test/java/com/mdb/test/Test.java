package com.mdb.test;

import com.mdb.base.query.Query;
import com.mdb.base.query.QueryOptions;
import com.mdb.entity.MongoPrimaryKey;
import com.mdb.error.MException;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.UserInfoPo;
import com.mongodb.QueryOperators;

import java.util.List;

public class Test {
    public static void main(String[] args) throws MException {
        MongoManager.getInstance().load("127.0.0.1:27017");
        // createIndex();
        //add();
        //get();
        findAll();
    }

    public static void createIndex() {
        MongoManager.getInstance().createIndex(UserInfoPo.class);

    }

    public static void add() {

        for (int i = 1; i < 100; i++) {
            UserInfoPo user = new UserInfoPo();
            user.setUid(i);
            user.setAge((byte) 26);
            user.setName("twj_" + i);
            user.setJob("developer");
            boolean successful = MongoManager.getInstance().add(user);
        }
    }

    public static void get() throws MException {
        UserInfoPo user = MongoManager.getInstance().get(UserInfoPo.class, MongoPrimaryKey.builder("uid", 8));
        System.out.println(user.toString());
    }

    public static void findOne() throws MException {
        UserInfoPo res = MongoManager.getInstance().findOne(UserInfoPo.class, Query.builder().add("job", "developer"));
        if (res != null) {
            System.out.println(res.toString());
        }
    }

    public static void findAll() throws MException {

        List<UserInfoPo> res = MongoManager.getInstance().findAll(UserInfoPo.class, Query.builder().add("job", "developer"),
                QueryOptions.builder().setSkip(2).setLimit(4));
        if (res != null) {
            for (UserInfoPo infoPo : res) {
                System.out.println(infoPo.toString());
            }
        }
    }
}
