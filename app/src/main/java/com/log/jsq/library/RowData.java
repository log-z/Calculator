package com.log.jsq.library;

import android.support.annotation.NonNull;

/**
 * Created by log on 2017/1/19.
 */

public class RowData implements Comparable {
    private final String equation;
    private final String result;
    private final long time;
    private boolean importance;
    private int position = -1;

    public RowData(String equation, String result, long time, boolean importance) {
        this.equation = equation;
        this.result = result;
        this.time = time;
        this.importance = importance;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (this.importance) {
            if (((RowData) o).importance) {
                return (int) (((RowData) o).time - this.time);
            } else {
                return -1;
            }
        } else {
            if (((RowData) o).importance) {
                return 1;
            } else {
                return (int) (((RowData) o).time - this.time);
            }
        }
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

    public int getPosition() {
        return position;
    }

    public RowData setPosition(int position) {
        this.position = position;
        return this;
    }

    public RowData setImportance(boolean importance) {
        this.importance = importance;

        return this;
    }
}
