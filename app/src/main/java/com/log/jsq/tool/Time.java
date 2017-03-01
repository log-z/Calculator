package com.log.jsq.tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {
    private final static long hour = 1000 * 60 * 60;
    private final static long day = hour * 24;
    private final static long year = day * 365;
    private final static String TODAY = "HH:mm";
    private final static String YESTERDAY = "昨天";
    private final static String THE_DAY_BEFORE_YESTERDAY = "前天";
    private final static String THE_YEAR = "M月d日";
    private final static String OTHER = "yyyy年M月d日";

    public enum time {
        ALL,
        A_WEEK,
        HALF_A_MONTH,
        A_MONTH,
        HALF_A_YEAR,
        A_YEAR
    }

    public static String toString(final long time) {
        final long nowTime = System.currentTimeMillis();
        final int timeZone = Calendar
                .getInstance()
                .getTimeZone()
                .getRawOffset();
        final long timePlusGMT = time + timeZone;
        final long nowTimePlusGMT = nowTime + timeZone;
        String timeStr;

        if (nowTimePlusGMT - timePlusGMT < 0) {
            //未来（时间错乱）
            SimpleDateFormat formatter = new SimpleDateFormat (OTHER);
            timeStr = formatter.format(new Date(time));
        } else if (nowTimePlusGMT - timePlusGMT < day) {
            if (timePlusGMT % day < nowTimePlusGMT % day) {
                //今天
                SimpleDateFormat formatter = new SimpleDateFormat(TODAY);
                timeStr = formatter.format(new Date(time));
            } else {
                //昨天
                timeStr = YESTERDAY;
            }
        } else if (nowTimePlusGMT - timePlusGMT < day * 2) {
            if (timePlusGMT % day < nowTimePlusGMT % day) {
                //昨天
                timeStr = YESTERDAY;
            } else {
                //前天
                timeStr = THE_DAY_BEFORE_YESTERDAY;
            }
        } else if (nowTimePlusGMT - timePlusGMT < day * 3) {
            if (timePlusGMT % day < nowTimePlusGMT % day) {
                //前天
                timeStr = THE_DAY_BEFORE_YESTERDAY;
            } else {
                //前天之前、今年以内
                SimpleDateFormat formatter = new SimpleDateFormat (OTHER);
                timeStr = formatter.format(new Date(time));
            }
        } else if (nowTimePlusGMT - timePlusGMT < year) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int year = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(nowTime);
            int nowYear = calendar.get(Calendar.YEAR);

            if (nowYear == year) {
                //前天之前、今年以内
                SimpleDateFormat formatter = new SimpleDateFormat(THE_YEAR);
                timeStr = formatter.format(new Date(time));
            } else {
                //今年以外
                SimpleDateFormat formatter = new SimpleDateFormat (OTHER);
                timeStr = formatter.format(new Date(time));
            }
        } else {
            //今年以外
            SimpleDateFormat formatter = new SimpleDateFormat (OTHER);
            timeStr = formatter.format(new Date(time));
        }

        return timeStr;
    }

    public static long getMinTime(time time) {
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
