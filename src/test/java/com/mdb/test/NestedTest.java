package com.mdb.test;

import com.mdb.base.query.Query;
import com.mdb.entity.MongoPo;
import com.mdb.entity.NestedMongoPo;
import com.mdb.entity.PrimaryKey;
import com.mdb.exception.MException;
import com.mdb.manager.MongoCollectionManager;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.Cat;
import com.mdb.test.entity.Dog;
import com.mdb.test.entity.*;
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
        //addCity();
        //getCity();
        //addCat();
        getCat();
//        get();
//        getAll();
//        findOne();
//        findAll();
//        findAll();

        //count();
        //update();
        // updateAddress();
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
        for (int i = 8; i < 10; i++) {
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

    public static void addCity() throws MException {
        CityPo cityPo = new CityPo();
        cityPo.setUid(2);
        cityPo.setLevel(3);
        cityPo.setName("大城堡");
        cityPo.setX(1);
        cityPo.setY(2);
        MongoManager.getInstance().add(cityPo);
    }


    public static void addCat() throws MException {

        Cat cat = new Cat();
        cat.setUid(2);
        List<Dog> dogList = new ArrayList<>();
        Dog d1 = new Dog();
        cat.setName("tom");
        d1.setAge(2);
        d1.setName("skp");
        Dog d2 = new Dog();
        d2.setAge(4);
        d2.setName("skp1");
        dogList.add(d1);
        dogList.add(d2);
        cat.setFriend(dogList);
        MongoManager.getInstance().add(cat);
    }

    public static void getCat() throws MException {

        Cat cat = MongoManager.getInstance().get(Cat.class, PrimaryKey.builder("uid", 2));
        System.out.println(cat.getFriend().get(0).getName());
    }

    public static void getCity() throws MException {
        CityPo city = MongoManager.getInstance().get(CityPo.class, PrimaryKey.builder("uid", 2));

        System.out.println(city);
        CityPo c = MongoManager.getInstance().findOne(CityPo.class, Query.builder().and("uid", 2), null, Query.builder().and("name", "大城堡"));
        System.out.println(c);

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
        for (int i = 1; i < 6; i++) {
            AddressPo addressPo = new AddressPo();
            addressPo.setAddress("shagnhai" + i);
            addressPo.setUid(2);
            addressPo.setPid(2);
            MongoManager.getInstance().add(addressPo);
        }
    }


    public static void get() throws MException {
        UserInfoPo userInfoPo = MongoManager.getInstance().get(UserInfoPo.class, PrimaryKey.builder("uid", 1));
        System.out.println(userInfoPo.toString());
        AddressPo address = MongoManager.getInstance().get(AddressPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("pid", 2), PrimaryKey.builder("aid", 11));

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
        List<AddressPo> list = MongoManager.getInstance().findAll(AddressPo.class, Query.builder().and("uid", 1), Query.builder().and("pid", 2), null, null);
        System.out.println(list.size());
    }

    public static void count() throws MException {
        long count = MongoManager.getInstance().count(UserInfoPo.class, Query.builder().in("uid", 1, 2), null, null);
        System.out.println(count);
        long addCount = MongoManager.getInstance().count(AddressPo.class, Query.builder().and("uid", 1), Query.builder().and("pid", 2), null);
        System.out.println(addCount);
    }

    public static void update() throws MException {
        UserInfoPo user = MongoManager.getInstance().get(UserInfoPo.class, PrimaryKey.builder("uid", 1));
        if (user == null) {
            return;
        }
        user.setJob("java developer");
        user.setAge((byte) 29);
        boolean successful = MongoManager.getInstance().update(user);

    }

    public static void updateAddress() throws MException {
        AddressPo address = MongoManager.getInstance().get(AddressPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("pid", 2), PrimaryKey.builder("aid", 11));
        address.setAddress("shanghai1");
        MongoManager.getInstance().update(address);
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

        List<Bson> pipeline = new ArrayList<>();
        Bson rootMatch = Aggregates.match(rootFilter);
        pipeline.add(rootMatch);

        Bson nestedProject = Aggregates.project(eq(nestedName, "$" + nestedName + "." + 11));
        // pipeline.add(nestedProject);

        if (false) {
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
