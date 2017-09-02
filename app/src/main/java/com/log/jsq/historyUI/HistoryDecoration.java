package com.log.jsq.historyUI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.log.jsq.R;
import com.log.jsq.tool.Time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by log on 2017/8/19.
 */

public class HistoryDecoration extends RecyclerView.ItemDecoration {

    /** 分割线大小 */
    private static final int DECORATION_SIZE = 3;
    /** 目前时间 */
    private final long NOW_TIME;
    /** 目前时区 */
    private final TimeZone TIME_ZONE;
    /** Section背景画笔 */
    private Paint mBgPaint;
    /** 回调 */
    private DecorationCallback mCallback;
    /** Section高度 */
    private final float mSectionHeight;
    /** Section标题文本画笔 */
    private TextPaint mTextPaint;
    /** Section标题左边距 */
    private final float mTitleMarginLeft;
    /** Section标题基线与Section底部距离 */
    private final float mTitleBaselineToBottom;

    /**
     * 回调
     */
    public interface DecorationCallback {

        /**
         * 获取条目的时间
         * @param position  条目位置
         * @return          返回条目的时间
         */
        long getItemTime(int position);

        /**
         * 确认是否为重要条目
         * @param position  条目位置
         * @return          返回指定条目是否为重要条目
         */
        boolean isImportantItem(int position);

    }

    /**
     * 时间段
     */
    public static class TimeSpan {

        /** 日期格式 */
        static final String DATE_FORMAT = "M月";
        /** 长日期格式 */
        static final String DATE_FORMAT_LONG = "yyyy年M月";
        /** 缓存 */
        private static final ArrayList<TimeSpan> mCache = new ArrayList<>();
        /** 时间段开始时间 */
        final long startTime;
        /** 时间段标签 */
        final Tag tag;

        /**
         * 时间段标签
         */
        public enum Tag {
            /** 今天 */
            TODAY,
            /** 昨天 */
            YESTERDAY,
            /** 前天 */
            THE_DAY_BEFORE_YESTERDAY,
            /** 一周（7天）内 */
            A_WEEK,
            /** 不到一个月的时间段 */
            LESS_THAN_A_MONTH,
            /** 一个月 */
            A_MONTH
        }

        /**
         * 构造方法
         * @param startTime     时间段开始时间
         * @param tag           时间段标签
         */
        TimeSpan(long startTime, Tag tag) {
            this.startTime = startTime;
            this.tag = tag;
        }

        /**
         * 清理缓存
         */
        static void clear() {
            mCache.clear();
        }

        /**
         * 比较对象
         * @param obj   待比较的对象
         * @return      指定对象与自身是否相等
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof TimeSpan) {
                TimeSpan times = (TimeSpan) obj;
                return startTime == times.startTime && tag == times.tag;
            } else {
                return false;
            }
        }

        /**
         * 获取时间段（并缓存）
         * @param time      指定时间
         * @param nowTime   现在的时间
         * @param timeZone  时区信息
         * @return          返回指定时间所在的时间段
         */
        static TimeSpan getTimeSpan(long time, long nowTime, TimeZone timeZone) {
            if (time < 0) {
                return null;
            }

            /// 先从缓存中匹配
            for (TimeSpan timeSpan : mCache) {
                // 比对是否匹配
                if (timeSpan.startTime < time) {
                    return timeSpan;
                }
            }

            /// 缓存中没有匹配成功，则构建新时间段缓存并进行匹配
            // 新时间段
            TimeSpan timeSpan;
            // 获取日历
            Calendar calendar = Calendar.getInstance(timeZone);
            // 如果缓存为空
            if (mCache.size() == 0) {
                /// 构建“今天”
                // 设置日历时间
                calendar.setTimeInMillis(nowTime);
                // 清空小时
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                // 清空分钟
                calendar.set(Calendar.MINUTE, 0);
                // 清空秒
                calendar.set(Calendar.SECOND, 0);
                // 清空毫秒
                calendar.set(Calendar.MILLISECOND, 0);
                timeSpan = new TimeSpan(calendar.getTimeInMillis(), Tag.TODAY);
                mCache.add(timeSpan);
                if (timeSpan.startTime < time) {
                    return timeSpan;
                }
            }

            // 新的时间段开始时间
            long newStartTime;
            // 新的标签
            Tag newTag;
            // 取出最早的时间段
            timeSpan = mCache.get(mCache.size() - 1);
            // 不断比对是否与新时间段匹配
            while (true) {
                switch (timeSpan.tag) {
                    /// 构造“昨天”
                    case TODAY:
                        // “今天”后退一天为“昨天”
                        newStartTime = timeSpan.startTime - Time.day;
                        newTag = Tag.YESTERDAY;
                        break;
                    /// 构建“前天”
                    case YESTERDAY:
                        // “昨天”后退一天为“前天”
                        newStartTime = timeSpan.startTime - Time.day;
                        newTag = Tag.THE_DAY_BEFORE_YESTERDAY;
                        break;
                    /// 构建“一周（7天）内”
                    case THE_DAY_BEFORE_YESTERDAY:
                        // “前天”后退四天为“一周（7天）内”
                        newStartTime = timeSpan.startTime - Time.day * 4;
                        newTag = Tag.A_WEEK;
                        break;
                    /// 构建“这个月剩下的时间”
                    case A_WEEK:
                        calendar.setTimeInMillis(timeSpan.startTime);
                        if (calendar.get(Calendar.DAY_OF_MONTH) > 1) {
                            // 不是每月的一号，则清空号数
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                        } else {
                            // 否则后退一个月
                            calendar.add(Calendar.MONTH, -1);
                        }
                        newStartTime = calendar.getTimeInMillis();
                        newTag = Tag.LESS_THAN_A_MONTH;
                        break;
                    /// 构建“前一个月”
                    case LESS_THAN_A_MONTH:
                        calendar.setTimeInMillis(timeSpan.startTime);
                        calendar.add(Calendar.MONTH, -1);
                        newStartTime = calendar.getTimeInMillis();
                        newTag = Tag.A_MONTH;
                        break;
                    /// 构建“前一个月”
                    case A_MONTH:
                        calendar.setTimeInMillis(timeSpan.startTime);
                        calendar.add(Calendar.MONTH, -1);
                        newStartTime = calendar.getTimeInMillis();
                        newTag = Tag.A_MONTH;
                        break;
                    default:
                        return null;
                }

                // 构建新时间段
                timeSpan = new TimeSpan(newStartTime, newTag);
                // 加入缓存
                mCache.add(timeSpan);
                // 比对新时间段是否匹配
                if (timeSpan.startTime < time) {
                    return timeSpan;
                }
            }
        }

