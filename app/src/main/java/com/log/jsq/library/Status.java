package com.log.jsq.library;

/**
 * Created by log on 2017/8/3.
 */

public class Status {

    // 正常状态
    public static final int NORMAL = 1;
    // 编辑状态
    public static final int EDIT = 2;
    // 计算状态
    public static final int CALCULATING = 3;
    private static final Status INSTANCE = new Status();
    private int mType = NORMAL;
    private Watcher mWatcher;

    public interface Watcher {
        /**
         * 改变状态类型之前
         * @param type      将要修改的状态类型
         * @param nowType   当前状态类型
         */
        public void beforeTypeChanged(int type, int nowType);

        /**
         * 改变状态类型之后
         * @param type  改变后的状态类型
         */
        public void afterTypeChanged(int type, int oldType);
    }

    private Status() {}

    /**
     * 获取单例
     * @return  返回该类单例
     */
    public static Status getInstance() {
        return INSTANCE;
    }

    /**
     * 设置状态类型
     * @param type  要更改的状态类型
     */
    public void setType(int type) {
        if (mWatcher == null) {
            mType = type;
        } else {
            mWatcher.beforeTypeChanged(type, mType);
            int oldType = mType;
            mType = type;
            mWatcher.afterTypeChanged(type, oldType);
        }
    }

    /**
     * 获取状态类型
     * @return  当前状态类型
     */
    public int getType() {
        return mType;
    }

    /**
     * 设置观察者
     * @param watcher   观测者
     */
    public void setWatcher(Watcher watcher) {
        mWatcher = watcher;
    }

}
