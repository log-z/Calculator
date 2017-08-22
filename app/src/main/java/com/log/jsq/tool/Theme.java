package com.log.jsq.tool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Window;

import com.log.jsq.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by log on 2017/7/29.
 */

public class Theme {

    /**
     * 配置主题
     * @param activity  指定的activity
     */
    public static void setTheme(Activity activity) {
        SharedPreferences preferences = activity
                .getSharedPreferences("item", MODE_PRIVATE);
        int themeId = preferences.getInt("theme", 0);

        switch (themeId) {
            case R.style.AppTheme_Purple:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Green:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_DeepOrange:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Pink:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Grey:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_DeepPurple:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Indigo:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Teal:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Amber:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Red:
                activity.setTheme(themeId);
                break;
            case R.style.AppTheme_Brown:
                activity.setTheme(themeId);
                break;
            default:
                activity.setTheme(R.style.AppTheme_Blue);
        }

        setStatusBar(activity);
        setNavigationBar(activity);
    }

    /**
     * 设置状态栏背景色
     * @param activity  指定的activity
     */
    public static void setStatusBar(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences("setting", MODE_PRIVATE);
        String style = sp.getString(
                "statusBar",
                activity.getResources().getString(R.string.default_statusBar));
        Window window = activity.getWindow();
        TypedValue typedValue = new TypedValue();

        switch (style) {
            // 透明
            case "transparent":
                activity.getTheme().resolveAttribute(
                        R.attr.colorPrimary,
                        typedValue,
                        true);
                break;
            // 半透明
            case "translucent":
                activity.getTheme().resolveAttribute(
                        R.attr.colorPrimaryDark,
                        typedValue,
                        true);
                break;
            // 省电黑
            case "black":
                typedValue.data = Color.BLACK;
                break;
            default: return;
        }

        window.setStatusBarColor(typedValue.data);
    }

    /**
     * 设置导航栏背景色
     * @param activity  指定的activity
     */
    public static void setNavigationBar(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences("setting", MODE_PRIVATE);
        String style = sp.getString(
                "navigationBar",
                activity.getResources().getString(R.string.default_navigationBar));
        Window window = activity.getWindow();
        TypedValue typedValue = new TypedValue();

        switch (style) {
            // 透明
            case "transparent":
                activity.getTheme().resolveAttribute(
                        R.attr.colorPrimary,
                        typedValue,
                        true);
                break;
            // 半透明
            case "translucent":
                activity.getTheme().resolveAttribute(
                        R.attr.colorPrimaryDark,
                        typedValue,
                        true);
                break;
             // 省点黑
            case "black":
                typedValue.data = Color.BLACK;
                break;
            default: return;
        }

        window.setNavigationBarColor(typedValue.data);
    }

}
