package com.mdb.test;

import com.mdb.base.query.Query;
import com.mdb.base.query.QueryOptions;
import com.mdb.entity.PrimaryKey;
import com.mdb.entity.TickId;
import com.mdb.exception.MException;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.UserInfoPo;

import java.util.ArrayList;
import java.util.List;

public class Test {


    public static void main(String[] args) throws MException {
        //MongoManager.getInstance().load("127.0.0.1:27017");
        init();
        // createIndex();
        // add();
        //  addMany();
        //get();
        //findAll();
        //count();
        //update();
        //updateMany();
        //nextId();
        // delete();
        asyncOp();

        try {
            Thread.sleep(99999999);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static MongoManager mongoManager;

    private static void init() {
        mongoManager = new MongoManager();
        mongoManager.setUrl("127.0.0.1:27017");
        mongoManager.setAsync(true);
    }

    public static void createIndex() {
        MongoManager.getInstance().createIndex(TickId.class);
    }

    public static void add() throws MException {

        for (int i = 1; i < 100; i++) {
            UserInfoPo user = new UserInfoPo();
            user.setAge((byte) 26);
            user.setName("twj_" + i);
            user.setJob("developer");
            user.setJobType(i);
            boolean successful = mongoManager.add(user);
            if (successful) {
                System.out.println("insert successful=" + user.getUid());
            }
        }
    }

    public static void addMany() throws MException {

        List<UserInfoPo> list = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            UserInfoPo user = new UserInfoPo();
            user.setAge((byte) 26);
            user.setName("twj_" + i);
            user.setJob("developer");
            user.setJobType(i);
            list.add(user);
        }
        boolean successful = mongoManager.addMany(list);
        if (successful) {
            System.out.println("insert many successful");
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

    public static void delete() throws MException {
        UserInfoPo user = MongoManager.getInstance().get(UserInfoPo.class, PrimaryKey.builder("uid", 1));
        boolean successful = MongoManager.getInstance().delete(user);
        System.out.println(successful);
    }


    public static void asyncOp() throws MException {
        UserInfoPo user = mongoManager.get(UserInfoPo.class, PrimaryKey.builder("uid", 407));
        user.setAge(27);
        mongoManager.update(user);
        user.setJob("java developer");
        mongoManager.update(user);
        UserInfoPo user2 = mongoManager.get(UserInfoPo.class, PrimaryKey.builder("uid", 408));
        user2.setAge(28);
        mongoManager.update(user2);
        user2.setJob("java developer");
        mongoManager.update(user2);
    }
}
