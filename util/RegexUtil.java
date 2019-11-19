package com.bilibili.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author Dex
 */
public class RegexUtil {
    //存储正则表达式的实例对象，key->表达式,value->正则对象
    private static Map<String, Pattern> patternMap = new HashMap<>();

    public static String regex(String cont, String regex) {
        return regex(cont, regex, 0);
    }

    public static String regex(String cont, String regex, int group) {
        if (!patternMap.containsKey(regex)) patternMap.put(regex, Pattern.compile(regex));

        if (cont != null) {
            Matcher matcher = patternMap.get(regex).matcher(cont);
            if (matcher.find()) return matcher.group(group);
        }

        return null;
    }

}