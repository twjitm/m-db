package com.mdb.entity;

public class MongoPrimaryKey {

    private String name;
    private Object value;

    public MongoPrimaryKey(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static MongoPrimaryKey builder(String name, Object value) {
        return new MongoPrimaryKey(name, value);
    }
}
