package com.mdb.test;

import com.mdb.base.query.Query;
import com.mdb.entity.MongoPo;
import com.mdb.entity.NestedMongoPo;
import com.mdb.entity.PrimaryKey;
import com.mdb.exception.MException;
import com.mdb.helper.MongoHelper;
import com.mdb.manager.MongoCollectionManager;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.AddressPo;
import com.mdb.test.entity.UserInfoPo;
import com.mongodb.QueryBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class NestedTest {

    public static void main(String[] args) throws MException {
        init();
        //findTest(AddressPo.class);
        //addUser();
        //addManyUsers();
        //addAddress();
        //get();
        // getAll();
        findOne();
        findAll();

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
        UserInfoPo userInfoPo = MongoManager.getInstance().get(UserInfoPo.class, PrimaryKey.builder("uid", 1));
        System.out.println(userInfoPo.toString());
        AddressPo address = MongoManager.getInstance().get(AddressPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("aid", 11));

        System.out.println(address.toString());

    }


    public static void getAll() throws MException {
        List<AddressPo> list = MongoManager.getInstance().getAll(AddressPo.class, PrimaryKey.builder("uid", 1));
        list.forEach(item -> System.out.println(item.toString()));
    }

    public static void findOne() throws MException {
        AddressPo list = MongoManager.getInstance().findOne(AddressPo.class, Query.builder().and("uid", 1), null, Query.builder().and("address", "beijing2"));
        System.out.println(list);
    }

    public static void findAll() throws MException {//Query.builder().and("address", "beijing2")
        List<AddressPo> list = MongoManager.getInstance().findAll(AddressPo.class, Query.builder().and("uid", 1), null, null, null);
        System.out.println(list.size());
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

    public static <T extends NestedMongoPo, E extends MongoPo> MongoIterable<Document> findTest(Class<E> clazz) {

        String nestedName = "address";
        Bson rootFilter = Filters.eq("uid", 1);
//        Bson nestedFilter = Filters.eq("aid", 11);
//        Bson nextedMatch = Aggregates.match(nestedFilter);

        boolean isBase = MongoHelper.isNestedBase(clazz);
        List<Bson> pipeline = new ArrayList<>();
        Bson rootMatch = Aggregates.match(rootFilter);
        pipeline.add(rootMatch);

        Bson nestedProject = Aggregates.project(eq(nestedName, "$" + nestedName + "." + 11));
        // pipeline.add(nestedProject);

        if (!isBase) {
            Bson objectToArray = Filters.eq("$objectToArray", "$" + nestedName);
            Bson input = Filters.eq("input", objectToArray);
            Bson in = Filters.eq("in", "$$this.v");
            Bson map = Filters.and(input, in);
            Bson project = Filters.eq(nestedName, Filters.eq("$map", map));
            pipeline.add(Aggregates.project(project));
            pipeline.add(Aggregates.unwind("$" + nestedName));

        }
        Bson replaceRoot = Aggregates.replaceRoot(Filters.eq("newRoot", "$" + nestedName));
        pipeline.add(replaceRoot);

        pipeline.add(Aggregates.match(Filters.eq("newRoot.address", "beijing2")));
        AggregateIterable<Document> result = new MongoCollectionManager("127.0.0.1:27017").getCollection(clazz).aggregate(pipeline);

        System.out.println(result.first().get("newRoot"));
//
//        MongoCursor<Document> it = result.iterator();
//        while (it.hasNext()){
//            System.out.println(it.next());
//        }


        return null;
    }
}
