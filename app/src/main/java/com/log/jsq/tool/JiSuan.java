package com.log.jsq.tool;

import java.math.BigDecimal;

import com.log.jsq.library.FuHao;
import com.log.jsq.library.Nums;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

public class JiSuan {

    private String t = null;
    private StringBuffer strBuf = null;
    private String[] numStr = null;                 //数值数组（字符串）
    private ArrayList<BigDecimal> numArray = null;  //存储数值
    private int[][] fuHao = null;                   //存储运算符数据
    private static final char jiaChar = '+';
    private static final char jianChar = '-';
    private static final char chengChar = '*';
    private static final char chuChar = '/';
    private static final char dianChar = '.';
    private static final char kuoHaoTou = '(';
    private static final char kuoHaoWei = ')';
    private static final char jjccdChars[] = {jiaChar, jianChar, chengChar, chuChar, dianChar};
    private static final int JIA_NUM = 1;
    private static final int JIAN_NUM = 2;
    private static final int CHENG_NUM = 3;
    private static final int CHU_NUM = 4;
    private static final int[] JJCC_NUM = {JIA_NUM, JIAN_NUM, CHENG_NUM, CHU_NUM};
    private static final int FUHAO_XUHAO_HANG = 0;
    private static final int FUHAO_JJCC_HANG = 1;
    private static final int FUHAO_KUOHAO_HANG = 2;
    private static final int FUHAO_ZONG_HANG = 3;
    private static final int KUOHAOTOU_PULS_NUM = 1;
    private static final int KUOHAOWEI_PULS_NUM = -1;
    private final MathContext mathContext;
    private static int nextIndex;                       //下一个存储的运算符（加减乘除，已替换）的位置

    public JiSuan(int numLen) {
        mathContext = new MathContext(numLen, RoundingMode.HALF_UP);
    }

    //入口
    public String dengYu(String t) throws  ArithmeticException, RuntimeException{
        String jieGuo;
        this.t = t;

        if(t.length() == 0){
            return Nums.nums[0];
        } else {
            try {
                yuChuLi();
                System.out.println("预处理OK");
            } catch(Exception e) {
                throw new RuntimeException();
            } finally {
                this.t = null;
                strBuf = null;
                numStr = null;
            }
        }

        jieGuo = jiSuan().toString();   //计算开始
        numArray.clear();
        fuHao = null;
        System.out.println("计算OK");

        return jieGuo;
    }

    //预处理_mian
    private void yuChuLi(){
        strBuf = new StringBuffer(t);

        if (strBuf.indexOf(FuHao.jian) == 0) {
            strBuf.insert(0, Nums.nums[0]);
        }

        int fuIndex = 0;
        while (true) {
            fuIndex = strBuf.indexOf(FuHao.kuoHaoTou + FuHao.jian, fuIndex);

            if (fuIndex >= 0) {
                strBuf.insert(fuIndex + FuHao.kuoHaoTou.length(), Nums.nums[0]);
            } else {
                break;
            }
        }

        for(int i=0;i<FuHao.jjccd.length;i++) {     //替换符号 - 加减乘除点
            tiHuanFuHao(FuHao.jjccd[i], jjccdChars[i]);
        }
        tiHuanFuHao(FuHao.kuoHaoTou, kuoHaoTou);    //替换符号 - 括号头
        tiHuanFuHao(FuHao.kuoHaoWei, kuoHaoWei);    //替换符号 - 括号尾

        t = strBuf.toString();                  //赋予替换了符号的算式的字符串

        if( t.startsWith(jianChar + "") ) {  //算式以减号开头
            t = 0 + t;
        }

        strFenGe();                //拆分算式的数值

        strToFloatForNum();   //提取 - 数值
        setFuHaoArray(numArray.size());      //提取 - 符号

//        printTable();
    }

