package com.log.jsq.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.log.jsq.library.RowData;

import java.util.ArrayList;

public class HistoryListData {

    public static final long ALL_TIME = -1;

    public enum RowId {
        ROW_ID_NEWEST_TIME,
        ROW_ID_OLDEST_TIME;
    }

    public static ArrayList<RowData> exportAllFromSQLite(Context context) {
        ArrayList<RowData> arrayList = new ArrayList<RowData>();
        HistoryListSqlite sqliet = new HistoryListSqlite(context);
        SQLiteDatabase db = sqliet.getReadableDatabase();
        String[] projection = {
                HistoryListSqlite.COLUMN_NAME_EQUATION,
                HistoryListSqlite.COLUMN_NAME_RESULT,
                HistoryListSqlite.COLUMN_NAME_DATE_TIME,
                HistoryListSqlite.COLUMN_NAME_IMPORTANCE
        };
        Cursor cursor = db.query(
                HistoryListSqlite.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        while (true) {
            try {
                String equation = cursor.getString(cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_EQUATION));
                String result = cursor.getString(cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_RESULT));
                long time = cursor.getLong(cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_DATE_TIME));
                int importanceNum = cursor.getInt(cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_IMPORTANCE));
                boolean importance = false;

                if (importanceNum == HistoryListSqlite.IMPORTANCE_TRUE) {
                    importance = true;
                }

                arrayList.add(new RowData(equation, result, time, importance));
                cursor.moveToNext();
            } catch (IllegalArgumentException e) {
                break;
            } catch (CursorIndexOutOfBoundsException ee) {
                break;
            }
        }

        cursor.close();
        db.close();
        sqliet.close();

        return arrayList;
    }

    public static RowData exportFromSQLite(Context context, RowId rowId) {
        HistoryListSqlite sqliet = new HistoryListSqlite(context);
        SQLiteDatabase db = sqliet.getReadableDatabase();
        String[] projection = {
                HistoryListSqlite.COLUMN_NAME_EQUATION,
                HistoryListSqlite.COLUMN_NAME_RESULT,
                HistoryListSqlite.COLUMN_NAME_DATE_TIME,
                HistoryListSqlite.COLUMN_NAME_IMPORTANCE
        };
        String sortOrder = HistoryListSqlite.COLUMN_NAME_DATE_TIME + " DESC";
        Cursor cursor = db.query(
                HistoryListSqlite.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor.getCount() <= 0) {
            return null;
        }

        switch (rowId) {
            case ROW_ID_NEWEST_TIME:
                cursor.moveToFirst();
                break;
            case ROW_ID_OLDEST_TIME:
                cursor.moveToLast();
                break;
        }

        String equation = cursor.getString(cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_EQUATION));
        String result = cursor.getString(cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_RESULT));
        long time = cursor.getLong(cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_DATE_TIME));
        RowData rowData = new RowData(equation, result, time, false);

        cursor.close();
        db.close();
        sqliet.close();

        return rowData;
    }

    public static void deleteRow(final String tableName, final long[] time, Context context) {
        if (time != null && time.length > 0) {

            HistoryListSqlite sqliet = new HistoryListSqlite(context);
            SQLiteDatabase db = sqliet.getReadableDatabase();
            if (time[0] == ALL_TIME) {
                db.delete(tableName, null, null);
            } else {
                String whereClause = HistoryListSqlite.COLUMN_NAME_DATE_TIME + " == ";

                for (long t : time) {
                    db.delete(tableName, whereClause + t, null);
                }
            }

            db.close();
            sqliet.close();
        }
    }

    public static void insertToSQLite(RowData rowData, Context context) {
        HistoryListSqlite historyListSqlite = new HistoryListSqlite(context);
        SQLiteDatabase db = historyListSqlite.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(HistoryListSqlite.COLUMN_NAME_EQUATION, rowData.getEquation());
            values.put(HistoryListSqlite.COLUMN_NAME_RESULT, rowData.getResult());
            values.put(HistoryListSqlite.COLUMN_NAME_DATE_TIME, rowData.getTime());

            if (rowData.getImportance()) {
                values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE, HistoryListSqlite.IMPORTANCE_TRUE);
            } else {
                values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE, HistoryListSqlite.IMPORTANCE_FALSE);
            }

            db.insert(HistoryListSqlite.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
            db.close();
            historyListSqlite.close();
        }
    }

    public static void updateFromSQLite(final String tableName, final RowData[] rowDates, Context context) {
        if (rowDates != null && rowDates.length > 0) {
            HistoryListSqlite historyListSqlite = new HistoryListSqlite(context);
            SQLiteDatabase db = historyListSqlite.getWritableDatabase();
            ContentValues values = new ContentValues();
            String whereClause = HistoryListSqlite.COLUMN_NAME_DATE_TIME + " == ";

            for (RowData rowDate : rowDates) {
                if (rowDate.getImportance()) {
                    values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE, HistoryListSqlite.IMPORTANCE_TRUE);
                } else {
                    values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE, HistoryListSqlite.IMPORTANCE_FALSE);
                }

                db.update(tableName, values, whereClause + rowDate.getTime(), null);
            }

            values.clear();
            db.close();
            historyListSqlite.close();
        }
    }
}
