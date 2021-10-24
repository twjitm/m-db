package com.mdb.utils;

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
}
