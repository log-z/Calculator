package com.log.jsq.library;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by log on 2017/8/3.
 */

public class Recorder {

    // 栈大小
    private static final int MAXSIZE = 50;
    // 当前记录
    private static Record nowRecord;
    // 可撤回记录栈
    private static ArrayList<Record> undoStack = new ArrayList<>();
    // 可重做记录栈
    private static ArrayList<Record> redoStack = new ArrayList<>();

    /**
     * （一条）记录
     */
    public static class Record {
        public String equation;
        public String result;

        public Record(String equation, String result) {
            this.equation = equation.replaceAll("\\s", "");
            this.result = result.replaceAll("\\s", "");
        }
    }

    /**
     * 处理新记录
     * @param record     新记录
     */
    public static void update(Record record) {
        Record redoStackTop = null;
        if (redoStack.size() > 0) {
            redoStackTop = redoStack.get(0);
        }
        if (redoStackTop != null
                && Objects.equals(record.equation, redoStackTop.equation)
                && Objects.equals(record.result, redoStackTop.result)) {
            // 新记录与redo栈顶一致
            // 相当于重做
            redo();
        } else if (nowRecord == null
                || !Objects.equals(record.equation, nowRecord.equation)
                || !Objects.equals(record.result, nowRecord.result)) {
            // 新记录与当前记录不一致时
            // 允许录入
            stacking(undoStack, nowRecord);
            nowRecord = record;
            // 清空redo栈
            redoStack.clear();
        }
    }

    /**
     * 执行撤回操作
     * @return  返回执行撤回后的当前记录，无法撤回则返回null
     */
    public static Record undo() {
        if (undoStack.size() == 0) {
            return null;
        } else {
            // 取出undo栈顶
            Record undoTopRecord = unstack(undoStack);
            // 把nowRecord压入redo栈
            stacking(redoStack, nowRecord);
            // 更新nowRecord
            nowRecord = undoTopRecord;
            return nowRecord;
        }
    }

    /**
     * 执行重做操作
     * @return  返回执行重做后的当前记录，无法重做则返回null
     */
    public static Record redo() {
        if (redoStack.size() == 0) {
            return null;
        } else {
            // 把nowRecord压入undo栈
            stacking(undoStack, nowRecord);
            // 取出redo栈顶到nowRecord（更新nowRecord）
            nowRecord = unstack(redoStack);
            return nowRecord;
        }
    }

    /**
     * 压栈（添加记录到指定栈）
     * @param stack     指定栈
     * @param record    记录
     */
    private static void stacking(ArrayList<Record> stack, Record record) {
        stack.add(0, record);
        while (stack.size() > MAXSIZE) {
            stack.remove(MAXSIZE);
        }
    }

    /**
     * 出栈（提取并移除栈顶记录）
     * @param stack     指定栈
     * @return          已被移除的记录
     */
    private static Record unstack(ArrayList<Record> stack) {
        return stack.remove(0);
    }

}
