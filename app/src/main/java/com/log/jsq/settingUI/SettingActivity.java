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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.log.jsq.R;
import com.log.jsq.aboutUI.AboutActivity;
import com.log.jsq.mainUI.MainActivity;
import com.log.jsq.tool.AudioOnTTS;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    private static Context thisContext;

    public static class SettingFragment
            extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName("setting");
            addPreferencesFromResource(R.xml.setting);
            SharedPreferences sp = thisContext.getSharedPreferences("setting", MODE_PRIVATE);
            sp.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(sp, null);

            ListPreference listPreference = (ListPreference) findPreference("setTTSProgram");
            String[][] info = AudioOnTTS.getEngines(thisContext.getApplicationContext());
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
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (Objects.equals(preference.getKey(), "about")) {
                Intent aboutIntent = new Intent(thisContext.getApplicationContext(), AboutActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(aboutIntent, new Bundle());
            } else if (Objects.equals(preference.getKey(), "fontSizeForEquation")) {
                int value = thisContext.getSharedPreferences(
                        "setting", MODE_PRIVATE).getInt(preference.getKey(),
                        getResources().getInteger(R.integer.default_fontSizeForEquation)
                );
                seekBar(preference.getKey(),
                        preference.getTitle().toString() + getString(R.string.fontSize),
                        value,
                        100,
                        getResources().getInteger(R.integer.default_fontSizeForEquation));
            } else if (Objects.equals(preference.getKey(), "fontSizeForNums")) {
                int value = thisContext.getSharedPreferences(
                        "setting", MODE_PRIVATE).getInt(preference.getKey(),
                        getResources().getInteger(R.integer.default_fontSizeForNums)
                );
                seekBar(preference.getKey(),
                        preference.getTitle().toString() + getString(R.string.fontSize),
                        value,
                        100,
                        getResources().getInteger(R.integer.default_fontSizeForNums));
            } else if (Objects.equals(preference.getKey(), "fontSizeForButton")) {
                int value = thisContext.getSharedPreferences(
                        "setting", MODE_PRIVATE).getInt(preference.getKey(),
                        getResources().getInteger(R.integer.default_fontSizeForButton)
                );
                seekBar(preference.getKey(),
                        preference.getTitle().toString() + getString(R.string.fontSize),
                        value,
                        100,
                        getResources().getInteger(R.integer.default_fontSizeForButton));
            } else if (Objects.equals(preference.getKey(), "translucentStatusBar")
                    || Objects.equals(preference.getKey(), "translucentNavigationBar")) {
                MainActivity.setTheme(thisContext);
            } else if (Objects.equals(preference.getKey(), "restoreSettings")) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("确认重置设置？")
                        .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor spe = thisContext.getSharedPreferences("setting", MODE_PRIVATE).edit();
                                spe.clear();
                                spe.apply();
                                ((Activity) thisContext).finishAfterTransition();
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

            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        private void seekBar(final String key, String title, int value, int maxValue, final int defaultValue) {

            LayoutInflater inflater = ((Activity) thisContext).getLayoutInflater();
            View view = inflater.inflate(R.layout.seek_bar, null);
            final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            final TextView valueText = (TextView) view.findViewById(R.id.value);
            ImageView upButton = (ImageView) view.findViewById(R.id.up);
            ImageView downButton = (ImageView) view.findViewById(R.id.down);

            seekBar.setProgress(value);
            seekBar.setMax(maxValue);
            valueText.setText(String.valueOf(value));
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

            new AlertDialog.Builder(thisContext)
                    .setTitle(title)
                    .setView(view)
                    .setNeutralButton(R.string._default, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = thisContext.getSharedPreferences("setting", MODE_PRIVATE).edit();
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

                            SharedPreferences.Editor editor = thisContext.getSharedPreferences("setting", MODE_PRIVATE).edit();
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
            String onTTSKey = "onTTS";
            String setTTSProgramKey = "setTTSProgram";
            String resultsAgainCalculationKey = "resultsAgainCalculation";

            if (key == null || Objects.equals(key, fontSizeForEquationKey)) {
                int fontSizeForEquationValue = sp.getInt(fontSizeForEquationKey, getResources().getInteger(R.integer.default_fontSizeForEquation));
                findPreference(fontSizeForEquationKey).setSummary(fontSizeForEquationValue + unit);
            }
            if (key == null || Objects.equals(key, fontSizeForNumsKey)) {
                int fontSizeForNumsValue = sp.getInt(fontSizeForNumsKey, getResources()
                        .getInteger(R.integer.default_fontSizeForNums));
                findPreference(fontSizeForNumsKey).setSummary(fontSizeForNumsValue + unit);
            }
            if (key == null || Objects.equals(key, fontSizeForButtonKey)) {
                int fontSizeForButtonValue = sp.getInt(fontSizeForButtonKey, getResources()
                        .getInteger(R.integer.default_fontSizeForButton));
                findPreference(fontSizeForButtonKey).setSummary(fontSizeForButtonValue + unit);
            }
            if (key == null || Objects.equals(key, onTTSKey)) {
                findPreference(setTTSProgramKey)
                        .setEnabled(sp.getBoolean(
                                onTTSKey,
                                getResources().getBoolean(R.bool.default_onTTS)
                        ));
            }
            if (key == null || Objects.equals(key, setTTSProgramKey)) {
                findPreference(setTTSProgramKey)
                        .setSummary(sp.getString(
                                setTTSProgramKey,
                                getString(R.string.default_setTTS_program)
                        ));
            }
            if (key == null || Objects.equals(key, resultsAgainCalculationKey)) {
                String keyKey = sp.getString(
                        resultsAgainCalculationKey,
                        getString(R.string.default_resultsAgainCalculation)
                );
                String[] keys = getResources().getStringArray(R.array.resultsAgainCalculationKey);
                String[] item = getResources().getStringArray(R.array.resultsAgainCalculationItem);

                for (int i = 0; i < keys.length; i++) {
                    if (keys[i].equals(keyKey)) {
                        findPreference(resultsAgainCalculationKey).setSummary(item[i]);
                        break;
                    }
                }
            }
        }

        @Override
        public void onDestroy() {
            thisContext.getSharedPreferences("setting", MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        MainActivity.setTheme(this);
        setActionBar();

        SettingFragment settingFragment = new SettingFragment();
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content,settingFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActionBar() {
        setTitle(getResources().getString(R.string.setting));

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