    //预处理_替换运算符
    private void tiHuanFuHao(String oldStr, char newChar){
        for(int j=0,k; ; j=k+1){
            k = strBuf.indexOf(oldStr, j);
//            System.out.println(k + " 替换符号中 - 内：" + oldStr + " -> " + newChar);

            if(k != -1) {
//                System.out.println(oldStr.length() + "原 " + strBuf);
                strBuf.delete(k, k + oldStr.length());
//                System.out.println("删 " + strBuf);
                strBuf.insert(k, newChar);
//                System.out.println("增 " + strBuf);
            }
            else{
                break;
            }
        }
    }

    //预处理_拆分字符串（仅数值）
    private void strFenGe(){
        final String zzbdsFuHao = "\\+|\\-|\\*|\\/|\\(|\\)";  //正则表达式 - 匹配运算符

        try{
            numStr = t.split(zzbdsFuHao);
        }
        catch(PatternSyntaxException e){
            System.out.println(t + " : " +"算式与预设的正则表达式不匹配");
        }
    }

    //预处理_字符串提取数值
    private void strToFloatForNum(){
        numArray = new ArrayList<>();

        for (String aNumStr : numStr) {
            if (aNumStr.length() > 0) {
                BigDecimal bigDecimal = new BigDecimal(aNumStr, mathContext);
                numArray.add(bigDecimal);
            }
        }
    }

    //预处理_设置运算符数组
    private void setFuHaoArray(final int lie){
        final int jjccLen = 3;  //加减乘除的长度（在jjccdChars中）
        int temp;               //运算符数组数据的临时存储

        fuHao = new int[FUHAO_ZONG_HANG][lie];  //定义用于存储运算符（包括加减乘除与括号）的二维数组
        nextIndex = 0;

        for(int i=0; i <= jjccLen; i++) {       //数值化 - 加减乘除
            shuZhiHuaJJCC(jjccdChars[i], JJCC_NUM[i]);
        }

        for(int j=0;j<lie-1;j++) {               //还原数值化后的加减乘除顺序（使用冒泡算法）
            for(int k=j;k>0;k--) {
                if(fuHao[FUHAO_XUHAO_HANG][k] < fuHao[FUHAO_XUHAO_HANG][k-1]) {
                    //交换序号
                    temp = fuHao[FUHAO_XUHAO_HANG][k-1];
                    fuHao[FUHAO_XUHAO_HANG][k-1] = fuHao[FUHAO_XUHAO_HANG][k];
                    fuHao[FUHAO_XUHAO_HANG][k] = temp;

                    //交换运算符代数
                    temp = fuHao[FUHAO_JJCC_HANG][k-1];
                    fuHao[FUHAO_JJCC_HANG][k-1] = fuHao[FUHAO_JJCC_HANG][k];
                    fuHao[FUHAO_JJCC_HANG][k] = temp;
                }
            }
        }

        //数值化括号
        shuZhiHuaKuoHao(kuoHaoTou, KUOHAOTOU_PULS_NUM);
        shuZhiHuaKuoHao(kuoHaoWei, KUOHAOWEI_PULS_NUM);

    }

    //预处理_数值化运算符 - 加减乘除
    private void shuZhiHuaJJCC(char fuHaoChar, int fuHaoNum){
        for(int next=0, find; ; next=find+1, nextIndex++) {
            find = t.indexOf(fuHaoChar, next);

            if(find != -1) {
                fuHao[FUHAO_XUHAO_HANG][nextIndex] = find;
                fuHao[FUHAO_JJCC_HANG][nextIndex] = fuHaoNum;
            }
            else{
                break;
            }
        }
    }

    //预处理_数值化运算符 - 括号
    private void
    shuZhiHuaKuoHao(char kuoHaoChar, int plusNum) {
        final int lie = numArray.size();   //数组的列数
        int i;
        boolean findeFuHao;

        for(int next=0, find; ; next=find+1) {      //next与find均为位置下标
            find = t.indexOf(kuoHaoChar, next);
//            System.out.println("!找到的括号对应的位置下标：" + find);

            if(find != -1) {
                for(i=0, findeFuHao=false;i < lie-1;i++) {                   //加减乘除点的列数会比数组和括号的少一列
                    if(fuHao[FUHAO_XUHAO_HANG][i] > find){          //找到与该括号最近的一个加减乘除
//                        System.out.println(">>> 找到括号对应的符号 <<<");
                        findeFuHao = true;
                        break;
                    }
                }

                if(i >= lie && !findeFuHao) {                  //对加减乘除宽度以后的括号的附加操作
                    i++;
                }

//                System.out.println("$数值化括号对应的位置下标：" + i);
                fuHao[FUHAO_KUOHAO_HANG][i] += plusNum;         //在原来的基础上加上括号相应的常数
            }
            else{
                break;
            }
        }
    }

