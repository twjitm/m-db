package com.mdb.test;

import com.mdb.base.query.Query;
import com.mdb.base.query.QueryOptions;
import com.mdb.entity.PrimaryKey;
import com.mdb.entity.TickId;
import com.mdb.exception.MException;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.UserInfoPo;

import java.util.List;

public class Test {
    public static void main(String[] args) throws MException {
        MongoManager.getInstance().load("127.0.0.1:27017");
        // createIndex();
        //add();
        //get();
        //findAll();
        //count();
        //update();
        //updateMany();
        nextId();
    }

    public static void createIndex() {
        MongoManager.getInstance().createIndex(TickId.class);

    }

    public static void add() {

        for (int i = 1; i < 100; i++) {
            UserInfoPo user = new UserInfoPo();
            user.setUid(i);
            user.setAge((byte) 26);
            user.setName("twj_" + i);
            user.setJob("developer");
            user.setJobType(i);
            boolean successful = MongoManager.getInstance().add(user);
        }
    }

    public static void get() throws MException {
        UserInfoPo user = MongoManager.getInstance().get(UserInfoPo.class, PrimaryKey.builder("uid", 8));
        System.out.println(user.toString());
    }

    public static void findOne() throws MException {
        UserInfoPo res = MongoManager.getInstance().findOne(UserInfoPo.class, Query.builder().and("job", "developer"));
        if (res != null) {
            System.out.println(res.toString());
        }
    }

    public static void findAll() throws MException {

        List<UserInfoPo> res = MongoManager.getInstance().findAll(UserInfoPo.class, Query.builder().and("job", "developer"),
                QueryOptions.builder().skip(0).limit(4));
        if (res != null) {
            for (UserInfoPo infoPo : res) {
                System.out.println(infoPo.toString());
            }
        }
    }

    public static void count() {
        //long count = MongoManager.getInstance().count(UserInfoPo.class, Query.builder().add("uid", 1));
        //long count = MongoManager.getInstance().count(UserInfoPo.class,
        //long count = Query.builder().or("uid", 1).or("uid", 2).and("job", "developer"));
        long count = MongoManager.getInstance().count(UserInfoPo.class,
                Query.builder().in("uid", 1, 2));
        System.out.println(count);
    }

    public static void update() throws MException {
        UserInfoPo user = MongoManager.getInstance().get(UserInfoPo.class,
                PrimaryKey.builder("uid", 8));
        if (user == null) {
            return;
        }
        user.setJob("java developer");
        user.setAge((byte) 29);
        boolean successful = MongoManager.getInstance().update(user);

    }

    public static void updateMany() throws MException {
        List<UserInfoPo> res = MongoManager.getInstance().findAll(UserInfoPo.class, Query.builder().and("job", "developer"),
                QueryOptions.builder().skip(0).limit(4));
        res.forEach(item -> item.setAge((byte) 22));
        boolean successful = MongoManager.getInstance().updateMany(res);
    }

    public static void nextId() {
        long id = MongoManager.getInstance().nextId(UserInfoPo.class);
        System.out.println(id);
    }


}
