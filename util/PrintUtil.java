package com.bilibili.util;

import java.io.*;

/**
 * 用于打印消息和写入日记的类
 * 所有控制台的消息都在此输出
 *
 * @author Dex
 */
public final class PrintUtil {

    private static PrintWriter log;

    private enum LevelName {
        INFO(INFO_NAME, INFO_SWITCH, INFO_LEVEL),
        WARN(WARN_NAME, WARN_SWITCH, WARN_LEVEL),
        DEBUG(DEBUG_NAME, DEBUG_SWITCH, DEBUG_LEVEL),
        ERROR(ERROR_NAME, ERROR_SWITCH, ERROR_LEVEL),
        FATAL(FATAL_NAME, FATAL_SWITCH, FATAL_LEVEL);

        private final String name;
        private final boolean print;
        private final int level;

        private LevelName(String name, boolean print, int level) {
            this.name = name;
            this.print = print;
            this.level = level;
        }
    }

    private static final String INFO_NAME = "INFO";
    private static final String WARN_NAME = "WARN";
    private static final String DEBUG_NAME = "DEBUG";
    private static final String ERROR_NAME = "ERROR";
    private static final String FATAL_NAME = "FATAL";

    private static final boolean INFO_SWITCH = true;
    private static final boolean WARN_SWITCH = true;
    private static final boolean DEBUG_SWITCH = true;
    private static final boolean ERROR_SWITCH = true;
    private static final boolean FATAL_SWITCH = true;

    private static final int INFO_LEVEL = 1;
    private static final int WARN_LEVEL = 3;
    private static final int DEBUG_LEVEL = 3;
    private static final int ERROR_LEVEL = 6;
    private static final int FATAL_LEVEL = 8;

    /*
        静态创建文件,如果文件存在会将原来的文件删除，再创建一个新的文件
        默认在文本后面写内容,使用打印流输出,且设置自动刷新缓冲区
     */
    static {
        try {
            File file = new File("log.txt");
            if (file.exists()) file.delete();
            log = new PrintWriter(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //私有化构造器，防止实例化对象
    private PrintUtil() {
    }

    public static final void info(Object cont) {
        check(LevelName.INFO, cont, false);
    }

    public static final void warn(Object cont) {
        check(LevelName.WARN, cont, true);
    }

    public static final void debug(Object cont) {
        check(LevelName.DEBUG, cont, false);
    }

    public static final void error(Object cont) {
        check(LevelName.ERROR, cont, true);
    }

    public static final void fatal(Object cont) {
        check(LevelName.FATAL, cont, true);
    }

    /**
     * 判断是否打印，以及是否为err打印，和是否写入文件
     * 如果为err，则使用System.err打印,否则用System.out打印
     * 只有消息等级>=3，才会写入文件
     *
     * @param level   打印等级
     * @param cont    打印内容
     * @param isError 是否为err
     */
    private static final void check(LevelName level, Object cont, boolean isError) {
        if (level.print) {      //如果当前消息等级的打印为true，打印
            String text = stitching(level, cont);   //组装打印内容
            if (isError) printError(text);          //如果为err，则使用System.err打印,否则用System.out
            else printInfo(text);

            if (level.level >= 3) writeLog(text);     //如果消息等级大于3，则写入日记文件
        }
    }


    private static final void printInfo(String cont) {
        System.out.println(cont);
    }

    private static final void printError(String cont) {
        System.err.println(cont);
    }

    private static final void writeLog(String cont) {
        log.write(cont);
        log.flush();
    }

    /**
     * 拼接时间+线程名+消息等级的字符串并返回
     *
     * @return
     */
    private static final String stitching(LevelName level, Object cont) {
        String time = insert(TimeUtil.getDate()) + " "; //获取时间信息
        String thread = insert(Thread.currentThread().getName() + "/" + level.name) + ":";   //获取线程名
        return time + thread + cont.toString();  //拼接时间,线程名,打印文本
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