        /**
         * 通过标签从缓存中获取时间段
         * 不支持“月份”时间段的获取，它可能返回任意月份
         * @param tag   标签
         * @return      指定标签的时间段
         */
        static TimeSpan getTimeSpan(Tag tag) {
            for (TimeSpan t : mCache) {
                if (t.tag == tag) {
                    return t;
                }
            }
            return null;
        }

    }

    /**
     * 构造方法
     * @param context   上下文，用于获取资源
     * @param callback  回调
     */
    HistoryDecoration(Context context, DecorationCallback callback) {
        NOW_TIME = System.currentTimeMillis();
        TIME_ZONE = Calendar.getInstance().getTimeZone();
        mCallback = callback;
        Resources res = context.getResources();
        // Section标题左边距
        mTitleMarginLeft = res.getDimensionPixelSize(R.dimen.historySectionTitleMarginLeft);

        /// 设置背景画笔
        mBgPaint = new Paint();
        // 设置背景画笔颜色
        mBgPaint.setColor(res.getColor(R.color.whiteLow));

        /// 设置字体画笔
        mTextPaint = new TextPaint();
        // 设置字体
        mTextPaint.setTypeface(Typeface.DEFAULT);
        // 开启抗锯齿
        mTextPaint.setAntiAlias(true);
        // 设置字体颜色
        mTextPaint.setColor(res.getColor(android.R.color.primary_text_light));
        // 设置字体大小
        int titleSize = res.getDimensionPixelSize(R.dimen.historySectionTitleSize);
        mTextPaint.setTextSize(titleSize);
        // 设置字体对齐方式
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        /// 计算背景高度
        // 获取字体度量
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // Section标题上边距
        int titleMarginTop = res.getDimensionPixelSize(R.dimen.historySectionTitleMarginBottom);
        // Section标题下边距
        int titleMarginBottom = res.getDimensionPixelSize(R.dimen.historySectionTitleMarginBottom);
        // 自适应Section高度
        mSectionHeight = titleMarginBottom + fontMetrics.bottom - fontMetrics.top + titleMarginTop;
        // 计算标题基线与Section底部距离
        mTitleBaselineToBottom = fontMetrics.descent + titleMarginBottom;
    }

    /**
     * 获取条目偏移量
     * @param outRect   外矩形边框
     * @param view      当前视图
     * @param parent    父视图
     * @param state     父视图状态
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        // 获取当前条目位置
        int position = parent.getChildAdapterPosition(view);
        // 预留section标题位置，位于当前section第一个时
        if (isFirstInSection(position)) {
            // 设置外边距
            outRect.set(0, (int) mSectionHeight, 0, DECORATION_SIZE);
        } else {
            outRect.set(0, 0, 0, DECORATION_SIZE);
        }
    }

    /**
     * 判断当前条目是否位于当前Section中的第一个
     * @param position  条目位置
     * @return          返回当前条目是否位于当前Section中的第一个
     */
    private boolean isFirstInSection(int position) {
        if (position < 0) {
            return false;
        }

        /// 判断是否为“重要”Section
        if (mCallback.isImportantItem(position)) {
            return position == 0;
        }

        /// 不为“重要”的Section
        long itemTime = mCallback.getItemTime(position);
        if (position == 0 || mCallback.isImportantItem(position - 1)) {
            // 初始化缓存
            TimeSpan.getTimeSpan(itemTime, NOW_TIME, TIME_ZONE);
            return true;
        } else {
            // 比较当前条目与前一条目是否在同一Section内
            long prevItemTime = mCallback.getItemTime(position - 1);
            TimeSpan times = TimeSpan.getTimeSpan(itemTime, NOW_TIME, TIME_ZONE);
            TimeSpan prevTimes = TimeSpan.getTimeSpan(prevItemTime, NOW_TIME, TIME_ZONE);
            return times != null && prevTimes != null && !times.equals(prevTimes);
        }
    }

