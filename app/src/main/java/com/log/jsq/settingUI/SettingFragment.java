package com.log.jsq.settingUI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.log.jsq.R;
import com.log.jsq.aboutUI.AboutActivity;
import com.log.jsq.tool.AudioOnTTS;
import com.log.jsq.tool.Theme;

import java.util.Objects;

/**
 * Created by log on 2017/8/21.
 */

public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context mContext;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        sharedPreferences = mContext.getSharedPreferences("setting", Context.MODE_PRIVATE);

        // 指定SharedPreferences
        getPreferenceManager().setSharedPreferencesName("setting");
        // 指定xml资源
        addPreferencesFromResource(R.xml.setting);
        // 注册SharedPreferences监听器
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        // 初始化summary
        onSharedPreferenceChanged(sharedPreferences, null);

        // 初始化“选择TTS引擎”选项
        ListPreference listPreference = (ListPreference) findPreference("setTTSProgram");
        String[][] info = AudioOnTTS.getEngines(mContext.getApplicationContext());
        String[] label = info[0];
        String[] name = info[1];
        if (label.length > 0 && name.length > 0) {
            listPreference.setEntries(label);
            listPreference.setEntryValues(name);
        } else {
            String[] engines = new String[] {getString(R.string.default_setTTS_program)};
            listPreference.setEntries(engines);
            listPreference.setEntryValues(engines);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen ps, Preference p) {
        if (Objects.equals(p.getKey(), "about")) {
            /// 关于
            Intent aboutIntent =
                    new Intent(mContext.getApplicationContext(), AboutActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(aboutIntent, new Bundle());
        } else if (Objects.equals(p.getKey(), "fontSizeForEquation")) {
            /// 算式区字体大小
            int value = sharedPreferences.getInt(p.getKey(),
                    getResources().getInteger(R.integer.default_fontSizeForEquation));
            seekBar(p.getKey(),
                    p.getTitle().toString() + getString(R.string.fontSize),
                    value,
                    100,
                    getResources().getInteger(R.integer.default_fontSizeForEquation));
        } else if (Objects.equals(p.getKey(), "fontSizeForNums")) {
            /// 数值区字体大小
            int value = sharedPreferences.getInt(p.getKey(),
                    getResources().getInteger(R.integer.default_fontSizeForNums));
            seekBar(p.getKey(),
                    p.getTitle().toString() + getString(R.string.fontSize),
                    value,
                    100,
                    getResources().getInteger(R.integer.default_fontSizeForNums));
        } else if (Objects.equals(p.getKey(), "fontSizeForButton")) {
            /// 按钮字体大小
            int value = sharedPreferences.getInt(p.getKey(),
                    getResources().getInteger(R.integer.default_fontSizeForButton));
            seekBar(p.getKey(),
                    p.getTitle().toString() + getString(R.string.fontSize),
                    value,
                    100,
                    getResources().getInteger(R.integer.default_fontSizeForButton));
        } else if (Objects.equals(p.getKey(), "restoreSettings")) {
            /// 重置设置
            new AlertDialog.Builder(getActivity())
                    .setTitle("确认重置设置？")
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            ((Activity) mContext).finishAfterTransition();
                        }
                    })
                    .setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        }

        return super.onPreferenceTreeClick(ps, p);
    }

    /**
     * 拖动条弹窗
     * @param key               对应的SharedPreferences key
     * @param title             弹窗标题
     * @param value             当前值
     * @param maxValue          最大值
     * @param defaultValue      默认值
     */
    private void seekBar(final String key,
                         String title,
                         int value,
                         int maxValue,
                         final int defaultValue) {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.seek_bar, null);
        final SeekBar seekBar = view.findViewById(R.id.seekBar);
        final TextView valueText = view.findViewById(R.id.value);
        ImageView upButton = view.findViewById(R.id.up);
        ImageView downButton = view.findViewById(R.id.down);

        // 初始化值
        seekBar.setProgress(value);
        // 设置最大值
        seekBar.setMax(maxValue);
        // 显示当前值
        valueText.setText(String.valueOf(value));
        // 注册拖动条监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 同步显示当前值
                valueText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                onProgressChanged(seekBar, seekBar.getProgress(), true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onProgressChanged(seekBar, seekBar.getProgress(), true);

            }
        });
        // 注册按钮监听
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.valueOf(valueText.getText().toString());

                if (value < seekBar.getMax()) {
                    value += 1;
                    valueText.setText(String.valueOf(value));
                    seekBar.setProgress(value);
                }
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.valueOf(valueText.getText().toString());

                if (value > 1) {
                    value -= 1;
                    valueText.setText(String.valueOf(value));
                    seekBar.setProgress(value);
                }
            }
        });

        // 构造并显示弹窗
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(view)
                .setNeutralButton(R.string._default, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(key, defaultValue);
                        editor.apply();
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int size = Integer.valueOf(valueText.getText().toString());
                        if (size <= 0) {
                            size = 1;
                        }

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(key, size);
                        editor.apply();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        String unit = "sp";
        String fontSizeForEquationKey = "fontSizeForEquation";
        String fontSizeForNumsKey = "fontSizeForNums";
        String fontSizeForButtonKey = "fontSizeForButton";
        String statusBarKey = "statusBar";
        String navigationBarKey = "navigationBar";
        String onTTSKey = "onTTS";
        String setTTSProgramKey = "setTTSProgram";
        String resultsAgainCalculationKey = "resultsAgainCalculation";
        String autoLineFeedKey = "autoLineFeed";
        String historyDeleteAutoKey = "historyDeleteAuto";

        // 算式区字体大小
        if (key == null || Objects.equals(key, fontSizeForEquationKey)) {
            int fontSizeForEquationValue = sp.getInt(fontSizeForEquationKey,
                    getResources().getInteger(R.integer.default_fontSizeForEquation));
            findPreference(fontSizeForEquationKey).setSummary(fontSizeForEquationValue + unit);
        }
        // 数值区字体大小
        if (key == null || Objects.equals(key, fontSizeForNumsKey)) {
            int fontSizeForNumsValue = sp.getInt(fontSizeForNumsKey, getResources()
                    .getInteger(R.integer.default_fontSizeForNums));
            findPreference(fontSizeForNumsKey).setSummary(fontSizeForNumsValue + unit);
        }
        // 按钮字体大小
        if (key == null || Objects.equals(key, fontSizeForButtonKey)) {
            int fontSizeForButtonValue = sp.getInt(fontSizeForButtonKey, getResources()
                    .getInteger(R.integer.default_fontSizeForButton));
            findPreference(fontSizeForButtonKey).setSummary(fontSizeForButtonValue + unit);
        }
        // 状态栏
        if (key == null || Objects.equals(key, statusBarKey)) {
            String keyKey = sp.getString(
                    statusBarKey,
                    getString(R.string.default_statusBar));
            String[] keys = getResources().getStringArray(R.array.statusBar_key);
            String[] item = getResources().getStringArray(R.array.statusBar_item);

            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(keyKey)) {
                    findPreference(statusBarKey).setSummary(item[i]);
                    break;
                }
            }

            Theme.setStatusBar((Activity) mContext);
        }
        // 导航栏
        if (key == null || Objects.equals(key, navigationBarKey)) {
            String keyKey = sp.getString(
                    navigationBarKey,
                    getString(R.string.default_navigationBar));
            String[] keys = getResources().getStringArray(R.array.navigationBar_key);
            String[] item = getResources().getStringArray(R.array.navigationBar_item);

            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(keyKey)) {
                    findPreference(navigationBarKey).setSummary(item[i]);
                    break;
                }
            }

            Theme.setNavigationBar((Activity) mContext);
        }
        // 使用TTS引擎代替自带语音
        if (key == null || Objects.equals(key, onTTSKey)) {
            // 控制“setTTSProgram”是否可编辑
            findPreference(setTTSProgramKey)
                    .setEnabled(sp.getBoolean(
                            onTTSKey,
                            getResources().getBoolean(R.bool.default_onTTS)
                    ));
        }
        // 选择TTS引擎
        if (key == null || Objects.equals(key, setTTSProgramKey)) {
            findPreference(setTTSProgramKey)
                    .setSummary(sp.getString(
                            setTTSProgramKey,
                            getString(R.string.default_setTTS_program)
                    ));
        }
        // 对结果计算时
        if (key == null || Objects.equals(key, resultsAgainCalculationKey)) {
            String keyKey = sp.getString(
                    resultsAgainCalculationKey,
                    getString(R.string.default_resultsAgainCalculation));
            String[] keys = getResources()
                    .getStringArray(R.array.resultsAgainCalculationKey);
            String[] item = getResources()
                    .getStringArray(R.array.resultsAgainCalculationItem);

            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(keyKey)) {
                    findPreference(resultsAgainCalculationKey).setSummary(item[i]);
                    break;
                }
            }
        }
        // 自动换行
        if (key == null || Objects.equals(key, autoLineFeedKey)) {
            String keyKey = sp.getString(
                    autoLineFeedKey,
                    getString(R.string.default_autoLineFeed));
            String[] keys = getResources().getStringArray(R.array.autoLineFeed_key);
            String[] item = getResources().getStringArray(R.array.autoLineFeed_item);

            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(keyKey)) {
                    findPreference(autoLineFeedKey).setSummary(item[i]);
                    break;
                }
            }
        }
        // 自动删除历史记录
        if (key == null || Objects.equals(key, historyDeleteAutoKey)) {
            String keyKey = sp.getString(
                    historyDeleteAutoKey,
                    getString(R.string.default_historyDeleteAuto));
            String[] keys = getResources().getStringArray(R.array.historyDeleteAuto_key);
            String[] item = getResources().getStringArray(R.array.historyDeleteAuto_item);

            for (int i = 0;i < keys.length; i++) {
                if (keys[i].equals(keyKey)) {
                    findPreference(historyDeleteAutoKey).setSummary(item[i]);
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除SharedPreferences监听器
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        // 释放引用
        mContext = null;
    }
}
