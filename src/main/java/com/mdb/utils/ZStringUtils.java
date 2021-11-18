package com.mdb.utils;

import java.util.Objects;

public class ZStringUtils {
    /**
     * 判断字符串是否为空
     *
     * @param string
     */
    public static boolean isEmpty(String string) {
        if (string == null || "".equals(string.trim())) {
            return true;
        }
        return false;
    }

    public static boolean eq(String a, String b) {
        return Objects.equals(a, b);
    }
}
