package com.bilibili.util;

/**
 * 十进制英文相互转换
 * 例如:1=A,27=AA
 *
 * @author Dex
 */
public class ChangeNumEngUtil {

    //将数字转成英文
    public static String ChangeEnglish(int number) {
        String english = "";
        while (number > 0) {
            int m = number % 26;
            if (m == 0) m = 26;
            english = (char) (m + 64) + english;
            number = (number - m) / 26;
        }
        return english;
    }

    //将英文转成数字
    public static int ChangeNumber(String english) {
        int number = 0;
        char[] value = english.toCharArray();
        for (int i = 0; i < value.length; i++) {
            number += (value[i] - 64) * Math.pow(26, i);
        }

        return number;
    }


}