    //计算_main
    private BigDecimal jiSuan() throws ArithmeticException {
        int[] kuoHaoIndexArray;
        int lose;

        while (true){
            lose = 0;
            kuoHaoIndexArray = getMaxLevelIndex();
//            System.out.println("计算-括号最大级别位置：" + kuoHaoIndexArray[0] + "/" + kuoHaoIndexArray[1]);

           if(kuoHaoIndexArray[0] < 0 || kuoHaoIndexArray[1] < 0){
                break;
           }
           else{
               lose += chengChu(kuoHaoIndexArray[0], kuoHaoIndexArray[1]);
               lose += jiaJian(kuoHaoIndexArray[0], kuoHaoIndexArray[1] - lose);

               if(lose > 0) {
                    kuoHaoDel(kuoHaoIndexArray);
               }

//             printTable();
           }
        }

        if(numArray.size() > 1) {
            chengChu(0, numArray.size() - 1);
            jiaJian(0, numArray.size() - 1);
        }

//        printTable();
        return numArray.get(0);
    }

    //计算_获取最高级别的计算范围（关于括号）
    private int[] getMaxLevelIndex(){
        int maxLevelStartIndex = -1;    //最高级别起始下标（括号头位置）
        int maxLevelEndIndex = -1;      //最高级别结束下标（括号尾位置）
        int[] index = new int[2];       //存储最高级别始末下标
        int kuoHouDaiShu;

        for (int nowLevel=0, maxLevel=0, i=0; i<numArray.size();i++) {
            kuoHouDaiShu = fuHao[FUHAO_KUOHAO_HANG][i];
//            System.out.println("找到的括号代数：" + i + "/" + kuoHouDaiShu);

            if(kuoHouDaiShu > 0){
//                System.out.println("括号头：" + i + "/" + kuoHouDaiShu);
                nowLevel += kuoHouDaiShu;

                if(nowLevel > maxLevel) {
                    maxLevel = nowLevel;
                    maxLevelStartIndex = i;
                }
            }
            else if(kuoHouDaiShu < 0){
//                System.out.println("括号尾：" + i + "/" + kuoHouDaiShu);
                if(nowLevel == maxLevel){
                    maxLevelEndIndex = i;
                }

                nowLevel -= kuoHouDaiShu;
            }
        }

        index[0] = maxLevelStartIndex;
        index[1] = maxLevelEndIndex;

        return index;
    }

    //jiSuan_删除加减乘除
    private void fuHaoDel(int index) {
        int j = index;

        while(j < numArray.size()-1) {
            fuHao[FUHAO_XUHAO_HANG][j] = fuHao[FUHAO_XUHAO_HANG][j+1];
            fuHao[FUHAO_JJCC_HANG][j] = fuHao[FUHAO_JJCC_HANG][j+1];

            j++;
        }
    }

    //jiSuan_更新数值
    private void numRefresh (int index, BigDecimal newNum) {
        numArray.remove(index);
        numArray.remove(index);
        numArray.add(index, newNum);
    }

    //jiSuan_删除括号
    private void kuoHaoDel(int[] kuoHaoIndexArray) {
        int kuoHaoIndexStart = kuoHaoIndexArray[0];
        int kuoHaoIndexEnd = kuoHaoIndexArray[1];

        fuHao[FUHAO_KUOHAO_HANG][kuoHaoIndexStart] -= KUOHAOTOU_PULS_NUM;
        fuHao[FUHAO_KUOHAO_HANG][kuoHaoIndexEnd] -= KUOHAOWEI_PULS_NUM;
        System.arraycopy(fuHao[FUHAO_KUOHAO_HANG], kuoHaoIndexStart + 1, fuHao[FUHAO_KUOHAO_HANG], kuoHaoIndexStart, fuHao[FUHAO_KUOHAO_HANG].length - kuoHaoIndexStart - 1);
    }

