package com.mdb.entity;

import com.mongodb.client.model.IndexModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

import java.util.SortedSet;

public interface MongoPo {

    Document document();

    public List<IndexModel> getIndex();

    Map<String, ?> data();

    Document modify();

    Bson primaryKeys();


}
