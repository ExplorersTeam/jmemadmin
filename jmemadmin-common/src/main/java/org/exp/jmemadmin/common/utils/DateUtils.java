package org.exp.jmemadmin.common.utils;

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

    public static long getSecondTimeDifference(String beforeTime, String afterTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        long second;
        try {
            Date beforeDate = dateFormat.parse(beforeTime);
            Date afterDate = dateFormat.parse(afterTime);
            long diff = afterDate.getTime() - beforeDate.getTime(); // 获取时间差
            long day = diff / (24 * 60 * 60 * 1000);
            long hour = (diff / (60 * 60 * 1000) - day * 24);
            long minute = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            second = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);
            System.out.println(day + "天" + hour + "小时" + minute + "分" + second + "秒");
        } catch (Exception e) {
            second = -1L;
            e.printStackTrace();
        }
        return second;
    }

}