    //jiSuan_乘除运算
    private int chengChu(int start, int end) throws ArithmeticException {
        int lose = 0;

        for (int i = start, last = end;i < last;last = end - lose) {
            if(fuHao[FUHAO_JJCC_HANG][i] == CHENG_NUM){
                BigDecimal fastBigDecimal = numArray.get(i);
                BigDecimal lastBigDecimal = numArray.get(i+1);
                BigDecimal newBigDecimal = fastBigDecimal.multiply(lastBigDecimal, mathContext);

                jiSuanTongYong(i, newBigDecimal);
                lose++;

//                System.out.println(">>>>>>>>>>>>>>>" + fastBigDecimal + " * " + lastBigDecimal + " = " + newBigDecimal);
//                printTable();
            }
            else if(fuHao[FUHAO_JJCC_HANG][i] == CHU_NUM){
                BigDecimal fastBigDecimal = numArray.get(i);
                BigDecimal lastBigDecimal = numArray.get(i+1);

                if(lastBigDecimal.equals(new BigDecimal(0))){
                    throw new ArithmeticException();
                }
                else {
                    BigDecimal newBigDecimal = fastBigDecimal.divide(lastBigDecimal, mathContext);

                    jiSuanTongYong(i, newBigDecimal);
                    lose++;

//                    System.out.println(">>>>>>>>>>>>>>>" + fastBigDecimal + " / " + lastBigDecimal + " = " + newBigDecimal);
//                    printTable();
                }
            }
            else{
                i++;
            }
        }

        return lose;
    }

    //jiSuan_加减运算
    private int jiaJian(int start, int end) {
        int lose = 0;

        for (int i = start, last = end;i < last;last = end - lose) {
            if (fuHao[FUHAO_JJCC_HANG][i] == JIA_NUM) {
                BigDecimal fastBigDecimal = numArray.get(i);
                BigDecimal lastBigDecimal = numArray.get(i+1);
                BigDecimal newBigDecimal =  fastBigDecimal.add(lastBigDecimal, mathContext);

                jiSuanTongYong(i, newBigDecimal);
                lose++;

//                System.out.println(">>>>>>>>>>>>>>>" + fastBigDecimal + " + " + lastBigDecimal + " = " + newBigDecimal);
//                printTable();
            }
            else if(fuHao[FUHAO_JJCC_HANG][i] == JIAN_NUM){
                BigDecimal fastBigDecimal = numArray.get(i);
                BigDecimal lastBigDecimal = numArray.get(i+1);
                BigDecimal newBigDecimal = fastBigDecimal.subtract(lastBigDecimal, mathContext);

                jiSuanTongYong(i, newBigDecimal);
                lose++;

//                System.out.println(">>>>>>>>>>>>>>>" + fastBigDecimal + " - " + lastBigDecimal + " = " + newBigDecimal);
//                printTable();
            }
            else{
                i++;
            }
        }

        return lose;
    }

    //jiSuan_加减乘除通用过程
    private void jiSuanTongYong(int index, BigDecimal newNum) {
        numRefresh(index, newNum);
        fuHaoDel(index);
    }

/*
    //测试_输出表
    private void printTable() {
        for(int j=0;j<numArray.size();j++){
            System.out.printf("%d, ", fuHao[FUHAO_XUHAO_HANG][j]);
        }

        System.out.println();

        for(int j=0;j<numArray.size();j++){
            System.out.printf("%d, ", fuHao[FUHAO_JJCC_HANG][j]);
        }

        System.out.println();

        for(int j=0;j<numArray.size();j++){
            System.out.printf("%d, ", fuHao[FUHAO_KUOHAO_HANG][j]);
        }

        System.out.println();

        for(BigDecimal bigDecimal: numArray) {
            System.out.printf("%s, ", bigDecimal);
        }

        System.out.println();
    }

*/
}
