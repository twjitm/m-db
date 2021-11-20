package com.mdb.test;

import com.mdb.base.query.Query;
import com.mdb.entity.PrimaryKey;
import com.mdb.exception.MException;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.AddressPo;
import com.mdb.test.entity.UserInfoPo;

import java.util.ArrayList;
import java.util.List;

public class NestedTest {

    public static void main(String[] args) throws MException {
        init();
//        addUser();
//        addManyUsers();
//        addAddress();
        get();

        //findAll();
        //count();
        //update();
        //updateMany();
        //nextId();
        // delete();
        //asyncOp();

    }

    static MongoManager mongoManager;

    private static void init() {
        mongoManager = new MongoManager("127.0.0.1:27017", false);
    }

    public static void addUser() throws MException {
        for (int i = 4; i < 6; i++) {
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


    public static void addManyUsers() throws MException {

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

    public static void addAddress() throws MException {
        for (int i = 1; i < 3; i++) {
            AddressPo addressPo = new AddressPo();
            addressPo.setAddress("beijing" + i);
            addressPo.setUid(1);
            MongoManager.getInstance().add(addressPo);
        }
    }


    public static void get() throws MException {
       // UserInfoPo userInfoPo = MongoManager.getInstance().get(UserInfoPo.class, PrimaryKey.builder("uid", 1));
       // System.out.println(userInfoPo.toString());
        AddressPo address = MongoManager.getInstance().get(AddressPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("aid", 11));

        System.out.println(address);

    }


    public static void getAll() throws MException {

    }

    public static void findOne() throws MException {

    }

    public static void findAll() throws MException {
    }

    public static void count() {
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
