package com.hzy.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

public class DateUtil {
    public static SimpleDateFormat sdf = null;

    public static final String FORMAT_YEAR = "yyyy";
    public static final String FORMAT_MONTH = "yyyy-MM";
    public static final String FORMAT_MONTH2 = "yyyyMM";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATE_SLASH = "yyyy/MM/dd";
    public static final String FORMAT_TIME = "hh:mm:ss";
    public static final String FORMAT_TIME2 = "hhmmss";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIME2 = "yyyyMMddHHmmss";
    public static final String FORMAT_DATETIME3 = "MM-dd HH:mm:ss";
    public static final String FORMAT_DATEHOUR = "yyyy-MM-dd HH";
    public static final String FORMAT_DATEHOUR2 = "yyyyMMddHH";
    public static final String FORMAT_DATE2 = "yyyyMMdd";
    public static final String FORMAT_DATEMINUTE2 = "yyyyMMddHHmm";
    public static final String FORMAT_DATE_CJ = "MM/dd/yyyy";
    public static final String FORMAT_HOUR = "HH";

    private static void setSDF() {
        TimeZone tz = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-0"));
        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        TimeZone.setDefault(tz);
    }

    public static String StrDate(String format, Date date) {
        SimpleDateFormat ff = new SimpleDateFormat();
        ff.applyPattern(format);
        return ff.format(date);
    }

