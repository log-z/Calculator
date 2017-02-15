package com.log.jsq.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
    private final static long day = 1000 * 60 * 60 * 24;
    private final static long year = day * 365;
    private final static String TODAY = "HH:mm";
    private final static String YESTERDAY = "昨天";
    private final static String THE_DAY_BEFORE_YESTERDAY = "前天";
    private final static String THE_YEAR = "M月d日";
    private final static String OTHER = "yyyy年M月d日";

    public static String toString(long time) {
        long nowTime = System.currentTimeMillis();
        String timeStr;

        if (nowTime - time < day) {
            if (time % day < nowTime % day) {
                //今天
                SimpleDateFormat formatter = new SimpleDateFormat(TODAY);
                timeStr = formatter.format(new Date(time));
            } else {
                //昨天
                timeStr = YESTERDAY;
            }
        } else if (nowTime - time < day * 2) {
            if (time % day < nowTime % day) {
                //昨天
                timeStr = YESTERDAY;
            } else {
                //前天
                timeStr = THE_DAY_BEFORE_YESTERDAY;
            }
        } else if (nowTime - time < day * 3) {
            if (time % day < nowTime % day) {
                //前天
                timeStr = THE_DAY_BEFORE_YESTERDAY;
            } else {
                //前天之前
                SimpleDateFormat formatter = new SimpleDateFormat (OTHER);
                timeStr = formatter.format(new Date(time));
            }
        } else if (nowTime - time < year) {
            //前天之前
            SimpleDateFormat formatter = new SimpleDateFormat (THE_YEAR);
            timeStr = formatter.format(new Date(time));
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat (OTHER);
            timeStr = formatter.format(new Date(time));
        }

        return timeStr;
    }
}
