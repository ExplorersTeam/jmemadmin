package org.exp.jmemadmin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {
    private DateUtils() {
        // Do nothing.
    }

    public static String getNowTime() {
        return timestampToDate(System.currentTimeMillis());
    }

    public static String timestampToDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    public static String getNowTimeHM() {
        // new Date()为获取当前系统时间。
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

}
