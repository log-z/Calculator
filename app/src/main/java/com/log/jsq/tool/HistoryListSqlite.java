package com.log.jsq.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by log on 2017/1/17.
 */

public class HistoryListSqlite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HistoryList.db";
    public static final String TABLE_NAME = "historyList";
    public static final String COLUMN_NAME_EQUATION = "equation";
    public static final String COLUMN_NAME_RESULT = "result";
    public static final String COLUMN_NAME_DATE_TIME = "dateTime";
    public static final String COLUMN_NAME_IMPORTANCE = "importance";
    public static final int IMPORTANCE_TRUE = 1;
    public static final int IMPORTANCE_FALSE = 0;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_NAME_EQUATION + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_RESULT + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_DATE_TIME + INTEGER_TYPE + COMMA_SEP +
            COLUMN_NAME_IMPORTANCE + INTEGER_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public HistoryListSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
