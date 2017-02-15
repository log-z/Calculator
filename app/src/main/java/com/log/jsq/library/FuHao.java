package com.log.jsq.library;

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

    public static void luRu(String _jia, String _jian, String _cheng, String _chu, String _dian, String _dengYu, String _kuoHaoTou, String _kuoHaoWei){
        jjccd = new String[] {_jia, _jian, _cheng, _chu, _dian};
        jia = _jia;
        jian = _jian;
        cheng = _cheng;
        chu = _chu;
        dian = _dian;
        dengYu = _dengYu;
        kuoHaoTou = _kuoHaoTou;
        kuoHaoWei = _kuoHaoWei;
    }
}
