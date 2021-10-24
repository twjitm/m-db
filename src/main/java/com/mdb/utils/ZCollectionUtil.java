package com.mdb.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author twjitm
 */
public class ZCollectionUtil {

    /**
     * 判断某个list是否没有数据
     */
    public static <T> boolean isEmpty(List<T> list) {
        boolean b = false;
        if (list == null || list.isEmpty()) {
            b = true;
        }
        return b;
    }


    /**
     * 判断某个list是否没有数据
     */
    public static <T> boolean isEmpty(Set<T> list) {
        boolean b = false;
        if (list == null || list.isEmpty()) {
            b = true;
        }
        return b;
    }


    public static <T> boolean isEmpty(T[] list) {
        boolean b = false;
        if (list == null || list.length == 0) {
            b = true;
        }
        return b;
    }


    public static boolean inSet(Set<Long> ms, long l) {
        if (ms == null) {
            return false;
        }
        for (long m : ms) {
            if (m == l) {
                return true;
            }
        }
        return false;
    }

    public static long size(Collection<?> c) {
        if (c == null) {
            return 0;
        }
        return c.size();
    }

    public static <T> boolean isempty(Map<T, T> map) {
        boolean b = false;
        if (map == null || map.isEmpty()) {
            b = true;
        }
        return b;
    }

    public static <T, E> boolean isEmpty(Map<T, E> map) {

        boolean b = false;
        if (map == null || map.isEmpty()) {
            b = true;
        }
        return b;
    }
}
