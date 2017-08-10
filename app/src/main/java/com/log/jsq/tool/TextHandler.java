package com.log.jsq.tool;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.log.jsq.library.FuHao;

public class TextHandler {

    private static final String lineFeed = "\n";

    /**
     * 对文本添加样式（如变色等）
     * @param text      文本
     * @param color     变色颜色
     * @return          带样式的文本
     */
    public static Spanned setStyle(@NonNull String text, int color) {
        SpannableString ss = new SpannableString(text);
        if (text.length() == 0) {
            return ss;
        }

        //需要变色的符号（字符串数组）
        String strs[] = new String[] {
                FuHao.jia,
                FuHao.jian,
                FuHao.cheng,
                FuHao.chu,
                FuHao.kuoHaoTou,
                FuHao.kuoHaoWei,
                FuHao.dengYu
        };

        // 外循环 - 对符号
        for (String cs : strs) {
            // 内循环 - 对文本
            for (int ti = 0; true; ) {
                // 找到可变色符号的位置
                ti = text.indexOf(cs, ti);

                if (ti < 0) {
                    // 找不到则跳出循环，开始从下一个符号找
                    break;
                } else {
                    // 检测负号
                    if (cs.equals(FuHao.jian) && jianCeFu(text, ti)) {
                        ti += FuHao.jian.length();
                        continue;
                    }
                    if (cs.equals(FuHao.jia) && jianCeZheng(text, ti)) {
                        ti += FuHao.jia.length();
                        continue;
                    }

                    // 设置颜色
                    ss.setSpan(new ForegroundColorSpan(color),
                            ti,
                            ti + cs.length(),
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    ti += cs.length();
                }
            }
        }

        return ss;
    }

    /**
     * 自动换行
     * @param str                   需要换行的字符串
     * @param addOn_jiaJian         是否在加减前换行
     * @param addOn_chengChu        是否在乘除前换行
     * @param addOn_kuoHaoNei       允许在括号内换行
     * @return                      添加换行后的字符串
     */
    public static String addLineFeed(String str, boolean addOn_jiaJian, boolean addOn_chengChu, boolean addOn_kuoHaoNei) {
        StringBuffer strBuf = new StringBuffer(str);

        if (addOn_jiaJian) {
            addLineFeedRun(strBuf, FuHao.jia, addOn_kuoHaoNei);
            addLineFeedRun(strBuf, FuHao.jian, addOn_kuoHaoNei);
        }
        if (addOn_chengChu) {
            addLineFeedRun(strBuf, FuHao.cheng, addOn_kuoHaoNei);
            addLineFeedRun(strBuf, FuHao.chu, addOn_kuoHaoNei);
        }

        int dengYuIndex = strBuf.lastIndexOf(FuHao.dengYu);
        if (dengYuIndex >= 0) {
            strBuf.insert(dengYuIndex+FuHao.dengYu.length(), " ");
            strBuf.insert(dengYuIndex, lineFeed);
        }

        return strBuf.toString();
    }

    /**
     * 检测加号是否为正号
     * @param text      文本
     * @param index     出现的位置
     * @return          结果
     */
    private static boolean jianCeZheng(String text, int index) {
        final int indexUp = index - FuHao.TEN_POWER.length();                       //假设正号存在时，用“正号”的位置推出“十次方+正号”的位置。

        return (text.indexOf(FuHao.TEN_POWER + FuHao.jia, indexUp) == indexUp);    //判断“十次方+正号”的位置是否匹配
    }

    /**
     * 检测加号是否为正号（重载）
     * @param text      文本
     * @param index     出现的位置
     * @return          结果
     */
    private static boolean jianCeZheng(StringBuffer text, int index) {
        final int indexUp = index - FuHao.TEN_POWER.length();                       //假设正号存在时，用“正号”的位置推出“十次方+正号”的位置。

        return (text.indexOf(FuHao.TEN_POWER + FuHao.jia, indexUp) == indexUp);    //判断“十次方+正号”的位置是否匹配
    }

    /**
     * 检测减号是否为负号
     * @param text      文本
     * @param index     出现的位置
     * @return          结果
     */
    private static boolean jianCeFu(String text, int index) {
        final int indexUp1 = index - FuHao.kuoHaoTou.length();                       //假设负号存在时，用“负号”的位置推出“括号头+负号”的位置。
        final int indexUp2 = index - FuHao.dengYu.length();                       //假设负号存在时，用“负号”的位置推出“等于+负号”的位置。

        return text.indexOf(FuHao.kuoHaoTou + FuHao.jian, indexUp1) == indexUp1    //判断“括号头+负号”的位置是否匹配
                || text.indexOf(FuHao.dengYu  +  FuHao.jian, indexUp2) == indexUp2    //判断“等于+负号”的位置是否匹配
                || text.indexOf(FuHao.dengYu + " " + FuHao.jian, indexUp2 - 1) == indexUp2 - 1
                || index == 0;
    }

    /**
     * 检测减号是否为负号（重载）
     * @param text      文本
     * @param index     出现的位置
     * @return          结果
     */
    private static boolean jianCeFu(StringBuffer text, int index) {
        final int indexUp1 = index - FuHao.kuoHaoTou.length();                       //假设负号存在时，用“负号”的位置推出“括号头+负号”的位置。
        final int indexUp2 = index - FuHao.dengYu.length();                       //假设负号存在时，用“负号”的位置推出“等于+负号”的位置。

        return text.indexOf(FuHao.kuoHaoTou + FuHao.jian, indexUp1) == indexUp1    //判断“括号头+负号”的位置是否匹配
                || text.indexOf(FuHao.dengYu + FuHao.jian, indexUp2) == indexUp2    //判断“等于+负号”的位置是否匹配
                || text.indexOf(FuHao.dengYu + " " + FuHao.jian, indexUp2 - 1) == indexUp2 - 1
                || index == 0;
    }

    /**
     * 判断算式指定位置的括号级别是否为0
     * @param strBuf    字符串
     * @param endIndex  指定位置
     * @return          判断算式指定位置的括号级别是否为0
     */
    public static boolean isParenthesesClosed(StringBuffer strBuf, int endIndex) {
        if (endIndex > strBuf.length()) {
            throw new IndexOutOfBoundsException("strBuf.length = " + strBuf.length() + ", endIndex = " + endIndex);
        }

        int touLen = 0;
        int weiLen = 0;
        int i;

        i = strBuf.indexOf(FuHao.kuoHaoTou);
        while (i >= 0) {
            if (i > endIndex) {
                break;
            } else {
                touLen++;
                i = strBuf.indexOf(FuHao.kuoHaoTou, i + 1);
            }
        }

        i = strBuf.indexOf(FuHao.kuoHaoWei);
        while (i >= 0) {
            if (i > endIndex) {
                break;
            } else {
                weiLen++;
                i = strBuf.indexOf(FuHao.kuoHaoWei, i + 1);
            }
        }

        return touLen == weiLen;
    }

    /**
     * 判断算式指定位置的括号级别是否为0（重载）
     * @param str       字符串
     * @param endIndex  指定位置
     * @return          判断算式指定位置的括号级别是否为0
     */
    public static boolean isParenthesesClosed(String str, int endIndex) {
        return isParenthesesClosed(new StringBuffer(str), endIndex);
    }

    /**
     * 添加换行
     * @param strBuf            需要添加换行符的文本
     * @param targetStr         在此字符串前添加换行
     * @param addOn_kuoHaoNei   是否允许在括号内换行
     */
    private static void addLineFeedRun(@NonNull StringBuffer strBuf, @NonNull String targetStr, boolean addOn_kuoHaoNei) {
        for (int i = strBuf.lastIndexOf(targetStr);
             i >= 0 && i < strBuf.length();
             i = strBuf.lastIndexOf(targetStr, i)) {

            if (targetStr.equals(FuHao.jian) && jianCeFu(strBuf, i)
                    || targetStr.equals(FuHao.jia) && jianCeZheng(strBuf, i)) {
                i--;
                continue;
            }

            if (addOn_kuoHaoNei) {
                strBuf.insert(i, lineFeed);
            } else if (isParenthesesClosed(strBuf, i)) {
                strBuf.insert(i, lineFeed);
            } else {
                i--;
            }
        }
    }

}
