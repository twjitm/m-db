package com.mdb.entity;

import org.bson.Document;

import java.util.Map;

import java.util.SortedSet;

public interface MongoPo {

    Document document();

    Map<String, ?> data();

    void ticker(String filed, long id);

    Map<String, ?> modify();

    SortedSet<MongoPrimaryKey> primaryKeys();


}
