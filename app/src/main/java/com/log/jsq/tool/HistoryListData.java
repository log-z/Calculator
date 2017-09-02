package com.log.jsq.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;

public class HistoryListData {

    /**
     * 排序方式
     */
    public enum OrderBy {
        TIME_DESC,
        TIME_ASC;
    }

    /**
     * 历史记录数据组
     */
    public static class RowData {
        // 算式字符串
        private final String equation;
        // 结果字符串
        private final String result;
        // 日期时间
        private final long time;
        // 是否重要
        private boolean importance;
        // 标签字符串
        private String tag;

        public RowData(String equation, String result, long time, boolean importance, String tag) {
            this.equation = equation;
            this.result = result;
            this.time = time;
            this.importance = importance;
            this.tag = tag;
        }

        public RowData(String equation, String result, long time, boolean importance) {
            this(equation, result, time, importance, "");
        }

        public RowData(String equation, String result, long time) {
            this(equation, result, time, false);
        }

        public String getEquation() {
            return equation;
        }

        public String getResult() {
            return result;
        }

        public long getTime() {
            return time;
        }

        public boolean getImportance() {
            return importance;
        }

        public String getTag() {
            return tag;
        }

        public RowData setImportance(boolean importance) {
            this.importance = importance;
            return this;
        }

        public RowData setTag(String tag) {
            this.tag = tag;
            return this;
        }

    }

    /**
     * 按时间降序排序历史记录数据组
     */
    public static class SortByTimeDesc implements Comparator<RowData> {

        /**
         * 比较历史记录数据组
         * @param data  第一个数据组
         * @param t1    第二个数据组
         * @return      返回值大于0则t1优先，小于0则data优先
         */
        @Override
        public int compare(RowData data, RowData t1) {
            if (data.importance) {
                if (t1.importance) {
                    return Long.compare(t1.time, data.time);
                } else {
                    return -1;
                }
            } else {
                if (t1.importance) {
                    return 1;
                } else {
                    return Long.compare(t1.time, data.time);
                }
            }
        }

    }

    /**
     * 从数据库取出整个数据表
     * @param context   用于操作数据库
     * @return          数据表
     */
    public static ArrayList<RowData> exportAllFromSQLite(@NonNull Context context) {
        ArrayList<RowData> arrayList = new ArrayList<>();
        HistoryListSqlite sqliet = new HistoryListSqlite(context);
        SQLiteDatabase db = sqliet.getReadableDatabase();
        // 获取光标
        Cursor cursor = getCursor(db, OrderBy.TIME_DESC);

        // 移动光标到首行
        cursor.moveToFirst();
        // 取出数据
        while (true) {
            try {
                // 获取光标处数据
                arrayList.add(getCursorData(cursor));
                // 移动光标到下一行
                cursor.moveToNext();
            } catch (IllegalArgumentException e) {
                break;
            } catch (CursorIndexOutOfBoundsException ee) {
                // 数据表遍历完成
                break;
            }
        }

        cursor.close();
        db.close();
        sqliet.close();

        return arrayList;
    }

    /**
     * 从数据表取出第一行数据
     * @param context   用于操作数据库
     * @param orderBy   排序方法
     * @return          第一行数据
     */
    public static RowData exportFirstLineFromSQLite(@NonNull Context context,
                                                    @NonNull OrderBy orderBy) {
        HistoryListSqlite sqliet = new HistoryListSqlite(context);
        SQLiteDatabase db = sqliet.getReadableDatabase();
        Cursor cursor = getCursor(db, orderBy);

        if (cursor.getCount() <= 0) {
            return null;
        }

        // 获取首行数据
        cursor.moveToFirst();
        RowData rowData = getCursorData(cursor);

        cursor.close();
        db.close();
        sqliet.close();

        return rowData;
    }

