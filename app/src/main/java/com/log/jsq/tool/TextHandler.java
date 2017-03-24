package com.log.jsq.tool;

import android.text.Html;
import android.text.Spanned;

import com.log.jsq.library.FuHao;

public class TextHandler {

    private static final String lineFeed = "\n";

    //文本变色（主）
    public static Spanned run(String text, int color) {
        /**
         * 【colorArray的存储方式】
         * r0:符号是否变色的标记（如果有则为开始位置）
         * r1:变色符号在strs的位置（获得变色符号长度）
         */
        int COLOR_ARRAY_TEXT_START_HANG = 0; //文本变色开始的位置的行数（适用于colorArray）
        int COLOR_ARRAY_STRS_INDEX_HANG = 1; //变色符号对应的位置的行数（适用于colorArray）
        int NOT_CHANGE_COLOR = -1;           //不变色的标记（适用于colorArray）
        StringBuffer textBuf = new StringBuffer(text);  //需要变色的算式
        final int colorArrayLie = text.length();        //colorArray的列数
        int[][] colorArray = new int[2][colorArrayLie]; //控制变色的数组

        String htmlStart = "<font color=\"#" + Integer.toString(color, 16) + "\">";    //HTML标签头（指定文本颜色）
        String htmlEnd = "</font>";    //HTML标签尾
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

        //初始化colorArray，标记所有元素为“不变色”
        for (int i=0;i<colorArray.length;i++) {
            for (int j=0;j<colorArrayLie;j++) {
                colorArray[i][j] = NOT_CHANGE_COLOR;
            }
        }

        //标记colorArray，标记所有要变色的元素为它的位置
        //外循环 - 对符号
        for (int strsIndex = 0;strsIndex < strs.length;strsIndex++) {
            //内循环 - 对文本
            for (int textIndex = 0;true;) {
                textIndex = textBuf.indexOf(strs[strsIndex], textIndex);    //找到可变色符号的位置

                if (textIndex < 0) {
                    break;  //找不到则跳出循环，开始从下一个符号找
                } else {
                    //检测负号
                    if (strs[strsIndex].equals(FuHao.jian) && jianCeFu(textBuf.toString(),textIndex)) {
                        textIndex += FuHao.jian.length();
                        continue;
                    }

                    colorArray[COLOR_ARRAY_TEXT_START_HANG][textIndex] = textIndex; //标记当前变色符号在 textBuf 中的开始位置
                    colorArray[COLOR_ARRAY_STRS_INDEX_HANG][textIndex] = strsIndex; //标记当前变色符号在 strs 中的位置（获得变色符号长度）
                    textIndex += strs[strsIndex].length();
                }
            }
        }

        //文本变色处理（参照colorArray）
        for (int index=0;index<colorArrayLie;index++) {
            final int textStartIndex = colorArray[COLOR_ARRAY_TEXT_START_HANG][index];  //变色符号 textBuf 中的开始位置
            final int strsIndex = colorArray[COLOR_ARRAY_STRS_INDEX_HANG][index];       //变色符号在 strs 中的位置

            if (textStartIndex == NOT_CHANGE_COLOR || strsIndex == NOT_CHANGE_COLOR) {
                continue;   //跳过不变色的元素
            }

            textBuf.insert(textStartIndex, htmlStart);                                                  //添加HTML标签头
            textBuf.insert(textStartIndex + htmlStart.length() + strs[strsIndex].length(), htmlEnd);    //添加HTML标签尾

            //更新当前符号以后的所有符号的在 textBuf 中的位置
            for (int afterIndex=index+1;afterIndex<colorArrayLie;afterIndex++) {
                if (colorArray[COLOR_ARRAY_TEXT_START_HANG][afterIndex] != NOT_CHANGE_COLOR
                        && colorArray[COLOR_ARRAY_STRS_INDEX_HANG][afterIndex] != NOT_CHANGE_COLOR) {
                    colorArray[COLOR_ARRAY_TEXT_START_HANG][afterIndex] += htmlStart.length() + htmlEnd.length();
                }
            }
        }

        //返回HTML的表示
        return Html.fromHtml(textBuf.toString().replaceAll("(\r\n|\r|\n|\n\r)", "<br/>"));
    }

    public static String addLineFeed(String str, boolean addOn_jiaJian, boolean addOn_chengChu, boolean addOn_kuoHaoNei) {
        StringBuffer strBuf = new StringBuffer(str.replaceAll("\\s", FuHao.NULL));

        if (addOn_jiaJian) {
            addLineFeed(strBuf, FuHao.jia, addOn_kuoHaoNei);
            addLineFeed(strBuf, FuHao.jian, addOn_kuoHaoNei);
        }
        if (addOn_chengChu) {
            addLineFeed(strBuf, FuHao.cheng, addOn_kuoHaoNei);
            addLineFeed(strBuf, FuHao.chu, addOn_kuoHaoNei);
        }

        int dengYuIndex = strBuf.lastIndexOf(FuHao.dengYu);
        if (dengYuIndex >= 0) {
            strBuf.insert(dengYuIndex+FuHao.dengYu.length(), " ");
            strBuf.insert(dengYuIndex, lineFeed);
        }

        return strBuf.toString();
    }

    //检测减号是否为负号
    private static boolean jianCeFu(String text, int index) {
        final int indexUp1 = index - FuHao.kuoHaoTou.length();                       //假设负号存在时，用“负号”的位置推出“括号头+负号”的位置。
        final int indexUp2 = index - FuHao.dengYu.length();                       //假设负号存在时，用“负号”的位置推出“等于+负号”的位置。

        return (text.indexOf(FuHao.kuoHaoTou + FuHao.jian, indexUp1) == indexUp1    //判断“括号头+负号”的位置是否匹配
                || text.indexOf(FuHao.dengYu + FuHao.jian, indexUp2) == indexUp2    //判断“等于+负号”的位置是否匹配
                || index == 0
        );
    }

    public static boolean isParenthesesClosed(String str, int endIndex) {
        return isParenthesesClosed(new StringBuffer(str), endIndex);
    }

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

    private static void addLineFeed(StringBuffer strBuf, String targetStr, boolean addOn_kuoHaoNei) {
        for (int i = strBuf.lastIndexOf(targetStr);
             i >= 0 && i < strBuf.length();
             i = strBuf.lastIndexOf(targetStr, i)) {

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
