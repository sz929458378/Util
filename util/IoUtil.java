package com.bilibili.util;

import com.bilibili.storage.FileStorageService;
import com.bilibili.storage.IStorageService;
import com.bilibili.storage.StorageService;

import java.io.*;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * input，output 工具类
 * 集成了用于输入输出等内容的方法
 *
 * @author Dex
 */
public class IoUtil {

    /**
     * 关闭流，需要实现接口AutoCloseble
     *
     * @param closeables
     */
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            try {
                if (c == null) continue;
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过文件路径反序列化获取类
     * 返回存储文件中的所有对象集合
     *
     * @param filePath
     * @return
     */
    public static List readAllObj(String filePath) {
        return readAllObj(new File(filePath));
    }

    /**
     * 通过文件(File)对象,通过反序列化获取类。
     * 返回存储文件中的所有对象集合
     *
     * @param file
     * @return
     */
    public static List readAllObj(File file) {
        List lists = new LinkedList();
        ObjectInputStream ois = null;

        if (file.exists() && !file.isDirectory()) {     //如果文件存在且不是文件夹,则读取文件中的对象
            try {
                ois = new ObjectInputStream(new FileInputStream(file)); //初始化输入流
                while (true) {
                    try {
                        Object value = ois.readObject();    //读取对象,如果出现异常，则直接跳出循环,利用这点判断是否读取完
                        lists.add(value);                   //将对象添加到集合
                    } catch (ClassNotFoundException e) {
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // e.printStackTrace();
            } finally {
                IoUtil.close(ois);
            }
        }

        return lists;
    }

    /**
     * 通过反序列化获取类存储到其他存储接口
     * 例如 Mysql,Excel...
     *
     * @param filePath       序列化文件名
     * @param storageService 存储数据服务
     */
    public static void fileToStorage(String filePath, IStorageService storageService) {
        if (storageService instanceof FileStorageService) throw new StackOverflowError();   //如果实例接口为序列化实例类，则跳出内存溢出异常
        List data = readAllObj(filePath);   //获取文件中的对象
        storageService.write(data);         //写入实例化的存储接口
        //如果实例对象为带有缓冲区的实例类，则刷新此对象的缓冲区
        if (storageService instanceof StorageService) ((StorageService) storageService).flush();
    }

    /**
     * 对象数组获取对应实例对象
     * 通过变量查找数组对应的变量，以获取实例对象
     * 如果没有此内容返回null
     *
     * @param array     要查找的数组
     * @param fieldName 变量名称
     * @param cont      变量内容
     * @return
     */
    public static Object getValue(Object[] array, String fieldName, Object cont) {
        try {
            Field f = array[0].getClass().getDeclaredField(fieldName);  //获取成员变量对象
            for (Object v1 : array) {
                Object value = f.get(v1);
                if (value == cont) return v1;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

}
