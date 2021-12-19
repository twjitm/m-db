package com.mdb.test;

import com.mdb.exception.MException;
import com.mdb.manager.MongoManager;
import com.mdb.test.entity.UserMailPo;

public class ExpireTest {
    public static void main(String[] args) throws MException {
        createIndex();
        add();
    }


    public static void createIndex() {
       // MongoManager.getInstance().createIndex(UserMailPo.class);
    }



    public static void add() throws MException {
        UserMailPo userMail = new UserMailPo();
        userMail.setUid(1);
        userMail.setTitle("hello world");
        userMail.setContent("hello world,i am developer");
        MongoManager.getInstance().add(userMail);
    }

}
