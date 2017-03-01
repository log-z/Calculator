package com.log.jsq.mainUI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.log.jsq.aboutUI.AboutActivity;
import com.log.jsq.tool.Audio;
import com.log.jsq.library.FuHao;
import com.log.jsq.library.Nums;
import com.log.jsq.R;
import com.log.jsq.historyUI.HistoryListActivity;
import com.log.jsq.tool.Open;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private long mPressedTime = 0;
    public Audio au = null;
    private Vibrator mVibrator = null;
    public final long[] zhenDtongTime = {0, 35};
    public final long[] zhenDtongTimeLong = {0, 60};
    public final long[] zhenDtongTimeAdd = {0, 50, 120, 50};
    private HashMap<String, MenuItem> hashMap = new HashMap<String, MenuItem>();
    private boolean onYuYin = false;
    private boolean onZhenDong = false;
    private Activity thisActivity = this;
    private MainUI mainUI;

    @Override
    protected void finalize() throws Throwable  {
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        Log.d("MainActivity", "////////////////////// " + this + "已可以回收 ////////////////////////");
        super.finalize();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(this);
        setContentView(R.layout.activity_main);

        loadingSever();
        loadingString();

        mainUI = new MainUI(this);
        mainUI.run();

        versionDetection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recover();
    }

    @Override
    protected void onPause() {
        if (au != null) {
            au.stopSoundThread();
        }

        super.onPause();
    }

    @Override
    public void finish() {
        releaseSever(false, false);

        if (hashMap != null) {
            hashMap.clear();
        }

        if (mainUI != null) {
            mainUI.release();
            mainUI = null;
        }

        thisActivity = null;

        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final SharedPreferences read = getSharedPreferences("item", MODE_PRIVATE);
        final String str0 = getString(R.string.zhenDong);
        final String str1 = getString(R.string.yuYin);
        final String str2 = getString(R.string.theme);
        final String str3 = getString(R.string.about);
        final String historyStr = getString(R.string.history);
        boolean checked;

        MenuItem item0 = menu.add(str0);
        item0.setCheckable(true);
        checked = read.getBoolean("zhenDong", false);
        item0.setChecked(checked);
        onZhenDong = checked;

        MenuItem item1 = menu.add(str1);
        item1.setCheckable(true);
        checked = read.getBoolean("yuYin", false);
        item1.setChecked(checked);
        onYuYin = checked;

        MenuItem item2 = menu.add(str2);
        MenuItem item3 = menu.add(str3);

        MenuItem history = menu.add(historyStr);
        history.setIcon(R.drawable.history_icon);
        history.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        hashMap.put(str0, item0);
        hashMap.put(str1, item1);
        hashMap.put(str2, item2);
        hashMap.put(str3, item3);
        hashMap.put(historyStr, history);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean temp;

        if( item.isCheckable() ){
            final SharedPreferences.Editor editor = getSharedPreferences("item", MODE_PRIVATE).edit();    //存储数据

            if( !item.isChecked() ){
                item.setChecked(true);
                temp = true;
            } else {
                item.setChecked(false);
                temp = false;
            }

            if( item == hashMap.get( getString(R.string.zhenDong) ) ) {
                onZhenDong = temp;
                editor.putBoolean("zhenDong", temp);
            } else if( item == hashMap.get( getString(R.string.yuYin) ) ){
                onYuYin = temp;
                editor.putBoolean("yuYin", temp);
            }

            editor.apply();

            new Thread() {
                @Override
                public void run() {
                    startSever(onZhenDong, onYuYin);
                    releaseSever(onZhenDong, onYuYin);
                }
            }.start();
        } else {
            if (item == hashMap.get(getString(R.string.history))) {
                Intent intent = new Intent(getApplicationContext(), HistoryListActivity.class)
                        .putExtra("startFrom", getClass().toString())
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent, new Bundle());
            } else if (item == hashMap.get(getString(R.string.theme))) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.theme))
                        .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create();
                dialog.setView(getColorPickerView(dialog));
                dialog.show();
            } else if (item == hashMap.get(getString(R.string.about))) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent, new Bundle());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void zhenDong(long[] t){
        if(onZhenDong) {
            mVibrator.vibrate(t, -1);
        }
    }

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间

        if((mNowTime - mPressedTime) > 1000){//比较两次按键时间差
            Toast.makeText(this, "再按一次退出计算器", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        }
        else{//退出程序
            final SharedPreferences.Editor editor = getSharedPreferences("list", MODE_PRIVATE).edit();
            editor.putBoolean("normal", true);
            editor.putString("textView0", FuHao.NULL);
            editor.putString("numTextView0", FuHao.NULL);
            editor.apply();

            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU) {  //MENU键
            return true;       //监控/拦截菜单键
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    //恢复意外退出之前的内容
    private void recover() {
        new Thread() {
            @Override
            public void run() {
                final SharedPreferences read = getSharedPreferences("list", MODE_PRIVATE);

                if (read.getBoolean("normal", false)) {
                    final SharedPreferences.Editor editor = getSharedPreferences("list", MODE_PRIVATE).edit();
                    editor.putBoolean("normal", false);
                    editor.apply();
                } else {
                    final TextView textView = (TextView) findViewById(R.id.textView);
                    final TextView numTextView = (TextView) findViewById(R.id.textViewNum);
                    final String textViewStr = read.getString("textView0", FuHao.NULL);
                    final String numTextViewStr = read.getString("numTextView0", FuHao.NULL);

                    runOnUiThread(new Runnable() {
                       @Override
                        public void run() {
                            textView.setText(textViewStr);
                            numTextView.setText(numTextViewStr);
                            mainUI.flushTextColor();
                        }
                    });
                }
            }
        }.start();
    }

    private void loadingSever() {
        new Thread() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("item", MODE_PRIVATE);

                startSever(sp.getBoolean("zhenDong", false), sp.getBoolean("yuYin", false));
            }
        }.start();
    }

    private void startSever(final boolean zhenDong, final boolean yuYin) {
        if (zhenDong) {
            if (mVibrator == null) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }
        }

        if (yuYin) {
            if (au == null) {
                au = new Audio(thisActivity);
            }

            au.loading();
        }
    }

    private void releaseSever(final boolean zhenDong, final boolean yuYin) {
        if (!zhenDong && mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }

        if (!yuYin && au != null) {
            au.stopSoundThread();
            au.release();
            au = null;
        }
    }

    private void loadingString() {
        FuHao.luRu(getApplicationContext());
        Nums.luRu(getApplicationContext());
    }

    public boolean isOnZhenDong() {
        return onZhenDong;
    }

    public boolean isOnYuYin() {
        return onYuYin;
    }

    private void restart() {
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static void setTheme(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("item", MODE_PRIVATE);
        int themeId = preferences.getInt("theme", 0);

        switch (themeId) {
            case R.style.AppTheme_Purple:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Green:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_DeepOrange:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Pink:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Grey:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_DeepPurple:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Blue:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Teal:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Amber:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Red:
                context.setTheme(themeId);
                break;
            case R.style.AppTheme_Brown:
                context.setTheme(themeId);
                break;
            default:
                context.setTheme(R.style.AppTheme_Indigo);
        }
    }

    private View getColorPickerView(final AlertDialog dialog) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.color_picker, null);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor spe = getSharedPreferences("item", MODE_PRIVATE).edit();

                switch (v.getId()) {
                    case R.id.itemPurple:
                        spe.putInt("theme", R.style.AppTheme_Purple);
                        break;
                    case R.id.itemGreen:
                        spe.putInt("theme", R.style.AppTheme_Green);
                        break;
                    case R.id.itemDeepOrange:
                        spe.putInt("theme", R.style.AppTheme_DeepOrange);
                        break;
                    case R.id.itemPink:
                        spe.putInt("theme", R.style.AppTheme_Pink);
                        break;
                    case R.id.itemGrey:
                        spe.putInt("theme", R.style.AppTheme_Grey);
                        break;
                    case R.id.itemDeepPurple:
                        spe.putInt("theme", R.style.AppTheme_DeepPurple);
                        break;
                    case R.id.itemBlue:
                        spe.putInt("theme", R.style.AppTheme_Blue);
                        break;
                    case R.id.itemTeal:
                        spe.putInt("theme", R.style.AppTheme_Teal);
                        break;
                    case R.id.itemOrange:
                        spe.putInt("theme", R.style.AppTheme_Amber);
                        break;
                    case R.id.itemRed:
                        spe.putInt("theme", R.style.AppTheme_Red);
                        break;
                    case R.id.itemBrown:
                        spe.putInt("theme", R.style.AppTheme_Brown);
                        break;
                    default:
                        spe.putInt("theme", R.style.AppTheme_Indigo);
                }

                spe.apply();
                dialog.cancel();
                restart();
            }
        };

        TextView itemPurple = (TextView) rootView.findViewById(R.id.itemPurple);
        TextView itemIndigo = (TextView) rootView.findViewById(R.id.itemIndigo);
        TextView itemGreen = (TextView) rootView.findViewById(R.id.itemGreen);
        TextView itemDeepOrange = (TextView) rootView.findViewById(R.id.itemDeepOrange);
        TextView itemPink = (TextView) rootView.findViewById(R.id.itemPink);
        TextView itemGrey = (TextView) rootView.findViewById(R.id.itemGrey);
        TextView itemDeepPurple = (TextView) rootView.findViewById(R.id.itemDeepPurple);
        TextView itemBlue = (TextView) rootView.findViewById(R.id.itemBlue);
        TextView itemTeal = (TextView) rootView.findViewById(R.id.itemTeal);
        TextView itemAmber = (TextView) rootView.findViewById(R.id.itemOrange);
        TextView itemRed = (TextView) rootView.findViewById(R.id.itemRed);
        TextView itemBrown = (TextView) rootView.findViewById(R.id.itemBrown);

        itemPurple.setOnClickListener(clickListener);
        itemIndigo.setOnClickListener(clickListener);
        itemGreen.setOnClickListener(clickListener);
        itemDeepOrange.setOnClickListener(clickListener);
        itemPink.setOnClickListener(clickListener);
        itemGrey.setOnClickListener(clickListener);
        itemDeepPurple.setOnClickListener(clickListener);
        itemBlue.setOnClickListener(clickListener);
        itemTeal.setOnClickListener(clickListener);
        itemAmber.setOnClickListener(clickListener);
        itemRed.setOnClickListener(clickListener);
        itemBrown.setOnClickListener(clickListener);

        SharedPreferences preferences = getSharedPreferences("item", MODE_PRIVATE);
        switch (preferences.getInt("theme", 0)) {
            case R.style.AppTheme_Purple:
                itemPurple.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Green:
                itemGreen.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_DeepOrange:
                itemDeepOrange.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Pink:
                itemPink.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Grey:
                itemGrey.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_DeepPurple:
                itemDeepPurple.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Blue:
                itemBlue.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Teal:
                itemTeal.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Amber:
                itemAmber.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Red:
                itemRed.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            case R.style.AppTheme_Brown:
                itemBrown.setBackground(getDrawable(R.drawable.yuan_double));
                break;
            default:
                itemIndigo.setBackground(getDrawable(R.drawable.yuan_double));
        }

        return rootView;
    }

    private void versionDetection() {
        new Thread() {
            @Override
            public void run() {
                int versionCode;
                SharedPreferences sp = getSharedPreferences("date", MODE_PRIVATE);

                try {
                    PackageManager manager = getPackageManager();
                    PackageInfo info = manager.getPackageInfo(thisActivity.getPackageName(), 0);
                    versionCode = info.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    versionCode = Integer.MAX_VALUE;
                }

                if (versionCode > sp.getInt("versionCode", 0)) {
                    final String updateLog = Open.openTxt(getApplicationContext(), R.raw.update_log);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(thisActivity)
                                    .setTitle(getString(R.string.updateLog))
                                    .setMessage(updateLog)
                                    .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.helpWord), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            AboutActivity.openHelp(thisActivity);
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    });
                }

                SharedPreferences.Editor spe = getSharedPreferences("date", MODE_PRIVATE).edit();
                spe.putInt("versionCode", versionCode);
                spe.apply();
            }
        }.start();
    }

}
