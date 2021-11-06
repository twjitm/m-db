package com.mdb.utils;

import com.google.common.base.CaseFormat;
import com.mdb.entity.MongoPrimaryKey;
import org.bson.codecs.pojo.annotations.BsonProperty;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ZClassUtils {

    static Map<Class<?>, String> classPool = new HashMap<>();

    public static <T> Map<String, Object> getClassFiledKv(T t) {
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> kv = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true); // 私有属性必须设置访问权限
            Object v = getFieldVal(t, field);
            BsonProperty fd = field.getAnnotation(BsonProperty.class);
            String name = "";
            if (fd != null && !ZStringUtils.isEmpty(fd.value())) {
                name = fd.value();
            } else {
                name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            }
            kv.put(name, v);
        }
        return kv;
    }

    public static <T> Map<String, Object> getClassOriginalFiledKv(T t) {
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> kv = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true); // 私有属性必须设置访问权限
            Object v = getFieldVal(t, field);
            kv.put(field.getName(), v);
        }
        return kv;
    }

    public static <T> Map<String, String> getFiledKv2(T t) {
        Class<?> clazz = t.getClass();
        Field[] fileds = clazz.getDeclaredFields();
        Map<String, String> kv = new HashMap<>();
        for (Field field : fileds) {
            field.setAccessible(true); // 私有属性必须设置访问权限
            Object v = getFieldVal(t, field);
            String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            if (v == null) {
                v = "";
            }
            kv.put(name, v.toString());
        }
        return kv;
    }


    public static <T> Object getFieldVal(T obj, Field field) {
        try {
            switch (field.getType().getTypeName()) {
                case "int":
                    return field.getInt(obj);
                case "java.lang.String":
                    Object v = field.get(obj);
                    return v == null ? null : v.toString();
                case "long":
                    return field.getLong(obj);
                case "float":
                    return field.getFloat(obj);
                case "double":
                    return field.getDouble(obj);
                case "boolean":
                    return field.getBoolean(obj);
                case "byte":
                    return field.getByte(obj);
                case "java.util.Date":
                    Date date = (Date) field.get(obj);
                    if (date == null) {
                        return null;
                    }
                    return ZTimeUtils.getDateString(date);
                default:
                    return field.get(obj);

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T create(Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(t);
        return t;
    }

    public static <T> T create(Class<T> clazz, Map<String, Object> values) {
        T t = null;
        try {
            t = clazz.newInstance();
            if (values == null) {
                return t;
            }
            T finalT = t;
            values.forEach((k, v) -> {
                String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, k);
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(finalT, v);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            t = finalT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(t);
        return t;
    }

    public static <T> String getPackageNameBy(Class<T> clazz, String replace) {
        String path = classPool.get(clazz);
        if (path == null) {
            path = org.apache.commons.lang3.ClassUtils.getPackageCanonicalName(clazz).replace(".", replace);
            classPool.put(clazz, path);
        }
        path = path + ":" + clazz.getSimpleName();
        return path;
    }

    public static <T> String getCachePk(Class<T> clazz, MongoPrimaryKey... pks) {
        StringBuilder baseKey = new StringBuilder(ZClassUtils.getPackageNameBy(clazz, ":"));
        for (MongoPrimaryKey key : pks) {
            baseKey.append(":").append(key.getValue());
        }
        return baseKey.toString();
    }

    public static <T> String getCachePk(Class<T> clazz, List<MongoPrimaryKey> pks) {
        StringBuilder baseKey = new StringBuilder(ZClassUtils.getPackageNameBy(clazz, ":"));
        for (MongoPrimaryKey key : pks) {
            baseKey.append(":").append(key.getValue());
        }
        return baseKey.toString();
    }


    public static <T> List<T> getSubListPage(List<T> list, int skip, int pageSize) {
        if (list != null && !list.isEmpty()) {
            int endIndex = skip + pageSize;
            if (skip <= endIndex && skip <= list.size()) {
                if (endIndex > list.size()) {
                    endIndex = list.size();
                }

                return list.subList(skip, endIndex);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    private static Field[] getAllFields(Object obj) {
        Class<?> clazz = obj.getClass();

        Field[] rt;
        for (rt = null; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] tmp = clazz.getDeclaredFields();
            rt = combine(rt, tmp);
        }

        return rt;
    }

    private static Field[] combine(Field[] a, Field[] b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            Field[] rt = new Field[a.length + b.length];
            System.arraycopy(a, 0, rt, 0, a.length);
            System.arraycopy(b, 0, rt, a.length, b.length);
            return rt;
        }
    }

    private static Object getFieldsValueObj(Object obj, String fieldName) {
        Field field = getDeclaredField(obj, fieldName);
        field.setAccessible(true);

        try {
            return field.get(obj);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static String getFieldsValueStr(Object obj, String fieldName) {
        Object o = getFieldsValueObj(obj, fieldName);
        return o instanceof Date ? ZTimeUtils.dateToString((Date) o) : o.toString();
    }

    private static Field getDeclaredField(Object object, String fieldName) {
        Class clazz = object.getClass();

        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception var4) {
                clazz = clazz.getSuperclass();
            }
        }

        return null;
    }

    private static Method getSetMethod(Object object, String method, Class<?> fieldType) {
        Class clazz = object.getClass();

        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredMethod(method, fieldType);
            } catch (Exception var5) {
                clazz = clazz.getSuperclass();
            }
        }

        return null;
    }

    private static String buildSetMethod(String fieldName) {
        StringBuffer sb = new StringBuffer("set");
        if (fieldName.length() > 1) {
            String first = fieldName.substring(0, 1);
            String next = fieldName.substring(1);
            sb.append(first.toUpperCase()).append(next);
        } else {
            sb.append(fieldName.toUpperCase());
        }

        return sb.toString();
    }

    public static <T> boolean isEmptyList(List<T> list) {
        boolean b = false;
        if (list == null || list.isEmpty()) {
            b = true;
        }

        return b;
    }

    public static <T extends Annotation, E> List<T> getFieldAnnotations(E t, Class<T> type) {

        List<T> list = new ArrayList<>();
        Field[] fields = getAllFields(t);
        for (Field field : fields) {
            field.setAccessible(true); // 私有属性必须设置访问权限
            T e = field.getAnnotation(type);
            if (e == null) {
                continue;
            }
            list.add(e);
        }
        return list;
    }


    public static <T extends Annotation, E> T getClassAnnotations(E obj, Class<T> clazz) {
        return obj.getClass().getAnnotation(clazz);
    }
}
