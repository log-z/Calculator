package com.log.jsq.mainUI;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.log.jsq.library.FuHao;
import com.log.jsq.library.GuiZe;
import com.log.jsq.library.Recorder;
import com.log.jsq.tool.HistoryListData;
import com.log.jsq.tool.JiSuan;
import com.log.jsq.library.Nums;
import com.log.jsq.R;
import com.log.jsq.tool.TextHandler;

import java.util.Objects;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MainUI {
    private static MainUI INSTANCE = new MainUI();
    private TextView tv = null;
    private TextView tv2 = null;
    private TextView tvNum = null;
    private TextView tvNum2 = null;
    private ScrollView sv1 = null;
    private ScrollView sv2 = null;
    private Button jia = null;
    private Button jian = null;
    private Button cheng = null;
    private Button chu = null;
    private Button dian = null;
    private Button dengYu = null;
    private Button kuoHaoTou = null;
    private Button kuoHaoWei = null;
    private ImageButton huanSuan = null;
    private MainActivity ma = null;
    private boolean clickSure = true;
    private static final int NUM_MAX_LEN = 30;
    private String lastFormula;
    private String lastResult;

    private MainUI(){}

    /**
     * 获取该类的单例
     * @return  返回该类的单一实例
     */
    public static MainUI getInstance() {
        return INSTANCE;
    }

    /**
     * 构造和注册控件监听器
     * @param  activity     控件所在的activity
     */
    public void init(Activity activity) {
        ma = (MainActivity)activity;
        this.tv = ma.findViewById(R.id.textView);
        this.tv2 = ma.findViewById(R.id.textView2);
        this.tvNum = ma.findViewById(R.id.textViewNum);
        this.tvNum2 = ma.findViewById(R.id.textViewNum2);
        this.sv1 = ma.findViewById(R.id.sv1);
        this.sv2 = ma.findViewById(R.id.sv2);

        NumClickListener ncl = new NumClickListener();
        FuHaoClickListener fhcl = new FuHaoClickListener();
        FuHaoLongClickListener fhlcl = new FuHaoLongClickListener();
        CtrlClickListener ccl = new CtrlClickListener();
        TextOnClickListener tocl = new TextOnClickListener();
        TextOnLongClickListener tolcl = new TextOnLongClickListener();

        jia = activity.findViewById(R.id.bJia);
        jian = activity.findViewById(R.id.bJian);
        cheng = activity.findViewById(R.id.bCheng);
        chu = activity.findViewById(R.id.bChu);
        dian = activity.findViewById(R.id.bDian);
        dengYu = activity.findViewById(R.id.bDengyu);
        kuoHaoTou = activity.findViewById(R.id.bKuoHaoTou);
        kuoHaoWei = activity.findViewById(R.id.bKuoHaoWei);
        huanSuan = activity.findViewById(R.id.bZhuanHuan);

        tv.setOnLongClickListener(tolcl);
        tv.setOnClickListener(tocl);
        tv.addTextChangedListener(new TextWatcherListener(tv));
        tv2.setOnClickListener(tocl);
        tv2.addTextChangedListener(new TextWatcherListener(tv2));
        tvNum.setOnLongClickListener(tolcl);
        tvNum.setOnClickListener(tocl);
        tvNum.addTextChangedListener(new TextWatcherListener(tvNum));
        tvNum2.setOnLongClickListener(tolcl);

        activity.findViewById(R.id.b0).setOnClickListener(ncl);
        activity.findViewById(R.id.b1).setOnClickListener(ncl);
        activity.findViewById(R.id.b2).setOnClickListener(ncl);
        activity.findViewById(R.id.b3).setOnClickListener(ncl);
        activity.findViewById(R.id.b4).setOnClickListener(ncl);
        activity.findViewById(R.id.b5).setOnClickListener(ncl);
        activity.findViewById(R.id.b6).setOnClickListener(ncl);
        activity.findViewById(R.id.b7).setOnClickListener(ncl);
        activity.findViewById(R.id.b8).setOnClickListener(ncl);
        activity.findViewById(R.id.b9) .setOnClickListener(ncl);

        activity.findViewById(R.id.bGuiLing).setOnClickListener(ccl);
        activity.findViewById(R.id.bShanChu).setOnClickListener(ccl);
        huanSuan.setOnClickListener(ccl);

        jia.setOnClickListener(fhcl);
        jian.setOnClickListener(fhcl);
        cheng.setOnClickListener(fhcl);
        chu.setOnClickListener(fhcl);
        dian.setOnClickListener(fhcl);
        dengYu.setOnClickListener(fhcl);

        jia.setOnLongClickListener(fhlcl);
        jian.setOnLongClickListener(fhlcl);
        cheng.setOnLongClickListener(fhlcl);
        chu.setOnLongClickListener(fhlcl);
        dengYu.setOnLongClickListener(fhlcl);

        activity.findViewById(R.id.bKuoHaoTou) .setOnClickListener(fhcl);
        activity.findViewById(R.id.bKuoHaoWei) .setOnClickListener(fhcl);
    }

    /**
     * 追加字符串
     * @param  tv   指定的TextView
     * @param  str  追加的字符串
     */
    private void addText(TextView tv, String str){
        str = tv.getText().toString() + str;
        tv.setText(str);
    }

    /**
     * 连接数值区的字符串到算式区
     * 会添加必要的括号
     * @param  isCut    是否剪切
     * @param  addStr   再次追加的字符串，允许为空
     */
    private void connectionTextView(boolean isCut, String addStr) {
        String tvStr = tv.getText().toString();
        String tvNStr = tvNum.getText().toString();

        if (addStr == null) {
            addStr = FuHao.NULL;
        }

        if(tvNStr.startsWith(FuHao.jian)) {
            // 自动为负数头尾添加必要的括号
            if (tvStr.length() == 0 || tvStr.endsWith(FuHao.kuoHaoTou)) {
                tv.setText(tvStr + tvNStr + addStr);
            } else {
                tv.setText(tvStr + FuHao.kuoHaoTou + tvNStr + FuHao.kuoHaoWei + addStr);
            }
        } else {
            tv.setText(tvStr + tvNStr + addStr);
        }

        if (isCut) {
            tvNum.setText(null);
        }
    }

    /**
     * 检测数值区是否有等号
     */
    private boolean jianCeDengHao(){
        return tvNum.getText().toString().contains(FuHao.dengYu);
    }

    /**
     * 数字按钮点击监听器
     * 包括：0-9
     */
    private class NumClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (clickSure) {
                runZhenDong(ma.zhenDongTime);
                runYuYin(view);

                if (tvNum2.getVisibility() == View.VISIBLE) {
                    tvNum2.setVisibility(View.GONE);
                    tvNum.setVisibility(View.VISIBLE);
                    tvNum2.setText(null);
                }

                if (jianCeDengHao()) {
                    setTempHistory(true);
                    tv.setText(null);
                    tvNum.setText(null);
                }

                String tN = tvNum.getText().toString();

                if (!tv.getText().toString().endsWith(FuHao.kuoHaoWei)) {
                    if (tN.equals(Nums.nums[0])) {
                            tvNum.setText(null);
                    }
                    if ((tN.startsWith(FuHao.jian) && tN.length() >= NUM_MAX_LEN+1) || tN.length() >= NUM_MAX_LEN) {
                        Log.d("MainUI$NumClickListener", "数字超长");
                    } else {
                        addText(tvNum, ((Button) view).getText().toString());
                        toBottom(sv1);
                        toBottom(sv2);
                    }
                }

                // 更新记录者
                Recorder.update(
                        new Recorder.Record(tv.getText().toString(), tvNum.getText().toString()));
            }
        }
    }

    /**
     * 运算符按钮点击监听器
     * 包括：加减乘除、小数点、括号、等于
     */
    private class FuHaoClickListener implements View.OnClickListener{
        private long time = 0;
        private final int TIME_CHA = 500;
        private int chiShu = 0;
        private final int MAX_CHI_SHU = 5;

        @Override
        public void onClick(View view) {
            if (clickSure) {
                if (tvNum2.getVisibility() == View.VISIBLE) {
                    tvNum2.setVisibility(View.GONE);
                    tvNum.setVisibility(View.VISIBLE);
                    tvNum2.setText(null);
                }

                String tt = tv.getText().toString();
                String ttN = tvNum.getText().toString();
                String bt = ((Button) view).getText().toString();

                runZhenDong(ma.zhenDongTime);
                if (view.getId() != R.id.bDengyu) {
                    runYuYin(view);
                }

                if (jianCeDengHao() && view.getId() != dengYu.getId()) {
                    // 连续运算与常规运算的切换
                    SharedPreferences sp =
                            ma.getSharedPreferences("setting", MODE_PRIVATE);
                    String mode = sp.getString("resultsAgainCalculation",
                            ma.getString(R.string.default_resultsAgainCalculation));
                    setTempHistory(true);

                    switch (mode) {
                        case "resultsAgainCalculation_formulaPreferred":
                            continueByEquation(view, tt);
                            break;
                        case "resultsAgainCalculation_resultPreferred":
                            continueByResult(view, ttN);
                            break;
                    }
                } else {
                    if (view.getId() == jia.getId()
                        || view.getId() == jian.getId()
                        || view.getId() == cheng.getId()
                        || view.getId() == chu.getId()) {  //判断 加减乘除
                        final int jjccEndNum = GuiZe.jjccEnd(tt);
                        String fuHaoStr = ((Button) view).getText().toString();
                        // 忽略小数点
                        ttN = ignoreDian(ttN);
                        tvNum.setText(ttN);

                        if (jjccEndNum != GuiZe.NO_FIND_FUHAO && ttN.length() == 0) {
                            tt = tt.substring(0, tt.length() - FuHao.jjccd[jjccEndNum].length())
                                + fuHaoStr;
                            tv.setText(tt);
                            toBottom(sv1);
                            toTop(sv2);
                        } else if (GuiZe.jia_Jian_Cheng_Chu(tt, ttN)) {
                            connectionTextView(true, bt);
                            toBottom(sv1);
                            toTop(sv2);
                        }
                    } else if (view.getId() == dian.getId()) {      //判断 小数点
                        if (ttN.endsWith(FuHao.dian)) {
                            ttN = ttN.substring(0, ttN.length() - FuHao.dian.length());
                            tvNum.setText(ttN);
                            toBottom(sv1);
                            toBottom(sv2);
                        } else if (GuiZe.dian(ttN, bt)) {
                            addText(tvNum, bt);
                            toBottom(sv1);
                            toBottom(sv2);
                        }
                    } else if (view.getId() == kuoHaoTou.getId()) {   //判断 括号头
                        if (GuiZe.kuoHaoTou(tt, ttN)) {
                            addText(tv, FuHao.kuoHaoTou);
                            toBottom(sv1);
                            toTop(sv2);
                        }
                    } else if (view.getId() == kuoHaoWei.getId()) {   //判断 括号尾
                        if (GuiZe.kuoHaoWei(tt, ttN)) {
                            connectionTextView(true, FuHao.kuoHaoWei);
                            toBottom(sv1);
                            toTop(sv2);
                        } else if (ttN.length() == 0 && tt.endsWith(FuHao.kuoHaoTou)) {
                            tt = tt.substring(0, tt.length() - FuHao.kuoHaoTou.length());
                            tv.setText(tt);
                        }
                    } else if (view.getId() == dengYu.getId()) {    //判断 等号
                        runZhenDong(ma.zhenDongTimeAdd);

                        //彩蛋
                        if (tt.length() == 0 && ttN.length() == 0) {
                            long mNowTime = System.currentTimeMillis();

                            //比较两次按键时间差
                            if (mNowTime - time > TIME_CHA) {
                                chiShu = 0;
                                time = mNowTime;
                            } else {
                                if (chiShu < MAX_CHI_SHU) {
                                    chiShu++;
                                } else {
                                    chiShu = 0;
                                }

                                time = mNowTime;
                            }

                            if (chiShu >= MAX_CHI_SHU) {
                                Toast.makeText(ma, "_(:3」∠)_", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            runYuYin(view);
                        } else {
                            final int jjccEndNum = GuiZe.jjccEnd(tt);
                            ttN = ignoreDian(ttN);

                            if (jjccEndNum != GuiZe.NO_FIND_FUHAO && ttN.length() == 0) {
                                tt = tt.substring(0,
                                        tt.length() - FuHao.jjccd[jjccEndNum].length());
                            }

                            if (GuiZe.dengYu(tt, ttN)) {
                                toBottom(sv1);
                                toTop(sv2);

                                // 如果没有结果则连接字符串
                                if (!ttN.startsWith(FuHao.dengYu)) {
                                    tt = equationConnection(tt, ttN);
                                }
                                tv.setText(tt);
                                tvNum.setText(null);
                                clickSure = false;

                                final String equation = tt;
                                new Thread() {
                                    @Override
                                    public void run() {
                                        final String errorText = ma.getString(R.string.errorText);
                                        final String chuLingErrorText =
                                                ma.getString(R.string.chuLingErrorText);

                                        try {
                                            String jieGuoStr = new JiSuan(NUM_MAX_LEN)
                                                    .dengYu(equation);
                                            jieGuoStr = Nums.Ling(jieGuoStr);
                                            final String finalJieGuoStr = jieGuoStr;

                                            ma.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tvNum.setText(FuHao.dengYu + finalJieGuoStr);
                                                    clickSure = true;

                                                    // 更新记录者
                                                    Recorder.update(new Recorder.Record(
                                                            tv.getText().toString(),
                                                            tvNum.getText().toString()
                                                    ));
                                                }
                                            });

                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    saveToSql(equation, finalJieGuoStr);
                                                }
                                            }.start();

                                            if (ma.isOnYuYin()) {
                                                runYuYin(finalJieGuoStr);
                                            }

                                            lastFormula = equation;
                                            lastResult = jieGuoStr;
                                        } catch (ArithmeticException ae) {
                                            ma.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tvNum.setText(FuHao.dengYu + chuLingErrorText);
                                                    clickSure = true;

                                                    // 更新记录者
                                                    Recorder.update(new Recorder.Record(
                                                            tv.getText().toString(),
                                                            tvNum.getText().toString()
                                                    ));
                                                }
                                            });
                                        } catch (RuntimeException e) {
                                            ma.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tvNum.setText(FuHao.dengYu + errorText);
                                                    clickSure = true;

                                                    // 更新记录者
                                                    Recorder.update(new Recorder.Record(
                                                            tv.getText().toString(),
                                                            tvNum.getText().toString()
                                                    ));
                                                }
                                            });
                                            e.printStackTrace();
                                        } finally {
                                            //计算结束后，因文本变化要重新调整显示
                                            ma.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    toBottom(sv1);
                                                    toTop(sv2);
                                                }
                                            });
                                        }
                                    }
                                }.start();
                            } else {
                                runYuYin(view);
                            }
                        }
                    }
                }

                if (view.getId() != R.id.bDengyu) {
                    // 更新记录者
                    Recorder.update(new Recorder.Record(
                            tv.getText().toString(), tvNum.getText().toString()
                    ));
                }
            }
        }

        /**
         * 忽略末尾小数点
         * @param ttN   数值字符串
         * @return      处理后的数值字符串
         */
        private String ignoreDian(String ttN) {
            if (ttN.endsWith(FuHao.dian)) {
                ttN = ttN.substring(0, ttN.length() - FuHao.dian.length());
            }
            return ttN;
        }

        /**
         * 连接数值到算式末尾
         * 会添加必要的括号
         * @param equation  算式
         * @param newNum    数值
         * @return          处理后的算式
         */
        private String equationConnection(String equation, String newNum) {
            if (newNum.contains(FuHao.jian)) {
                if (equation.length() == 0 || equation.endsWith(FuHao.kuoHaoTou)) {
                    return equation + newNum;
                } else {
                    return equation + FuHao.kuoHaoTou + newNum + FuHao.kuoHaoWei;
                }
            } else {
                return equation + newNum;
            }
        }
    }

    /**
     * 运算符按钮长按监听器
     * 包括：加减乘除、等于
     */
    private class FuHaoLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            if (tvNum2.getVisibility() == View.VISIBLE) {
                tvNum2.setVisibility(View.GONE);
                tvNum.setVisibility(View.VISIBLE);
                tvNum2.setText(null);
            }

            String ttN = tvNum.getText().toString();

            if (jianCeDengHao()) {
                // 连续运算与常规运算的切换
                SharedPreferences sp = ma.getSharedPreferences("setting", MODE_PRIVATE);
                String mode = sp.getString("resultsAgainCalculation",
                        ma.getString(R.string.default_resultsAgainCalculation));
                setTempHistory(true);

                // 长按时操作相反
                switch (mode) {
                    case "resultsAgainCalculation_formulaPreferred":
                        continueByResult(v, ttN);
                        break;
                    case "resultsAgainCalculation_resultPreferred":
                        if (v.getId() == dengYu.getId()) {
                            continueByResult(v, ttN);
                        } else {
                            continueByEquation(v, tv.getText().toString());
                        }
                        break;
                }

                runYuYin(v);
                // 更新记录者
                Recorder.update(
                        new Recorder.Record(tv.getText().toString(), tvNum.getText().toString()));
            }

            return true;
        }
    }

    /**
     * 控制按钮点击监听器
     * 包括：删除、清除、转换
     */
    private class CtrlClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (clickSure) {
                if (tvNum2.getVisibility() == View.VISIBLE && view.getId() != huanSuan.getId()) {
                    tvNum2.setVisibility(View.GONE);
                    tvNum.setVisibility(View.VISIBLE);
                    tvNum2.setText(null);
                }

                runZhenDong(ma.zhenDongTime);
                runYuYin(view);

                if (view.getId() == R.id.bGuiLing && tv.length() == 0 && tvNum.length() == 0) {
                    setTempHistory(false);
                    return;
                }
                if (jianCeDengHao()) {
                    switch (view.getId()) {
                        case R.id.bGuiLing:
                            setTempHistory(true);
                            tv.setText(null);
                            tvNum.setText(null);
                            break;
                        case R.id.bShanChu:
                            setTempHistory(true);
                            tvNum.setText(null);
                            break;
                        case R.id.bZhuanHuan:
                            if (tvNum2.getVisibility() == View.GONE) {
                                String numStr = tvNum.getText().toString().substring(FuHao.dengYu.length());
                                String capsStr = Nums.CapsZH.toCapsZH(numStr, ma);

                                if (capsStr != null && capsStr.length() > 0) {
                                    tvNum2.setText(FuHao.dengYu + capsStr);
                                    tvNum.setVisibility(View.GONE);
                                    tvNum2.setVisibility(View.VISIBLE);
                                }
                            } else {
                                tvNum2.setVisibility(View.GONE);
                                tvNum.setVisibility(View.VISIBLE);
                                tvNum2.setText(null);
                            }
                            break;
                    }
                } else {
                    if (tvNum.getText().toString().length() > 0) {
                        delete(view, tvNum);

                    } else {
                        delete(view, tv);
                    }
                }

                String tvText = tv.getText().toString();

                // 数值回流
                if (tvNum.getText().length() == 0) {
                    int index = numberLastIndex(tvText);

                    if (index >= 0) {
                        tvNum.setText(tvText.substring(index, tvText.length()));
                        tv.setText(tvText.substring(0, index));
                    } else {
                        tvNum.setText(tvText);
                        tv.setText(null);
                    }
                }

                // 更新记录者
                Recorder.update(
                        new Recorder.Record(tv.getText().toString(), tvNum.getText().toString()));
            }
        }

        /**
         * 不定向删除或清除
         * @param   view    按下的按钮
         * @param   t       指定操作对象
         */
        private void delete(View view, TextView t) {
            switch (view.getId()) {
                case R.id.bGuiLing:
                    runZhenDong(ma.zhenDongTimeLong);

                    if(t.getId() == tv.getId()){
                        toTop(sv1);
                    } else if (t.getId() == tvNum.getId()) {
                        toBottom(sv1);
                    }

                    toTop(sv2);
                    t.setText(null);
                    break;
                case R.id.bShanChu:
                    String tt = t.getText().toString();

                    if (tt.length() > 0) {
                        toBottom(sv1);
                        toBottom(sv2);
                        t.setText(tt.substring(0, tt.length() - 1));
                    }
                    break;
            }
        }

        /**
         * 最后一个数值的位置
         * @param str   指定的字符串
         * @return      返回字符串中最后一个数值的位置
         */
        private int numberLastIndex(String str) {
            int tempIndex;
            int maxIndex = -1;
            String fuHao = FuHao.NULL;

            for(int i=0;i<FuHao.jjccd.length-1;i++) {
                tempIndex = str.lastIndexOf(FuHao.jjccd[i]);
                if (tempIndex > maxIndex) {
                    maxIndex = tempIndex;
                    fuHao = FuHao.jjccd[i];
                }
            }

            tempIndex = str.lastIndexOf(FuHao.kuoHaoTou);
            if (tempIndex > maxIndex) {
                maxIndex = tempIndex;
                fuHao = FuHao.kuoHaoTou;
            }

            tempIndex = str.lastIndexOf(FuHao.kuoHaoWei);
            if (tempIndex > maxIndex) {
                maxIndex = tempIndex;
                fuHao = FuHao.kuoHaoWei;
            }

            tempIndex = str.lastIndexOf(FuHao.kuoHaoTou + FuHao.jian);
            if (tempIndex > maxIndex || (fuHao.equals(FuHao.jian) && tempIndex + FuHao.kuoHaoTou.length() >= maxIndex)) {
                maxIndex = tempIndex;
                fuHao = FuHao.kuoHaoTou;
            }

            return maxIndex + fuHao.length();
        }
    }

    /**
     * 文本框点击监听器
     * 包括：数值区、算式区、附加算式区
     */
    private class TextOnClickListener implements View.OnClickListener {
        private long lastTime = 0;
        private long nowTime = 0;

        @Override
        public void onClick(View view) {
            if(ma.isMainMod() && clickSure && view.getId() == tvNum.getId()) {
                if (tvNum2.getVisibility() == View.VISIBLE) {
                    tvNum2.setVisibility(View.GONE);
                    tvNum.setVisibility(View.VISIBLE);
                    tvNum2.setText(null);
                }

                String ttN = tvNum.getText().toString();
                runZhenDong(ma.zhenDongTime);

                if (!jianCeDengHao()) {
                    runYuYin(view);
                    toTop(sv2);

                    if (ttN.startsWith(FuHao.jian)) {
                        tvNum.setText(new StringBuffer(ttN).delete(ttN.indexOf(FuHao.jian), FuHao.jian.length()).toString());
                    } else {
                        ttN = FuHao.jian + ttN;
                        tvNum.setText(ttN);
                    }

                    // 更新记录者
                    Recorder.update(
                            new Recorder.Record(tv.getText().toString(), tvNum.getText().toString()));
                }
            } else if (view.getId() == tv.getId() || view.getId() == tv2.getId()) {
                nowTime = System.currentTimeMillis();

                if (nowTime - lastTime < 500) {
                    toTop(sv1);
                } else {
                    lastTime = nowTime;
                }
            }
        }
    }

    /**
     * 文本框长按监听器
     * 包括：数值区、附加数值区、算式区
     */
    private class TextOnLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            ClipboardManager myClipboard = (ClipboardManager)ma.getSystemService(CLIPBOARD_SERVICE);  //实例化剪切板服务
            ClipData myClip;

            runZhenDong(ma.zhenDongTime);

            if(view.getId() == tv.getId()) {
                String tvText = tv.getText().toString();

                if(tvText.trim().length() > 0) {
                    myClip = ClipData.newPlainText("复制的算式", tvText);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(ma, "算式已复制", Toast.LENGTH_SHORT).show();
                }
            } else if (view.getId() == tvNum.getId() || view.getId() == tvNum2.getId()) {

                if(jianCeDengHao()) {
                    String tvNumText = ((TextView) view).getText().toString();
                    myClip = ClipData.newPlainText("复制的结果", tvNumText.substring(FuHao.dengYu.length()));
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(ma, "结果已复制", Toast.LENGTH_SHORT).show();
                }
            }

            return true;    //返回true时，点击和长按可以同时响应
        }

    }

    /**
     * 文本框变化监听器
     */
    private class TextWatcherListener implements TextWatcher {
        private TextView view;
        private String oldString;
        private boolean again = false;
        private int tvLineCountLast = 0;

        private TextWatcherListener(TextView view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(final Editable editable) {
            String text = editable.toString().replaceAll("\\s", FuHao.NULL);
            setButtonVisibility(text);
            save(text);

            //文本处理（换行、变色等）
            if (view.getId() == tv.getId() || view.getId() == tv2.getId() && view.length() > 0) {
                if (oldString == null || !Objects.equals(text, oldString) || again) {
                    again = false;
                    oldString = text;

                    // 自动换行
                    SharedPreferences sp =
                            ma.getSharedPreferences("setting", MODE_PRIVATE);
                    String autoLineFeed = sp.getString(
                            "autoLineFeed", ma.getString(R.string.default_autoLineFeed)
                    );
                    switch (Integer.valueOf(autoLineFeed)) {
                        case 0:
                            text = TextHandler.addLineFeed(text,
                                    false,
                                    false,
                                    false);
                            break;
                        case 1:
                            text = TextHandler.addLineFeed(text,
                                    true,
                                    false,
                                    false);
                            break;
                        case 2:
                            text = TextHandler.addLineFeed(text,
                                    true,
                                    false,
                                    true);
                            break;
                        case 3:
                            text = TextHandler.addLineFeed(text,
                                    true,
                                    true,
                                    false);
                            break;
                        case 4:
                            text = TextHandler.addLineFeed(text,
                                    true,
                                    true,
                                    true);
                            break;
                    }

                    // 变色
                    TypedValue value = new TypedValue();
                    ma.getTheme()
                            .resolveAttribute(R.attr.colorAccent, value, true);
                    Spanned spanned = TextHandler.setStyle(text, value.data);
                    editable.replace(0, editable.length(), spanned);
                } else {
                    again = true;
                }
            }

            // 算式区自适应高度
            if (tv.getLineCount() != tvLineCountLast) {
                LinearLayout.LayoutParams lp;
                tvLineCountLast = tv.getLineCount();

                if (tv2.getVisibility() == View.GONE
                        || tv.getPaddingBottom() + tv.getPaddingTop() + tvLineCountLast * tv.getLineHeight() + tv2.getHeight() <= sv1.getHeight()) {
                    lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                } else {
                    lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                tv.setLayoutParams(lp);
            }
        }

        /**
         * 保存文本值
         * @param cs    已变化的文本
         */
        private void save(CharSequence cs) {
            final String string = cs.toString();
            final SharedPreferences.Editor editor = ma.getSharedPreferences("list", MODE_PRIVATE).edit();

            if (view.getId() == tv.getId()) {
                editor.putString("textView0", string.replaceAll("\\s", FuHao.NULL));
            } else if (view.getId() == tvNum.getId()) {
                editor.putString("numTextView0", string.replaceAll("\\s", FuHao.NULL));
            }

            editor.apply();
        }

        /**
         * 按钮动态显示处理
         * @param cs    已变化的文本
         */
        private void setButtonVisibility(CharSequence cs) {
            String str = cs.toString();

            if (view.getId() == tvNum.getId()) {      //处理：括号头 与 小数点
                if (str.length() > 0) {
                    if (str.contains(FuHao.dengYu)) {
                        huanSuan.setVisibility(View.VISIBLE);
                        dian.setVisibility(View.GONE);
                        kuoHaoTou.setVisibility(View.GONE);
                    } else {
                        dian.setVisibility(View.VISIBLE);
                        huanSuan.setVisibility(View.GONE);
                        kuoHaoTou.setVisibility(View.GONE);
                    }
                } else {
                    kuoHaoTou.setVisibility(View.VISIBLE);
                    huanSuan.setVisibility(View.GONE);
                    dian.setVisibility(View.GONE);
                }
            } else if (view.getId() == tv.getId()) {  //处理：括号尾 与 等号
                if (TextHandler.isParenthesesClosed(str, str.length())) {
                    kuoHaoWei.setVisibility(View.GONE);
                    dengYu.setVisibility(View.VISIBLE);
                } else {
                    kuoHaoWei.setVisibility(View.VISIBLE);
                    dengYu.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 滚动视图到顶部
     * @param sv    指定的滚动视图
     */
    private void toTop(final ScrollView sv) {
        //在新的线程中更新UI
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                switch (sv.getId()) {
                    case R.id.sv1:
                        if (tv2.getVisibility() == View.GONE) {
                            sv.fullScroll(ScrollView.FOCUS_UP);
                        } else {
                            sv.smoothScrollTo(0, tv2.getHeight());
                        }
                        break;
                    case R.id.sv2:
                        sv.fullScroll(ScrollView.FOCUS_UP);
                        break;
                }
            }
        });
    }

    /**
     * 滚动视图到底部
     * @param sv    指定的滚动视图
     */
    private void toBottom(final ScrollView sv){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 保存算式和结果到数据库
     * @param equation  算式字符串
     * @param result    结果字符串
     */
    private void saveToSql(String equation, String result) {
        // 取出数据库中最新的数据
        HistoryListData.RowData rowDataTemp = HistoryListData.exportFirstLineFromSQLite(
                ma.getApplicationContext(), HistoryListData.OrderBy.TIME_DESC);

        if (rowDataTemp == null
                || !rowDataTemp.getEquation().equals(equation)
                || !rowDataTemp.getResult().equals(result)) {
            HistoryListData.RowData rowData = new HistoryListData.RowData(
                    equation.replaceAll("\\s", FuHao.NULL),
                    result.replaceAll("\\s", FuHao.NULL),
                    System.currentTimeMillis()
            );

            HistoryListData.insertToSQLite(rowData, ma.getApplicationContext());
        }
    }

    /**
     * 震动反馈
     * @param zhenDongTime  震动毫秒数
     */
    private void runZhenDong(final long[] zhenDongTime) {
        new Thread() {
            @Override
            public void run() {
                if (ma.isOnZhenDong()) {
                    ma.zhenDong(zhenDongTime);
                }
            }
        }.start();
    }

    /**
     * 语音提示（触控反馈）
     * @param view  需要提示的控件
     */
    private void runYuYin(final View view) {
        if (ma.isOnYuYin()) {
            if (ma.isOnTTS()) {
                ma.tts.speak(view);
            } else {
                ma.au.play(view);
            }
        }
    }

    /**
     * 语音提示（文本转语音）
     * @param numStr    需要播放的字符串
     */
    private void runYuYin(final String numStr) {
        if (ma.isOnTTS()) {
            ma.tts.speak(FuHao.dengYu + numStr);
        } else {
            try {
                int[] numArray = Nums.toIntArray(numStr, false);
                ma.au.play(numArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载字体配置
     */
    public void loadFontSet() {
        SharedPreferences sp = ma.getSharedPreferences("setting", MODE_PRIVATE);
        int fontSizeForEquation = sp.getInt(
                "fontSizeForEquation",
                ma.getResources().getInteger(R.integer.default_fontSizeForEquation)
        );
        int fontSizeForNums = sp.getInt(
                "fontSizeForNums",
                ma.getResources().getInteger(R.integer.default_fontSizeForNums)
        );
        int fontSizeForButton = sp.getInt(
                "fontSizeForButton",
                ma.getResources().getInteger(R.integer.default_fontSizeForButton)
        );

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForEquation);
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForEquation);
        tvNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForNums);
        tvNum2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForNums);
        jia.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        jian.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        cheng.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        chu.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        dengYu.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        dian.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        kuoHaoTou.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        kuoHaoWei.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        jia.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b0)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b2)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b3)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b4)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b5)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b6)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b7)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b8)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) ma.findViewById(R.id.b9)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
    }

    /**
     * 算式再计算
     * @param view          触发的控件
     * @param equation      算式
     */
    private void continueByEquation(View view, String equation) {
        if (view.getId() == jia.getId()
                || view.getId() == jian.getId()
                || view.getId() == cheng.getId()
                || view.getId() == chu.getId()) {
            tvNum.setText(null);
            tv.setText(equation + ((Button) view).getText());
            toBottom(sv1);
            toTop(sv2);
        }
    }

    /**
     * 结果再计算
     * @param v         触发的控件
     * @param result    结果
     */
    private void continueByResult(View v, String result) {
        if (v.getId() == jia.getId()
                || v.getId() == jian.getId()
                || v.getId() == cheng.getId()
                || v.getId() == chu.getId()
                || v.getId() == dengYu.getId()) {
            if (result.contains(FuHao.TEN_POWER)) {
                String tvStr = FuHao.kuoHaoTou + tv.getText().toString() + FuHao.kuoHaoWei;
                tv.setText(tvStr);
                tvNum.setText(null);
            } else if (Nums.isNum(result.substring(result.length() - 1, result.length()))) {
                String tvnStr = result.substring(FuHao.dengYu.length());
                tvNum.setText(tvnStr);
                tv.setText(null);
            }

            if (!(v.getId() == dengYu.getId())) {
                v.performClick();
            }

            toTop(sv2);
            toBottom(sv1);
        }
    }

    /**
     * 设置临时历史记录
     * @param isVisible     是否显示临时历史记录
     */
    public void setTempHistory(boolean isVisible) {
        if (isVisible) {
            SharedPreferences sp = ma.getSharedPreferences("setting", MODE_PRIVATE);

            if (sp.getBoolean(
                    "mainActivityHistoryVisibility",
                    ma.getResources().getBoolean(R.bool.default_mainActivityVisibilityHistory))) {
                if (lastFormula != null && lastResult != null) {
                    tv2.setText(lastFormula  + FuHao.dengYu + lastResult);
                    tv2.setVisibility(View.VISIBLE);
                } else if (jianCeDengHao()) {
                    tv2.setText(tv.getText().toString() + tvNum.getText().toString());
                    tv2.setVisibility(View.VISIBLE);
                }
            }
        } else {
            tv2.setText(null);
            tv2.setVisibility(View.GONE);
        }
    }
}