package com.log.jsq.library;

public class GuiZe {
    public static final int NO_FUHAO = -1;    //“空”的代号（必须与FuHao.jjccd[]对应）
    public static final int JIA_NUM = 0;      //“加”的代号（必须与FuHao.jjccd[]对应）
    public static final int JIAN_NUM = 1;     //“减”的代号（必须与FuHao.jjccd[]对应）
    public static final int CHENG_NUM = 2;    //“乘以”的代号（必须与FuHao.jjccd[]对应）
    public static final int CHU_NUM = 3;      //“除以”的代号（必须与FuHao.jjccd[]对应）

    public static boolean dian(String t, String b){
        if(t.length()>0 && t.indexOf(b)<0 && !t.endsWith(FuHao.jian)){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean jia_Jian_Cheng_Chu(String t, String tN) {
        if( ( tongYong(t, tN) && tN.length()>0 ) || ( tN.length() == 0 && t.endsWith(FuHao.kuoHaoWei) ) ){
            return true;
        }else{
            return false;
        }
    }

    public static boolean dengYu(String t, String tN){
        if( (tN.length()==0 && jjccdEnd(t) == NO_FUHAO) || tongYong(t, tN) ){
            return true;
        } else {
            return false;
        }
    }

    public static boolean tongYong(String t, String tN) {
        if( !tN.endsWith(FuHao.dian) && ( jjccdEnd(t) == NO_FUHAO || ( tN.length()>0 && jjccdEnd(t) != NO_FUHAO ) ) && !tN.endsWith(FuHao.jian) ){
            return true;
        }
        else{
            return false;
        }
    }

    public static int jjccdEnd(String t) {
        for(int i=0;i<FuHao.JJCC_LEN;i++){
            if(t.endsWith(FuHao.jjccd[i])){
                return i;
            }
        }

        return NO_FUHAO;
    }

    public static boolean kuoHaoTou(String t, String tN){
        boolean numEnd = false;

        for(int i=0;i<Nums.nums.length;i++){
            if( t.endsWith(Nums.nums[i]) ){
                numEnd = true;
            }
        }

        if(tN.length() == 0 && !t.endsWith(FuHao.kuoHaoWei) && !numEnd){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean kuoHaoWei(String t, String tN) {
        if ( ( tN.length()==0 && !t.endsWith(FuHao.kuoHaoTou) && jjccdEnd(t) == NO_FUHAO ) || ( tN.length()>0 && tongYong(t, tN) ) ) {
            return true;
        } else {
            return false;
        }
    }
}
