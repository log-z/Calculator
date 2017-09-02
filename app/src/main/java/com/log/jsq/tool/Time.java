package com.log.jsq.tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Time {
    public final static long hour = 1000 * 60 * 60;
    public final static long day = hour * 24;
    public final static long year = day * 365;
    public final static String TODAY = "HH:mm";
    private final static String YESTERDAY = "昨天";
    private final static String THE_DAY_BEFORE_YESTERDAY = "前天";
    public final static String THE_YEAR = "M月d日";
    public final static String OTHER = "yyyy年M月d日";

    public enum Span {
        ALL,
        A_WEEK,
        HALF_A_MONTH,
        A_MONTH,
        HALF_A_YEAR,
        A_YEAR
    }

    /**
     * 获取指定时间戳的字符串表示
     * @param time      时间戳的毫秒表示
     * @return          返回指定时间戳的字符串表示
     */
    public static String toString(final long time) {
        // 当前时间（毫秒）
        final long nowTime = System.currentTimeMillis();
        // 当前时区偏移量（毫秒）
        final int timeZone = Calendar.getInstance().getTimeZone().getRawOffset();
        // 时区纠偏
        final long timePlusGMT = time + timeZone;
        final long nowTimePlusGMT = nowTime + timeZone;
        // 日期格式
        SimpleDateFormat formatter;
        // 对应的时间字符串
        String timeStr;

        if (nowTimePlusGMT - timePlusGMT < 0) {
            /// 未来（时间错乱）
            formatter = new SimpleDateFormat (OTHER, Locale.getDefault());
            timeStr = formatter.format(new Date(time));
        } else if (nowTimePlusGMT - timePlusGMT < day) {
            formatter = new SimpleDateFormat(TODAY, Locale.getDefault());
            if (timePlusGMT % day < nowTimePlusGMT % day) {
                /// 今天
                timeStr = formatter.format(new Date(time));
            } else {
                /// 昨天
                timeStr = YESTERDAY + formatter.format(new Date(time));
            }
        } else if (nowTimePlusGMT - timePlusGMT < day * 2) {
            formatter = new SimpleDateFormat(TODAY, Locale.getDefault());
            if (timePlusGMT % day < nowTimePlusGMT % day) {
                /// 昨天
                timeStr = YESTERDAY + formatter.format(new Date(time));
            } else {
                /// 前天
                timeStr = THE_DAY_BEFORE_YESTERDAY + formatter.format(new Date(time));
            }
        } else if (nowTimePlusGMT - timePlusGMT < day * 3) {
            if (timePlusGMT % day < nowTimePlusGMT % day) {
                /// 前天
                formatter = new SimpleDateFormat(TODAY, Locale.getDefault());
                timeStr = THE_DAY_BEFORE_YESTERDAY + formatter.format(new Date(time));
            } else {
                /// 前天之前、今年以内
                formatter = new SimpleDateFormat (OTHER, Locale.getDefault());
                timeStr = formatter.format(new Date(time));
            }
        } else if (nowTimePlusGMT - timePlusGMT < year) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int year = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(nowTime);
            int nowYear = calendar.get(Calendar.YEAR);

            if (nowYear == year) {
                /// 前天之前、今年以内
                formatter = new SimpleDateFormat(THE_YEAR, Locale.getDefault());
                timeStr = formatter.format(new Date(time));
            } else {
                /// 今年以外
                formatter = new SimpleDateFormat (OTHER, Locale.getDefault());
                timeStr = formatter.format(new Date(time));
            }
        } else {
            /// 今年以外
            formatter = new SimpleDateFormat (OTHER, Locale.getDefault());
            timeStr = formatter.format(new Date(time));
        }

        return timeStr;
    }

    public static long getMinTime(Span time) {
        long nowTime = System.currentTimeMillis();

        switch (time) {
            case ALL:
                return nowTime;
            case A_WEEK:
                return nowTime - day * 7;
            case HALF_A_MONTH:
                return nowTime - day * 15;
            case A_MONTH:
                return nowTime - day * 30;
            case HALF_A_YEAR:
                return nowTime - day * 182;
            case A_YEAR:
                return nowTime - day * 365;
            default:
                return -1;
        }
    }
}
