package com.mdb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ZTimeUtils {

    public static long SECOND = 1000;
    public static long MINUTE = 60 * SECOND;
    public static long FIVE_MINUTE = 5 * MINUTE;
    public static long ONE_HOUR = 60 * MINUTE;
    public static long SIX_HOUR = 6 * ONE_HOUR;
    public static long ONE_DAY = 24 * ONE_HOUR;
    //秒
    public static int ONE_DAY_SECOND = (int) (ONE_DAY / SECOND);
    public static int MINUTE_SECOND = (int) (MINUTE / SECOND);
    public static int FIVEMINUTE_SECOND = (int) (FIVE_MINUTE / SECOND);
    public static int TEN_MINUTE_SECOND = (int) (FIVE_MINUTE / SECOND) * 2;
    public static int HALF_HOUR = (int) (TEN_MINUTE_SECOND) * 3;
    public static int SIX_HOUR_SECOND = (int) (SIX_HOUR / SECOND);
    public static int ONE_HOUR_SECOND = (int) (ONE_HOUR / SECOND);

    public static final String DEFAULT_DATE_FAMAT = "yyyyMMdd";

    public static long getSeconds() {
        long now = System.currentTimeMillis();
        return now / SECOND;
    }

    /**
     * 获取当前时间小时数量
     */
    public static int getHours() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间小时数量
     */
    public static int getMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    public static int getWeekOfDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getWeekOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }


    public static String getDateString(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(c.getTime());

    }

    public static String getDateString(Date d, String formatString) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(c.getTime());
    }

    public static long getUnixTimeAddYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 1);
        return c.getTime().getTime() / 1000;
    }

    /**
     * 返回当年第几周
     * 2015-28
     */
    public static String getWeekOfYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "-" + c.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getBeforeWeek() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.WEEK_OF_YEAR) - 1);
    }

    /**
     * 返回当年第几天
     * 2015-28
     */
    public static String getDayOfYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "-" + c.get(Calendar.DAY_OF_YEAR);
    }

    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getNextDaySeconds() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 3);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date tomorrowDate = c.getTime();
        return (int) Math.abs((tomorrowDate.getTime() - date.getTime()) / 1000);

    }

    //返回20160422-H(24小時制)
    public static String getYMDH() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * return 2016-6
     */
    public static String getYearMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1);
    }

    public static int getDays(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int t1 = c1.get(Calendar.DAY_OF_YEAR);
        int t2 = c2.get(Calendar.DAY_OF_YEAR);
        return t2 - t1;
    }


    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date strtodate = formatter.parse(strDate);
            return strtodate;
        } catch (ParseException e) {
        }
        return new Date();
    }


    public static long Now() {
        return new Date().getTime();
    }

    /**
     * 上周 : -1
     * 本周 : 0
     * 下周 : 1
     */
    public enum WeekType {
        LAST_WEEK(-1),
        THIS_WEEK(0),
        NEXT_WEEK(1),
        ;

        int value;

        WeekType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    private static final int DELAY_TYPE_HOUR = 6;
    private static final int DELAY_TYPE_TOTAL_DAY = 5;
    private static final int DELAY_TYPE_TOTAL_HOUR = 0;
    private static final int DELAY_TYPE_TOTAL_MINUTE = 1;
    private static final int DELAY_TYPE_TOTAL_SECOND = 2;
    private static final int DELAY_TYPE_MINUTE = 3;
    private static final int DELAY_TYPE_SECOND = 4;
    private static final int ACTION_AFTER = 0;
    private static final int ACTION_BEFORE = 1;
    private static final int ACTION_TYPE_DAY = 0;
    private static final int ACTION_TYPE_HOUR = 1;
    private static final int ACTION_TYPE_MINUTE = 2;
    private static final int ACTION_TYPE_SECOND = 3;


    public static Date stringToDate(String stringValue, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        try {
            return sdf.parse(stringValue);
        } catch (ParseException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static Date stringToDate(String stringValue) {
        return stringToDate(stringValue, "yyyy-MM-dd HH:mm:ss");
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat time = new SimpleDateFormat(format);
        return time.format(date);
    }

    public static String dateToString(Date date) {
        return dateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String dateLongToString(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return dateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String dateLongToShortString(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return dateToString(calendar.getTime(), "MM-dd HH:mm");
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(7);
    }

    public static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(3);
    }

    public static int delay(Date date1, Date date2, int type) {
        long t1 = date1.getTime();
        long t2 = date2.getTime();
        long fenzi = 0L;
        if (t1 < t2) {
            fenzi = t2 - t1;
        } else {
            fenzi = t1 - t2;
        }

        long fenmu = 1000L;
        switch (type) {
            case 0:
                fenmu *= 3600L;
                break;
            case 1:
                fenmu *= 60L;
            case 2:
                break;
            case 3:
                fenzi %= 3600000L;
                fenmu *= 60L;
                break;
            case 4:
                fenzi %= 60000L;
                break;
            case 5:
                fenmu *= 86400L;
                break;
            case 6:
                fenzi %= 86400000L;
                fenmu *= 3600L;
                break;
            default:
                return 0;
        }

        return (int) (fenzi / fenmu);
    }

    public static int delayTotalDay(Date date1, Date date2) {
        return delay(date1, date2, 5);
    }

    public static int delayTotalHour(Date date1, Date date2) {
        return delay(date1, date2, 0);
    }

    public static int delayTotalMinute(Date date1, Date date2) {
        return delay(date1, date2, 1);
    }

    public static int delayTotalSecond(Date date1, Date date2) {
        return delay(date1, date2, 2);
    }

    public static int delayMinute(Date date1, Date date2) {
        return delay(date1, date2, 3);
    }

    public static int delaySecond(Date date1, Date date2) {
        return delay(date1, date2, 4);
    }

    public static Date getDate(Date date, int type, int action, int count) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        switch (type) {
            case 0:
                if (action == 0) {
                    now.set(5, now.get(5) + count);
                } else if (action == 1) {
                    now.set(5, now.get(5) - count);
                }
                break;
            case 1:
                if (action == 0) {
                    now.set(10, now.get(10) + count);
                } else if (action == 1) {
                    now.set(10, now.get(10) - count);
                }
                break;
            case 2:
                if (action == 0) {
                    now.set(12, now.get(12) + count);
                } else if (action == 1) {
                    now.set(12, now.get(12) - count);
                }
                break;
            case 3:
                if (action == 0) {
                    now.set(13, now.get(13) + count);
                } else if (action == 1) {
                    now.set(13, now.get(13) - count);
                }
        }

        return now.getTime();
    }

    public static Date getDateBefore(Date date, int day) {
        return getDate(date, 0, 1, day);
    }

    public static Date getDateAfter(Date date, int day) {
        return getDate(date, 0, 0, day);
    }

    public static Date getHourBefore(Date date, int hour) {
        return getDate(date, 1, 1, hour);
    }

    public static Date getHourAfter(Date date, int hour) {
        return getDate(date, 1, 0, hour);
    }

    public static Date getMinuteAfter(Date date, int minute) {
        return getDate(date, 2, 0, minute);
    }

    public static Date getMinuteBefore(Date date, int minute) {
        return getDate(date, 2, 1, minute);
    }

    public static Date getSecondBefore(Date date, int second) {
        return getDate(date, 3, 1, second);
    }

    public static Date getSecondAftere(Date date, int second) {
        return getDate(date, 3, 0, second);
    }

    public static boolean areSameDays(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return dateToString(date1, "yyyy-MM-dd").equals(dateToString(date2, "yyyy-MM-dd"));
        } else {
            return false;
        }
    }

    public static boolean isBetweenDays(Date date, Date date1, Date date2) {
        if (date != null && date1 != null && date2 != null) {
            if (date1.before(date2) && date.after(date1) && date.before(date2)) {
                return true;
            } else {
                return date.before(date1) && date.after(date2);
            }
        } else {
            return false;
        }
    }

}



