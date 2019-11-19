package com.bilibili.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date转换时间工具类
 *
 * @author Dex
 */
public class TimeUtil {

    /**
     * 默认字符串格式
     */
    private static String pattern = "HH:mm:ss";

    /**
     * 字符串格式
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat();

    /**
     * 将time(时间毫秒)转成pattern格式的日期字符串
     *
     * @param pattern
     * @param time
     * @return
     */
    public synchronized static String getDate(String pattern, long time) {
        sdf.applyPattern(pattern);
        return sdf.format(time);
    }

    /**
     * 获取默认格式的日期字符串
     */
    public static String getDate() {
        return getDate(pattern, System.currentTimeMillis());
    }

    /**
     * 将date类转成默认格式的日期字符串
     *
     * @param date
     */
    public static String getDate(Date date) {
        return getDate(pattern, date.getTime());
    }

    /**
     * 获取当前时间的指定格式日期字符串
     *
     * @param pattern
     */
    public static String getDate(String pattern) {
        return getDate(pattern, System.currentTimeMillis());
    }

    /**
     * 线程休眠时间
     *
     * @param time
     */
    public static void sleep(long time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
