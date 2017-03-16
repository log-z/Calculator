package com.log.jsq.mainUI;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.log.jsq.library.FuHao;
import com.log.jsq.library.GuiZe;
import com.log.jsq.tool.HistoryListData;
import com.log.jsq.tool.JiSuan;
import com.log.jsq.library.Nums;
import com.log.jsq.R;
import com.log.jsq.tool.TextColorStyles;

import java.util.Objects;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MainUI {
    private static MainUI INSTANCE = new MainUI();
    private Activity activity = null;
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

    @Override
    protected void finalize() throws Throwable {
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        super.finalize();
    }

    private MainUI(){}

    public static MainUI getInstance() {
        return INSTANCE;
    }

    public void init(Activity activity) {
        this.activity = activity;
        ma = (MainActivity)activity;
        this.tv = (TextView)this.activity.findViewById(R.id.textView);
        this.tv2 = (TextView) this.activity.findViewById(R.id.textView2);
        this.tvNum = (TextView)this.activity.findViewById(R.id.textViewNum);
        this.tvNum2 = (TextView)this.activity.findViewById(R.id.textViewNum2);
        this.sv1 = (ScrollView) this.activity.findViewById(R.id.sv1);
        this.sv2 = (ScrollView) this.activity.findViewById(R.id.sv2);

        NumClickListener ncl = new NumClickListener();
        FuHaoClickListener fhcl = new FuHaoClickListener();
        FuHaoLongClickListener fhlcl = new FuHaoLongClickListener();
        CtrlClickListener ccl = new CtrlClickListener();
        TextOnClickListener tocl = new TextOnClickListener();
        TextOnLongClickListener tolcl = new TextOnLongClickListener();

        jia = (Button)activity.findViewById(R.id.bJia);
        jian = (Button)activity.findViewById(R.id.bJian);
        cheng = (Button)activity.findViewById(R.id.bCheng);
        chu = (Button)activity.findViewById(R.id.bChu);
        dian = (Button)activity.findViewById(R.id.bDian);
        dengYu = (Button)activity.findViewById(R.id.bDengyu);
        kuoHaoTou = (Button)activity.findViewById(R.id.bKuoHaoTou);
        kuoHaoWei = (Button)activity.findViewById(R.id.bKuoHaoWei);
        huanSuan = (ImageButton) activity.findViewById(R.id.bZhuanHuan);

        tv.setOnLongClickListener(tolcl);
        tv.setOnClickListener(tocl);
        tv.addTextChangedListener(new TextWatcherListener(tv));
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

    public void release() {
        activity = null;
        ma = null;
    }

    private void addTextView(TextView tv, String str){
        str = tv.getText().toString() + str;
        tv.setText(str);
    }

    private void addTextView(TextView tv, TextView tvNew, String str) {
        String tvStr = tv.getText().toString();
        String tvNStr = tvNew.getText().toString();

        if(tvNStr.startsWith(FuHao.jian)) {
            if (tvStr.length() == 0 || tvStr.endsWith(FuHao.kuoHaoTou)) {
                tv.setText(tvStr + tvNStr + str);
            } else {
                tv.setText(tvStr + FuHao.kuoHaoTou + tvNStr + FuHao.kuoHaoWei + str);
            }
        } else {
            tv.setText(tvStr + tvNStr + str);
        }
    }

    private String delTextView(int last) {
        String str =  tv.getText().toString();

        return str.substring(0, str.length() - last);
    }

    private boolean jianCeDengHao(){
        return tvNum.getText().toString().contains(FuHao.dengYu);
    }

    private class NumClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (clickSure) {
                runZhenDong(ma.zhenDtongTime);
                runYuYin(view);

                if (tvNum2.getVisibility() == View.VISIBLE) {
                    tvNum2.setVisibility(View.GONE);
                    tvNum.setVisibility(View.VISIBLE);
                    tvNum2.setText(null);
                }

                if (jianCeDengHao()) {
                    setTempHistory(true);
                    tv.setText(FuHao.NULL);
                    tvNum.setText(FuHao.NULL);
                }

                String tN = tvNum.getText().toString();

                if (!tv.getText().toString().endsWith(FuHao.kuoHaoWei)) {
                    if (tN.equals(Nums.nums[0])) {
                            tvNum.setText(FuHao.NULL);
                    }
                    if ((tN.startsWith(FuHao.jian) && tN.length() >= NUM_MAX_LEN+1) || tN.length() >= NUM_MAX_LEN) {
                        Log.d("MainUI$NumClickListener", "数字超长");
                    } else {
                        addTextView(tvNum, ((Button) view).getText().toString());
                        toBottom(sv1);
                        toBottom(sv2);
                    }
                }
            }
        }
    }

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

                runZhenDong(ma.zhenDtongTime);
                if (view.getId() != R.id.bDengyu) {
                    runYuYin(view);
                }

                if (jianCeDengHao() && view.getId() != dengYu.getId()) {
                    // TODO: 连续运算与常规运算的切换
                    SharedPreferences sp = activity.getSharedPreferences("setting", MODE_PRIVATE);
                    String mode = sp.getString("resultsAgainCalculation", activity.getString(R.string.default_resultsAgainCalculation));
                    setTempHistory(true);

                    if (mode.equals(activity.getString(R.string.resultsAgainCalculation_formulaPreferred_key))) {
                        continueByEquation(view, tt);
                    } else if (mode.equals(activity.getString(R.string.resultsAgainCalculation_resultPreferred_key))) {
                        continueByResult(view, ttN);
                    }
                } else {
                    if (view.getId() == jia.getId() || view.getId() == jian.getId() || view.getId() == cheng.getId() || view.getId() == chu.getId()) {  //判断 加减乘除
                        final int jjccEndNum = GuiZe.jjccdEnd(tt);

                        if (jjccEndNum != GuiZe.NO_FUHAO && ttN.length() == 0) {
                            switch (jjccEndNum) {
                                case GuiZe.JIA_NUM:
                                    tt = delTextView(FuHao.jia.length());
                                    break;
                                case GuiZe.JIAN_NUM:
                                    tt = delTextView(FuHao.jian.length());
                                    break;
                                case GuiZe.CHENG_NUM:
                                    tt = delTextView(FuHao.cheng.length());
                                    break;
                                case GuiZe.CHU_NUM:
                                    tt = delTextView(FuHao.chu.length());
                                    break;
                            }

                            tt += ((Button) view).getText();
                            tv.setText(tt);
                            toBottom(sv1);
                            toTop(sv2);
                        } else if (GuiZe.jia_Jian_Cheng_Chu(tt, ttN)) {
                            addTextView(tv, tvNum, bt);
                            tvNum.setText(FuHao.NULL);
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
                            addTextView(tvNum, bt);
                            toBottom(sv1);
                            toBottom(sv2);
                        }
                    } else if (view.getId() == kuoHaoTou.getId()) {   //判断 括号头
                        if (GuiZe.kuoHaoTou(tt, ttN)) {
                            addTextView(tv, FuHao.kuoHaoTou);
                            toBottom(sv1);
                            toTop(sv2);
                        }
                    } else if (view.getId() == kuoHaoWei.getId()) {   //判断 括号尾
                        if (GuiZe.kuoHaoWei(tt, ttN)) {
                            addTextView(tv, tvNum, FuHao.kuoHaoWei);
                            tvNum.setText("");
                            toBottom(sv1);
                            toTop(sv2);
                        }
                    } else if (view.getId() == dengYu.getId()) {    //判断 等号
                        runZhenDong(ma.zhenDtongTimeAdd);

                        //彩蛋
                        if (tt.length() == 0 && ttN.length() == 0) {
                            long mNowTime = System.currentTimeMillis();//获取按键时间

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
                                Toast.makeText(activity, "_(:3」∠)_", Toast.LENGTH_SHORT).show();
                            }

                            if (ma.isOnYuYin()) {
                                ma.au.play(view);
                            }
                        } else if (GuiZe.dengYu(tt, ttN)) {
                            toBottom(sv1);
                            toTop(sv2);

                            if (!ttN.startsWith(FuHao.dengYu)) {
                                addTextView(tv, tvNum, FuHao.NULL);
                            }

                            tvNum.setText(FuHao.NULL);

                            clickSure = false;

                            new Thread() {
                                @Override
                                public void run() {
                                    final String errorText = activity.getString(R.string.errorText);
                                    final String chuLingErrorText = activity.getString(R.string.chuLingErrorText);

                                    try {
                                        final String equation = tv.getText().toString();
                                        String jieGuoStr = new JiSuan(NUM_MAX_LEN).dengYu(equation);
                                        jieGuoStr = Nums.Ling(jieGuoStr);
                                        final String finalJieGuoStr = jieGuoStr;

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvNum.setText(FuHao.dengYu + finalJieGuoStr);
                                                clickSure = true;
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
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvNum.setText("=" + chuLingErrorText);
                                                clickSure = true;
                                            }
                                        });
                                    } catch (RuntimeException e) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvNum.setText("=" + errorText);
                                                clickSure = true;
                                            }
                                        });
                                        e.printStackTrace();
                                    } finally {
                                        //计算结束后，因文本变化要重新调整显示
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                toBottom(sv1);
                                                toTop(sv2);
                                            }
                                        });
                                    }
                                }
                            }.start();
                        } else if (ma.isOnYuYin()) {
                            ma.au.play(view);
                        }
                    }
                }
            }
        }
    }

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
                SharedPreferences sp = activity.getSharedPreferences("setting", MODE_PRIVATE);
                String mode = sp.getString("resultsAgainCalculation", activity.getString(R.string.default_resultsAgainCalculation));
                setTempHistory(true);

                // 长按时操作相反
                if (mode.equals(activity.getString(R.string.resultsAgainCalculation_formulaPreferred_key))) {
                    continueByResult(v, ttN);
                } else if (mode.equals(activity.getString(R.string.resultsAgainCalculation_resultPreferred_key))) {
                    if (v.getId() == dengYu.getId()) {
                        continueByResult(v, ttN);
                    } else {
                        continueByEquation(v, tv.getText().toString());
                    }
                }

                runYuYin(v);
            }

            return true;
        }
    }

    private class CtrlClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (clickSure) {
                if (tvNum2.getVisibility() == View.VISIBLE && view.getId() != huanSuan.getId()) {
                    tvNum2.setVisibility(View.GONE);
                    tvNum.setVisibility(View.VISIBLE);
                    tvNum2.setText(null);
                }

                runZhenDong(ma.zhenDtongTime);
                runYuYin(view);

                if (view.getId() == R.id.bGuiLing && tv.length() == 0 && tvNum.length() == 0) {
                    setTempHistory(false);
                    return;
                }
                if (jianCeDengHao()) {
                    switch (view.getId()) {
                        case R.id.bGuiLing:
                            setTempHistory(true);
                            tv.setText(FuHao.NULL);
                            tvNum.setText(FuHao.NULL);
                            break;
                        case R.id.bShanChu:
                            setTempHistory(true);
                            tvNum.setText(FuHao.NULL);
                            break;
                        case R.id.bZhuanHuan:
                            if (tvNum2.getVisibility() == View.GONE) {
                                String numStr = tvNum.getText().toString().substring(FuHao.dengYu.length());
                                String capsStr = Nums.CapsZH.toCapsZH(numStr, activity);

                                if (capsStr != null && capsStr.length() > 0) {
                                    tvNum2.setText(FuHao.dengYu + capsStr);
                                    tvNum.setVisibility(View.GONE);
                                    tvNum2.setVisibility(View.VISIBLE);
                                }
                            } else {
                                tvNum2.setVisibility(View.GONE);
                                tvNum.setVisibility(View.VISIBLE);
                                tvNum2.setText(FuHao.NULL);
                            }
                            break;
                    }
                } else {
                    if (tvNum.getText().toString().length() > 0) {
                        run(view, tvNum);
                    } else {
                        run(view, tv);
                    }
                }

                String tvText = tv.getText().toString();

                if (tvNum.getText().length() == 0) {
                    int index = jjcckLastEndIndex(tvText);

                    if (index >= 0) {
                        tvNum.setText(tvText.substring(index, tvText.length()));
                        tv.setText(tvText.substring(0, index));
                    } else {
                        tvNum.setText(tvText);
                        tv.setText(FuHao.NULL);
                    }
                }
            }
        }

        private void run(View view, TextView t) {
            switch (view.getId()) {
                case R.id.bGuiLing:
                    runZhenDong(ma.zhenDtongTimeLong);

                    if(t.getId() == tv.getId()){
                        toTop(sv1);
                    } else if (t.getId() == tvNum.getId()) {
                        toBottom(sv1);
                    }

                    toTop(sv2);
                    t.setText(FuHao.NULL);
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

        private int jjcckLastEndIndex(String str) {
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
                runZhenDong(ma.zhenDtongTime);

                if (!jianCeDengHao()) {
                    runYuYin(view);
                    toTop(sv2);

                    if (ttN.startsWith(FuHao.jian)) {
                        tvNum.setText(new StringBuffer(ttN).delete(ttN.indexOf(FuHao.jian), FuHao.jian.length()).toString());
                    } else {
                        ttN = FuHao.jian + ttN;
                        tvNum.setText(ttN);
                    }
                }
            } else if (view.getId() == tv.getId()) {
                nowTime = System.currentTimeMillis();

                if (nowTime - lastTime < 500) {
                    toTop(sv1);
                } else {
                    lastTime = nowTime;
                }
            }
        }
    }

    private class TextOnLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            ClipboardManager myClipboard = (ClipboardManager)activity.getSystemService(CLIPBOARD_SERVICE);  //实例化剪切板服务
            ClipData myClip;

            runZhenDong(ma.zhenDtongTime);

            if(view.getId() == tv.getId()) {
                String tvText = tv.getText().toString();

                if(tvText.trim().length() > 0) {
                    myClip = ClipData.newPlainText("复制的算式", tvText);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(activity, "算式已复制", Toast.LENGTH_SHORT).show();
                }
            } else if (view.getId() == tvNum.getId() || view.getId() == tvNum2.getId()) {

                if(jianCeDengHao()) {
                    String tvNumText = ((TextView) view).getText().toString();
                    myClip = ClipData.newPlainText("复制的结果", tvNumText.substring(FuHao.dengYu.length()));
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(activity, "结果已复制", Toast.LENGTH_SHORT).show();
                }
            }

            return true;    //返回true时，点击和长按可以同时响应
        }

    }

    private class TextWatcherListener implements TextWatcher {
        private TextView view;
        private String oldString;
        private boolean again = false;

        private TextWatcherListener(TextView view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            xianShiChuLi(editable);
            save(editable);

            //文本变色
            if (view.getId() == tv.getId() || view.getId() == tv2.getId() && view.length() > 0) {
                if (oldString == null || !Objects.equals(editable.toString(), oldString) || again) {
                    again = false;
                    oldString = editable.toString();
                    TypedValue value = new TypedValue();
                    activity.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
                    CharSequence cs = TextColorStyles.run(editable.toString(), value.data);
                    editable.replace(0, cs.length(), cs);
                } else {
                    again = true;
                }
            }
        }

        private void save(CharSequence cs) {
            final String string = cs.toString();
            final SharedPreferences.Editor editor = activity.getSharedPreferences("list", MODE_PRIVATE).edit();

            if (view.getId() == tv.getId()) {
                editor.putString("textView0", string);
            } else if (view.getId() == tvNum.getId()) {
                editor.putString("numTextView0", string);
            }

            editor.apply();
        }

        private void xianShiChuLi(CharSequence cs) {
            String tvText = cs.toString();

            if (view.getId() == tvNum.getId()) {      //处理：括号头 与 小数点
                if (tvText.length() > 0) {
                    if (tvText.contains(FuHao.dengYu)) {
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
                int touNun = 0;
                int weiNun = 0;
                int i;

                i = tvText.indexOf(FuHao.kuoHaoTou);
                while (i >= 0) {
                    touNun++;
                    i = tvText.indexOf(FuHao.kuoHaoTou, i + 1);
                }

                i = tvText.indexOf(FuHao.kuoHaoWei);
                while (i >= 0) {
                    weiNun++;
                    i = tvText.indexOf(FuHao.kuoHaoWei, i + 1);
                }

                if (touNun == weiNun) {
                    kuoHaoWei.setVisibility(View.GONE);
                    dengYu.setVisibility(View.VISIBLE);
                } else {
                    kuoHaoWei.setVisibility(View.VISIBLE);
                    dengYu.setVisibility(View.GONE);
                }
            }
        }

    }

    private void toTop(final ScrollView sv) {
        //在新的线程中更新UI
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private void toBottom(final ScrollView sv){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void saveToSql(String equation, String result) {
        HistoryListData.RowData rowDataTemp = HistoryListData.exportFromSQLite(activity.getApplicationContext(), HistoryListData.RowId.ROW_ID_NEWEST_TIME);

        if (rowDataTemp == null || !rowDataTemp.getEquation().equals(equation) || !rowDataTemp.getResult().equals(result)) {
            HistoryListData.RowData rowData = new HistoryListData.RowData(
                    equation,
                    result,
                    System.currentTimeMillis(),
                    false
            );

            HistoryListData.insertToSQLite(rowData, activity.getApplicationContext());
        }
    }

    private void runZhenDong(final long[] zhenDtongTime) {
        new Thread() {
            @Override
            public void run() {
                if (ma.isOnZhenDong()) {
                    ma.zhenDong(zhenDtongTime);
                }
            }
        }.start();
    }

    private void runYuYin(final View view) {
        if (ma.isOnYuYin()) {
            if (ma.isOnTTS()) {
                ma.tts.speak(view);
            } else {
                ma.au.play(view);
            }
        }
    }

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

    public void loadFontSet() {
        SharedPreferences sp = activity.getSharedPreferences("setting", MODE_PRIVATE);
        int fontSizeForEquation = sp.getInt(
                "fontSizeForEquation",
                activity.getResources().getInteger(R.integer.default_fontSizeForEquation)
        );
        int fontSizeForNums = sp.getInt(
                "fontSizeForNums",
                activity.getResources().getInteger(R.integer.default_fontSizeForNums)
        );
        int fontSizeForButton = sp.getInt(
                "fontSizeForButton",
                activity.getResources().getInteger(R.integer.default_fontSizeForButton)
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
        ((Button) activity.findViewById(R.id.b0)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b2)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b3)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b4)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b5)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b6)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b7)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b8)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
        ((Button) activity.findViewById(R.id.b9)).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeForButton);
    }

    private void continueByEquation(View view, String tt) {
        if (view.getId() == jia.getId()
                || view.getId() == jian.getId()
                || view.getId() == cheng.getId()
                || view.getId() == chu.getId()) {
            tvNum.setText(FuHao.NULL);
            tv.setText(tt + ((Button) view).getText());
            toBottom(sv1);
            toTop(sv2);
        }
    }

    private void continueByResult(View v, String ttN) {
        if (v.getId() == jia.getId()
                || v.getId() == jian.getId()
                || v.getId() == cheng.getId()
                || v.getId() == chu.getId()
                || v.getId() == dengYu.getId()) {
            if (ttN.contains(FuHao.TEN_POWER)) {
                String tvStr = FuHao.kuoHaoTou + tv.getText().toString() + FuHao.kuoHaoWei;
                tv.setText(tvStr);
                tvNum.setText(FuHao.NULL);
            } else if (Nums.isNum(ttN.substring(ttN.length() - 1, ttN.length()))) {
                String tvnStr = ttN.substring(FuHao.dengYu.length());
                tvNum.setText(tvnStr);
                tv.setText(FuHao.NULL);
            }

            if (!(v.getId() == dengYu.getId())) {
                v.performClick();
            }

            toTop(sv2);
            toBottom(sv1);
        }
    }

    public void setTempHistory(boolean visibility) {
        if (visibility) {
            SharedPreferences sp = activity.getSharedPreferences("setting", MODE_PRIVATE);

            if (sp.getBoolean(
                    "mainActivityHistoryVisibility",
                    activity.getResources().getBoolean(R.bool.default_mainActivityVisibilityHistory))) {
                if (lastFormula != null && lastResult != null) {
                    tv2.setText(lastFormula + "\n" + FuHao.dengYu + lastResult);
                    tv2.setVisibility(View.VISIBLE);
                } else if (jianCeDengHao()) {
                    tv2.setText(tv.getText() + "\n" + tvNum.getText());
                    tv2.setVisibility(View.VISIBLE);
                }
            }
        } else {
            tv2.setText(FuHao.NULL);
            tv2.setVisibility(View.GONE);
        }
    }
}