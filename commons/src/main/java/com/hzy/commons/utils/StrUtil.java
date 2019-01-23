package com.hzy.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;

public class StrUtil {

    public static boolean isEmpty(String s) {
        return StringUtils.isEmpty(s);
    }

    public static boolean isEmpty(Object object) {
        if (object == null || StringUtils.isBlank(object.toString())) {
            return true;
        }
        return false;
    }

    public static boolean empty(Object s) {
        if (s == null || s.toString().trim().equals("")) {
            return true;
        }
        return false;

    }

    public static boolean isBlank(String s) {
        return StringUtils.isBlank(s);
    }

    public static int len(String s) {
        int length = StringUtils.length(s);
        return length;
    }

    public static String toStr(Object obj) {
        String str = "";
        if (null != obj) {
            str = obj.toString();
        }
        return str;
    }

    public static long parseLong(String s) {
        return parseLong(s, 0L);
    }

    public static long parseLongObject(Object o) {
        if (null == o) {
            return 0L;
        }
        return parseLong(o.toString());
    }

    public static long parseLong(String s, long iDefault) {
        if (s == null || s.equals("")) {
            return iDefault;
        }
        try {
            s = s.replaceAll(",", "");
            int l = s.indexOf(".");
            if (l > 0) {
                s = s.substring(0, l);
            }
            return Long.parseLong(s);
        } catch (Exception e) {
            return iDefault;
        }
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static int parseIntObject(Object o) {
        if (null == o) {
            return 0;
        }
        return parseInt(o.toString());
    }

    public static int parseInt(String s, int iDefault) {
        if (s == null || s.equals("")) {
            return iDefault;
        }
        if (s.equals("true")) {
            return 1;
        }
        if (s.equals("false")) {
            return 0;
        }
        try {
            s = s.replaceAll(",", "");
            int l = s.indexOf(".");
            if (l > 0) {
                s = s.substring(0, l);
            }
            return Integer.parseInt(s);
        } catch (Exception e) {
            return iDefault;
        }
    }

    public static String date2String(String pattern, Date date) {
        String retStr = "";
        java.text.SimpleDateFormat ff = new java.text.SimpleDateFormat();
        ff.applyPattern(pattern);
        retStr = ff.format(date);
        return retStr;
    }

    public static Date string2Date(String pattern, String str) {
        Date date = new Date();
        if (StrUtil.isEmpty(str)) {
            return date;
        }
        java.text.SimpleDateFormat ff = new java.text.SimpleDateFormat();
        ff.applyPattern(pattern);
        try {
            date = ff.parse(str);
        } catch (ParseException e) {
        }
        return date;
    }
}
