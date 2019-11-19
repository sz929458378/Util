package com.bilibili.util;

import sun.applet.Main;

import java.io.*;

/**
 * 用于打印消息和写入日记的类
 * 所有控制台的消息都在此输出
 * level消息等级,cont打印内容
 * <p>
 * 以下不包含声明等级
 * 大于2级打印，大于4级写入文件
 * levelName默认为INFO大于6级为WARN
 *
 * @author Dex
 */
public final class PrintUtil {

    private static PrintWriter write;

    /*
        静态创建文件,如果文件存在会将原来的文件删除，再创建一个新的文件
        默认在文本后面写内容,使用打印流输出,且设置自动刷新缓冲区
     */
    static {
        try {
            File file = new File("log.txt");
            if (file.exists()) file.delete();
            write = new PrintWriter(new FileOutputStream(file, true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public final static void print(int level, Object cont) {
        if (level > 2) printCont(level, cont, false);
    }

    public final static void println(int level, Object cont) {
        if (level > 2) printCont(level, cont, true);
    }

    /**
     * 根据消息等级，是否换行，来打印文本到控制台及是否写入文件
     *
     * @param level 消息等级
     * @param cont  文本
     * @param line  换行
     */
    private final static void printCont(int level, Object cont, boolean line) {
        String levelName = "INFO";                      //默认消息等级为'INFO'
        if (level > 6) levelName = "WARN";              //如果消息等级大于6，则将消息等级改为'WARN'
        String time = insert(TimeUtil.getDate()) + " "; //获取时间信息
        String thread = insert(Thread.currentThread().getName() + "/" + levelName) + ":";   //获取线程名
        String text = time + thread + cont.toString();  //拼接时间,线程名,打印文本
        if (level > 4) write.println(text);             //如果消息等级大于4，则写入日记

        if (level > 6) {                                //如果消息等级大于6，则用err打印.具体效果为打印红色
            if (line) System.err.println(text);         //根据line属性觉得是否换行
            else System.err.println(text);
        } else {
            if (line) System.out.println(text);
            else System.out.println(text);
        }
    }

    /**
     * 将字符串用[]括起来
     *
     * @param cont 文本
     * @return
     */
    private final static String insert(String cont) {
        return "[" + cont + "]";
    }

}
