package com.log.jsq.library;

import android.content.Context;

import com.log.jsq.R;

public class FuHao {
    public static String jia = null;
    public static String jian = null;
    public static String cheng = null;
    public static String chu = null;
    public static String dian = null;
    public static String dengYu = null;
    public static String kuoHaoTou = null;
    public static String kuoHaoWei = null;
    public static String[] jjccd = null;
    public static final String NULL = "";
    public static final String TEN_POWER = "E";
    public static int JJCC_LEN = 4;         //在 jjccd[] 中加减乘除的长度

    public static void luRu(Context context){
        jia = context.getString(R.string.jia);
        jian = context.getString(R.string.jian);
        cheng = context.getString(R.string.cheng);
        chu = context.getString(R.string.chu);
        dian = context.getString(R.string.dian);
        dengYu = context.getString(R.string.dengYu);
        kuoHaoTou = context.getString(R.string.kuoHaoTou);
        kuoHaoWei = context.getString(R.string.kuoHaoWei);
        jjccd = new String[] {jia, jian, cheng, chu, dian};
    }
}
