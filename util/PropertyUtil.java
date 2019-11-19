package com.bilibili.util;

import java.io.*;
import java.util.*;

/**
 * 加载配置文件工具类
 *
 * @author Dex
 */
public class PropertyUtil {

    //存储已读取的文件，key->文件名,vlaue->Properties对象
    public static HashMap<String, Properties> Map = new HashMap<>();

    /**
     * 获取Int类型的值,如果不是value不全是数字，返回-1
     *
     * @param key
     * @param filePath
     * @return
     */
    public static int getInt(String key, String filePath) {
        String value = getValue(key, filePath);
        return value != null ? Integer.parseInt(value) : -1;
    }

    /**
     * 获取布尔类型的值，只有value为true时，返回true
     *
     * @param key
     * @param filePath
     * @return
     */
    public static boolean getBoolean(String key, String filePath) {
        return Boolean.parseBoolean(getValue(key, filePath));
    }

    /**
     * 获取String类型的值
     *
     * @param key
     * @param filePath
     * @return
     */
    public static String getValue(String key, String filePath) {
        String[] file = filePath.split("/");
        String fileName = file[file.length - 1];

        if (!Map.containsKey(fileName)) {    //如果Map没有存储过这个文件，则读取这个文件，且将这个参数存储到Map中
            BufferedInputStream bis = null;
            try {
                Properties properties = new Properties();
                bis = new BufferedInputStream(new FileInputStream(filePath));
                properties.load(bis);
                Map.put(fileName, properties);
            } catch (IOException e) {
                // e.printStackTrace();
                PrintUtil.println(8, "读取" + filePath + "出现异常");
            } finally {
                IoUtil.close(bis);
            }
        }

        return Map.get(fileName).getProperty(key);
    }

    /**
     * 写入properties文件
     *
     * @param key      键
     * @param value    值
     * @param filePath 文件路径
     */
    public static void writeKV(String key, String value, String filePath) {
        BufferedOutputStream bos = null;
        try {
            Properties properties = new Properties();
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            properties.setProperty(key, value);
            properties.store(bos, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(bos);
        }

    }

}