    /**
     * 当要绘制时
     * @param c         画板
     * @param parent    父视图
     * @param state     父视图状态
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        // 获取条目总数
        int childCount = parent.getChildCount();
        // 背景左侧位置
        float left = parent.getLeft();
        // 背景右侧位置
        float right = parent.getRight();
        // 遍历所有条目
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            // 获取所在时间及时间段
            long itemTime = mCallback.getItemTime(position);
            TimeSpan timeSpan = TimeSpan.getTimeSpan(itemTime, NOW_TIME, TIME_ZONE);
            if (timeSpan == null) {
                continue;
            }

            // 绘制section标题，位于当前section第一个时
            if (isFirstInSection(position)) {
                // 标题
                String title;
                // 判断是否为“重要”Section
                if (mCallback.isImportantItem(position)) {
                    title = "重要";
                } else {
                    title = isSectionTitle(timeSpan);
                }
                if (title == null) {
                    continue;
                }
                // 背景底部位置
                float bottom = view.getTop();
                // 背景顶部位置
                float top = bottom - mSectionHeight;
                // 标题基线位置
                float titleBaseline = bottom - mTitleBaselineToBottom;
                // 绘制背景
                c.drawRect(left, top, right, bottom, mBgPaint);
                // 绘制标题
                c.drawText(title, mTitleMarginLeft, titleBaseline, mTextPaint);
            }
        }
    }

    /**
     * 获取Section标题
     * @param timeSpan  当前Section的时间段
     * @return          返回Section标题
     */
    private String isSectionTitle(@NonNull TimeSpan timeSpan) {
        /// 处理“今天”、“昨天”和“明天”
        switch (timeSpan.tag) {
            case TODAY:
                return "今天";
            case YESTERDAY:
                return "昨天";
            case THE_DAY_BEFORE_YESTERDAY:
                return "前天";
        }

        // 初始化当前时间日历
        Calendar nowCalendar = Calendar.getInstance(TIME_ZONE);
        nowCalendar.setTimeInMillis(NOW_TIME);
        // 初始化目标（开始）时间日历
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTimeInMillis(timeSpan.startTime);

        /// 初始化日期格式
        // 不显示年份
        SimpleDateFormat dateFormat;
        // 显示年份
        SimpleDateFormat dateFormatLong;
        if (timeSpan.tag == TimeSpan.Tag.A_MONTH) {
            // 月份的日期格式只精确到每个月
            dateFormat = new SimpleDateFormat(TimeSpan.DATE_FORMAT, Locale.getDefault());
            dateFormatLong = new SimpleDateFormat(TimeSpan.DATE_FORMAT_LONG, Locale.getDefault());
        } else {
            // 其余的日期格式精确到每一天
            dateFormat = new SimpleDateFormat(Time.THE_YEAR, Locale.getDefault());
            dateFormatLong = new SimpleDateFormat(Time.OTHER, Locale.getDefault());
        }

        // 开始时间文本
        String startTimeStr;
        // 按时间距现在是否跨年来确定日期格式长度
        if (calendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)) {
            startTimeStr = dateFormat.format(calendar.getTime());
        } else {
            startTimeStr = dateFormatLong.format(calendar.getTime());
        }

        /// 处理“每个月”
        if (timeSpan.tag == TimeSpan.Tag.A_MONTH) {
            return startTimeStr;
        }

        /// 处理“一周（七天）内”和“这个月剩下的时间”
        // 结束时间文本
        String endTimeStr;
        // 初始化目标结束时间日历
        switch (timeSpan.tag) {
            case A_WEEK:
                // 计算目标“前天”的时间，并应用到日历
                calendar.setTimeInMillis(timeSpan.startTime + Time.day * 4);
                break;
            case LESS_THAN_A_MONTH:
                // 获取目标后一个时间段（即“一周内”），并应用到日历
                TimeSpan tempTimeSpan = TimeSpan.getTimeSpan(TimeSpan.Tag.A_WEEK);
                if (tempTimeSpan == null) {
                    return null;
                }
                calendar.setTimeInMillis(tempTimeSpan.startTime);
                break;
            default:
                return null;
        }
        // 按目标结束时间距现在是否跨年来确定日期格式长度
        if (calendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)) {
            endTimeStr = dateFormat.format(calendar.getTime());
        } else {
            endTimeStr = dateFormatLong.format(calendar.getTime());
        }
        // 按不同时间段的样式返回“一周（七天）内”或“这个月剩下的时间”文本
        switch (timeSpan.tag) {
            case A_WEEK:
                return startTimeStr + "至" + endTimeStr + "（一周内）";
            case LESS_THAN_A_MONTH:
                return startTimeStr + "至" + endTimeStr;
            default:
                return null;
        }
    }

}
