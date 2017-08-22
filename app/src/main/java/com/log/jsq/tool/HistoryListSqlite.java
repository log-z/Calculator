package com.log.jsq.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by log on 2017/1/17.
 */

public class HistoryListSqlite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "HistoryList.db";
    public static final String TABLE_NAME = "historyList";
    public static final String COLUMN_NAME_EQUATION = "equation";
    public static final String COLUMN_NAME_RESULT = "result";
    public static final String COLUMN_NAME_DATE_TIME = "dateTime";
    public static final String COLUMN_NAME_IMPORTANCE = "importance";
    public static final String COLUMN_NAME_TAG = "tag";
    public static final int IMPORTANCE_TRUE = 1;
    public static final int IMPORTANCE_FALSE = 0;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    public static final String DESC_ORDER = " DESC";
    public static final String ASC_ORDER = " ASC";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_NAME_EQUATION + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_RESULT + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_DATE_TIME + INTEGER_TYPE + COMMA_SEP +
            COLUMN_NAME_IMPORTANCE + INTEGER_TYPE + COMMA_SEP +
            COLUMN_NAME_TAG + TEXT_TYPE + " )";

    public HistoryListSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * 数据库升级
     * 数据库被打开时，如果版本号不一致则会自动回调此方法
     * @param db            需要升级的数据库
     * @param oldVersion    旧版本号
     * @param newVersion    新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_NAME_TAG + TEXT_TYPE);
        }
    }
}