    /**
     * 获取光标
     * @param db        历史记录数据库
     * @param orderBy   排序方法
     * @return          光标
     */
    private static Cursor getCursor(@NonNull SQLiteDatabase db, @NonNull OrderBy orderBy) {
        String[] projection = {
                HistoryListSqlite.COLUMN_NAME_EQUATION,
                HistoryListSqlite.COLUMN_NAME_RESULT,
                HistoryListSqlite.COLUMN_NAME_DATE_TIME,
                HistoryListSqlite.COLUMN_NAME_IMPORTANCE,
                HistoryListSqlite.COLUMN_NAME_TAG
        };
        String sortOrder = null;
        switch (orderBy) {
            case TIME_ASC:
                sortOrder = HistoryListSqlite.COLUMN_NAME_DATE_TIME
                        + HistoryListSqlite.ASC_ORDER;
                break;
            case TIME_DESC:
                sortOrder = HistoryListSqlite.COLUMN_NAME_DATE_TIME
                        + HistoryListSqlite.DESC_ORDER;
                break;
        }

        return db.query(
                HistoryListSqlite.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    /**
     * 获取光标处的数据
     * @param cursor    指定光标
     * @return          光标处的数据
     */
    private static RowData getCursorData(Cursor cursor) {
        String equation = cursor.getString(
                cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_EQUATION));
        String result = cursor.getString(
                cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_RESULT));
        long time = cursor.getLong(
                cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_DATE_TIME));
        String tag = cursor.getString(
                cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_TAG));

        boolean importance = false;
        int importanceNum = cursor.getInt(
                cursor.getColumnIndexOrThrow(HistoryListSqlite.COLUMN_NAME_IMPORTANCE));
        if (importanceNum == HistoryListSqlite.IMPORTANCE_TRUE) {
            importance = true;
        }

        return new HistoryListData.RowData(equation, result, time, importance, tag);
    }

    /**
     * 删除数据行（根据具体时间）
     * @param times     指定时间的数据会被删除
     * @param context   用于操作数据库
     */
    public static void deleteRow(@NonNull final long[] times, @NonNull Context context) {
        if (times.length > 0) {
            String tableName = HistoryListSqlite.TABLE_NAME;
            // 删除的条件
            String whereClause = HistoryListSqlite.COLUMN_NAME_DATE_TIME + " = ";
            HistoryListSqlite sqliet = new HistoryListSqlite(context);
            SQLiteDatabase db = sqliet.getReadableDatabase();

            // 开始删除
            for (long t : times) {
                db.delete(tableName, whereClause + t, null);
            }

            db.close();
            sqliet.close();
        }
    }

    /**
     * 删除数据行（根据时间范围）
     * @param minTime           此时间之前的数据行会被删除
     * @param delImportance     是否删除重要的数据
     * @param context           用于操作数据库
     */
    public static void deleteRow(final long minTime, final boolean delImportance,
                                 @NonNull Context context) {
        // 删除的条件
        String whereClause = HistoryListSqlite.COLUMN_NAME_DATE_TIME + " < " + minTime;
        HistoryListSqlite sqlite = new HistoryListSqlite(context);
        SQLiteDatabase db = sqlite.getReadableDatabase();

        if (!delImportance) {
            // 不删除重要数据
            whereClause += " AND " + HistoryListSqlite.COLUMN_NAME_IMPORTANCE
                    + " = " + HistoryListSqlite.IMPORTANCE_FALSE;
        }

        // 删除数据
        db.delete(HistoryListSqlite.TABLE_NAME, whereClause, null);
    }

    /**
     * 添加数据到数据库
     * @param rowData   添加的数据行
     * @param context   用于操作数据库
     */
    public static void insertToSQLite(RowData rowData, Context context) {
        HistoryListSqlite historyListSqlite = new HistoryListSqlite(context);
        // 创建存储介质
        ContentValues values = new ContentValues();

        try (SQLiteDatabase db = historyListSqlite.getWritableDatabase()) {
            // 存入数据
            values.put(HistoryListSqlite.COLUMN_NAME_EQUATION, rowData.getEquation());
            values.put(HistoryListSqlite.COLUMN_NAME_RESULT, rowData.getResult());
            values.put(HistoryListSqlite.COLUMN_NAME_DATE_TIME, rowData.getTime());
            values.put(HistoryListSqlite.COLUMN_NAME_TAG, rowData.getTag());

            if (rowData.getImportance()) {
                values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE,
                        HistoryListSqlite.IMPORTANCE_TRUE);
            } else {
                values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE,
                        HistoryListSqlite.IMPORTANCE_FALSE);
            }

            // 添加到数据库
            db.insert(HistoryListSqlite.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            values.clear();
            historyListSqlite.close();
        }
    }

    /**
     * 更新数据库
     * @param rowDates  需要更新的数据组集合
     * @param context   用于操作数据库
     */
    public static void updateFromSQLite(ArrayList<RowData> rowDates, Context context) {
        if (rowDates != null && rowDates.size() > 0) {
            HistoryListSqlite historyListSqlite = new HistoryListSqlite(context);
            SQLiteDatabase db = historyListSqlite.getWritableDatabase();
            // 创建存储介质
            ContentValues values = new ContentValues();
            // 更新条件
            String whereClause = HistoryListSqlite.COLUMN_NAME_DATE_TIME + " = ";

            for (RowData rowDate : rowDates) {
                // 存入数据
                values.put(HistoryListSqlite.COLUMN_NAME_TAG, rowDate.getTag());
                if (rowDate.getImportance()) {
                    values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE,
                            HistoryListSqlite.IMPORTANCE_TRUE);
                } else {
                    values.put(HistoryListSqlite.COLUMN_NAME_IMPORTANCE,
                            HistoryListSqlite.IMPORTANCE_FALSE);
                }

                // 更新数据库
                db.update(
                        HistoryListSqlite.TABLE_NAME,
                        values,
                        whereClause + rowDate.getTime(),
                        null
                );
            }

            values.clear();
            db.close();
            historyListSqlite.close();
        }
    }

}