    /**
     * @param date
     * @return Date
     */
    public static Date getNextNYear(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, year + n);
        return calendar.getTime();
    }

    /**
     * @param date
     * @return Date
     */
    public static Date getNextNDate(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day + n);
        return calendar.getTime();
    }

    /**
     * @param date
     * @return Date
     */
    public static Date getPreviousNDate(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - n);
        return calendar.getTime();
    }

    public static Date getFirstDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minDate = calendar.getActualMinimum(Calendar.DATE);
        calendar.set(Calendar.DATE, minDate);
        return calendar.getTime();
    }

    public static Date getLastDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int maxDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, maxDate);
        return calendar.getTime();
    }

    public static Date getCurMonth() {
        return getNextNMonth(new Date(), 0);
    }

    public static Date getNextNMonth(int n) {
        return getNextNMonth(new Date(), n);
    }

    public static Date getNextNMonth(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, n);
        return calendar.getTime();
    }

    public static Date getNextNDayOfMonth(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, n);
        return calendar.getTime();
    }

    public static String getCurMonthStr() {
        return StrDate(FORMAT_MONTH, getCurMonth());
    }

    public static String getNextNMonthStr(int n) {
        return StrDate(FORMAT_MONTH, getNextNMonth(n));
    }

    public static String getNextNMonthStr(Date date, int n) {
        return StrDate(FORMAT_MONTH, getNextNMonth(date, n));
    }

    public static String getCurDateStr() {
        return StrDate(FORMAT_DATE, new Date());
    }

    public static String getCurDateTimeStr() {
        return StrDate(FORMAT_DATETIME, new Date());
    }

    public static Date getPreviousNMonth(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0 - n);
        return calendar.getTime();
    }

    public static Date getPreviousNHour(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        calendar.set(Calendar.HOUR, hour - n);
        return calendar.getTime();
    }

    public static Date getPreviousNMinute(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, minute - n);
        return calendar.getTime();
    }

    public static Date getPreviousNSecond(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int second = calendar.get(Calendar.SECOND);
        calendar.set(Calendar.SECOND, second - n);
        return calendar.getTime();
    }

    public static Date getAppointDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, day);
        return calendar.getTime();
    }

    public static String dateplus(String from, int n) {
        Date date = StrUtil.string2Date(FORMAT_DATE, from);
        Date d = getPreviousNDate(date, n);
        return StrUtil.date2String(FORMAT_DATE, d);
    }

    public static String dateplus(String from, int n, int type) {
        if (from.length() == 10) {
            from += " 00:00:00";
        }
        Date date = StrUtil.string2Date(FORMAT_DATETIME, from);
        Date d = new Date();
        if (type == Calendar.MONTH) {
            d = getPreviousNMonth(date, n);
        } else if (type == Calendar.HOUR) {
            d = getPreviousNHour(date, n);
        } else if (type == Calendar.MINUTE) {
            d = getPreviousNMinute(date, n);
        } else if (type == Calendar.SECOND) {
            d = getPreviousNSecond(date, n);
        } else {
            d = getPreviousNDate(date, n);
        }
        return StrUtil.date2String(FORMAT_DATETIME, d);
    }

    public static Map<String, Integer> getDateDetail(String d) {
        Map<String, Integer> ret = new HashMap<String, Integer>();
        if (d.length() == 10) {
            d += " 00:00:00";
        }
        Date dd = StrUtil.string2Date(FORMAT_DATETIME, d);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        ret.put("year", year);
        ret.put("month", month);
        ret.put("date", date);
        ret.put("hour", hour);
        ret.put("minute", minute);
        ret.put("second", second);
        ret.put("week", week);
        return ret;
    }

    /**
     * @param from
     * @param to
     * @return
     */
    public static Map dateScopeMap(String from, String to) {
        Map map = new LinkedHashMap();
        map.put(from, null);
        if (from.length() == 4) {
            int i = 1;
            Date d = StrUtil.string2Date(FORMAT_YEAR, from);
            while (d.getTime() < StrUtil.string2Date(FORMAT_YEAR, to).getTime()) {
                d = getNextNYear(StrUtil.string2Date(FORMAT_YEAR, from), i);
                map.put(StrUtil.date2String(FORMAT_YEAR, d), null);
                i++;
            }
        } else if (from.length() == 7) {
            int i = 1;
            Date d = StrUtil.string2Date("yyyy-MM", from);
            while (d.getTime() < StrUtil.string2Date("yyyy-MM", to).getTime()) {
                d = getPreviousNMonth(StrUtil.string2Date("yyyy-MM", from), -i);
                map.put(StrUtil.date2String("yyyy-MM", d), null);
                i++;
            }
        } else {
            int i = 1;
            Date d = StrUtil.string2Date(FORMAT_DATE, from);
            while (d.getTime() < StrUtil.string2Date(FORMAT_DATE, to).getTime()) {
                d = getNextNDate(StrUtil.string2Date(FORMAT_DATE, from), i);
                map.put(StrUtil.date2String(FORMAT_DATE, d), null);
                i++;
            }
        }
        return map;
    }

    public static List<String> dateScope(String from, String to) {
        List<String> list = new ArrayList<String>();
        list.add(from);
        if (from.length() == 4) {
            int i = 1;
            Date d = StrUtil.string2Date(FORMAT_YEAR, from);
            while (d.getTime() < StrUtil.string2Date(FORMAT_YEAR, to).getTime()) {
                d = getNextNYear(StrUtil.string2Date(FORMAT_YEAR, from), i);
                list.add(StrUtil.date2String(FORMAT_YEAR, d));
                i++;
            }
        } else if (from.length() == 7) {
            int i = 1;
            Date d = StrUtil.string2Date(FORMAT_MONTH, from);
            while (d.getTime() < StrUtil.string2Date(FORMAT_MONTH, to).getTime()) {
                d = getPreviousNMonth(StrUtil.string2Date(FORMAT_MONTH, from), -i);
                list.add(StrUtil.date2String(FORMAT_MONTH, d));
                i++;
            }
        } else {
            int i = 1;
            Date d = StrUtil.string2Date(FORMAT_DATE, from);
            while (d.getTime() < StrUtil.string2Date(FORMAT_DATE, to).getTime()) {
                d = getNextNDate(StrUtil.string2Date(FORMAT_DATE, from), i);
                list.add(StrUtil.date2String(FORMAT_DATE, d));
                i++;
            }
        }
        return list;
    }

    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek <= 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek <= 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    public static String getStartDateOfWeek() {
        int dayOfWeek = getDayOfWeek();
        Date today = new Date();
        Date date = getNextNDate(today, 1 - dayOfWeek);
        return StrDate(FORMAT_DATE, date);
    }

    public static String getNextStartDateOfWeek() {
        int dayOfWeek = getDayOfWeek();
        Date today = new Date();
        Date date = getNextNDate(today, 1 - dayOfWeek + 7);
        return StrDate(FORMAT_DATE, date);
    }

    public static String getStartDateOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return StrDate(FORMAT_DATE, cal.getTime());
    }

    public static String getNextBidDate() {
        int dayOfWeek = getDayOfWeek();
        Date today = new Date();
        Date date;
        if (dayOfWeek < 5)
            date = getNextNDate(today, 5 - dayOfWeek);
        else {
            date = getNextNDate(today, 5 - dayOfWeek + 7);
        }
        return StrDate(FORMAT_DATE, date);
    }

    public static String getCurrentBidInternal() {
        int dayOfWeek = getDayOfWeek();
        Date today = new Date();
        Date startDate;
        Date endDate;
        if (dayOfWeek < 5) {
            startDate = getNextNDate(today, 1 - dayOfWeek);
            endDate = getNextNDate(today, 7 - dayOfWeek);
        } else {
            startDate = getNextNDate(today, 1 - dayOfWeek + 7);
            endDate = getNextNDate(today, 7 - dayOfWeek + 7);
        }
        return StrDate(FORMAT_DATE, startDate) + "~" + StrDate(FORMAT_DATE, endDate);
    }

    public static String getCurScribeDateStr() {
        return getCurDateStr().replaceAll("-", "/") + "/";
    }

    public static Long getZeroTS() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static Date setDateToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date setHour(Date date, int value) {
        Date tmpDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tmpDate);
        calendar.set(Calendar.HOUR_OF_DAY, value);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date setHourMinute(Date date, int hour, int minute) {
        Date tmpDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tmpDate);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean dateSame(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return StrUtil.date2String(FORMAT_DATE, date1).equals(StrUtil.date2String(FORMAT_DATE, date2));
    }

    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            return true;
        }
        if (curTime == null || !curTime.contains(":")) {
            return true;
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end == start) {
                return true;
            } else if (end < start) {
                if (now > end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now <= end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            return true;
        }

    }

    // public static Date parseDateSrcFormat(String timeSrc,String format,String key){
    // Date date;
    // try {
    // SimpleDateFormat sdf = new SimpleDateFormat(format);
    // date=sdf.parse(timeSrc);
    // } catch (Exception e) {
    // throw new ParamInvalidException(key, timeSrc, "日期必须是"+format+"的格式");
    // }
    // return date;
    // }

    public static Date parseDateSrcFormat(String timeSrc, String format) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(timeSrc);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
        return date;
    }

    public static long setDateHourAndSoToZero(long time) {
        Calendar startZeroDate = Calendar.getInstance();
        startZeroDate.setTimeInMillis(time);
        startZeroDate.set(Calendar.HOUR_OF_DAY, 0);
        startZeroDate.set(Calendar.MINUTE, 0);
        startZeroDate.set(Calendar.SECOND, 0);
        startZeroDate.set(Calendar.MILLISECOND, 0);
        return startZeroDate.getTimeInMillis();
    }

    public static long getWeekMonday(long millis) {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(millis);
        // 设置一周的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long getFirstDayOfMonth(long millis) {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(millis);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long getLastDayOfMonth(long millis) {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(millis);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long getFirstDayOfQuarter(long millis) {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(millis);

        int month = cal.get(Calendar.MONTH);
        int firstMonth = month / 3 * 3;

        cal.set(Calendar.MONTH, firstMonth);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long getLastDayOfQuarter(long millis) {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(millis);

        int month = cal.get(Calendar.MONTH);
        int lastMonth = month / 3 * 3 + 2;

        cal.set(Calendar.MONTH, lastMonth);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    /**
     * 获取n个小时前整点时间
     * 
     * @param date
     * @return
     */
    public static Date getSpecialWholeHourTime(Date date, int n) {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - n);
        ca.set(Calendar.MILLISECOND, 0);
        return ca.getTime();
    }

    public static Date getLastBalanceTimeNode4D0() {
        int[] nodes = new int[] { 24, 22, 20, 18, 16, 14, 12, 10, 8, 6, 4, 2, 0 };
        int hour = LocalTime.now().getHour();
        int last = 0;
        for (int node : nodes) {
            if (hour > node) {
                last = node;
                break;
            }
        }
        if (last != 0)
            return DateUtil.setHour(new Date(), last);
        else return DateUtil.getSpecialWholeHourTime(new Date(), 24 - nodes[0] + hour);
    }
}
