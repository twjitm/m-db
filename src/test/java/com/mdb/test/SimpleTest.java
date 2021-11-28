package com.mdb.test;

import com.mdb.base.query.Query;
import com.mdb.base.query.QueryOptions;
import com.mdb.entity.PrimaryKey;
import com.mdb.exception.MException;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.AddressPo;
import com.mdb.test.entity.BuildPo;
import com.mdb.test.entity.UserInfoPo;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单文档模型的crud
 */
public class SimpleTest {


    public static void main(String[] args) throws MException {
        init();
        // addBuild();
        //addManyBuild();
        get();
        getAll();
        findOne();
        findAll();
//
        update();

    }


    private static void addManyBuild() throws MException {
        List<BuildPo> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            BuildPo po = new BuildPo();
            po.setUid(1);
            po.setX(i);
            po.setY(i);
            po.setName("汤和一品" + i + "幢");
            po.setType("商业");
            list.add(po);
        }
        MongoManager.getInstance().addMany(list);
    }

    public static void addBuild() throws MException {
        BuildPo buildPo = new BuildPo();
        buildPo.setY(6);
        buildPo.setX(7);
        buildPo.setUid(1);
        buildPo.setName("北京SOHO大厦");
        buildPo.setType("高端建筑");
        MongoManager.getInstance().add(buildPo);
    }

    /**
     * 主键查询
     */
    public static void get() throws MException {
        BuildPo b = MongoManager.getInstance().get(BuildPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("build_id", 2));
        System.out.println(b.getBuildId());
    }

    public static void getAll() throws MException {
        List<BuildPo> bs = MongoManager.getInstance().getAll(BuildPo.class, PrimaryKey.builder("uid", 1));
        System.out.println("getAll size=" + bs.size());
    }


    public static void findOne() throws MException {
        BuildPo b = MongoManager.getInstance().findOne(BuildPo.class, Query.builder().and("uid", 1).and("build_id", 0));
        System.out.println(b.getX() + "|" + b.getY());

    }

    public static void findAll() throws MException {
        List<BuildPo> bs = MongoManager.getInstance().findAll(BuildPo.class, Query.builder().and("uid", 1).and("y", 6), QueryOptions.builder().limit(100));
        System.out.println("findAll size=" + bs.size());

    }

    public static void update() throws MException {
        BuildPo b = MongoManager.getInstance().get(BuildPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("build_id", 2));
        b.setName("new name build");
        MongoManager.getInstance().update(b);
    }


    static MongoManager mongoManager;

    private static void init() {
        mongoManager = new MongoManager("127.0.0.1:27017", false);
    }


}
