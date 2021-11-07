package com.mdb.entity;

public class PrimaryKey {

    private String name;
    private Object value;

    public PrimaryKey(String name, Object value) {
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

    public static PrimaryKey builder(String name, Object value) {
        return new PrimaryKey(name, value);
    }
}
