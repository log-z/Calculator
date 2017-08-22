package com.log.jsq.historyUI;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.log.jsq.R;
import com.log.jsq.tool.HistoryListData;
import com.log.jsq.tool.TextHandler;


/**
 * Created by log on 2017/8/17.
 */

class HistoryDialog {

    /** 单例 */
    private static HistoryDialog INSTANCE = new HistoryDialog();
    /** 回调对象 */
    private Callback mCallback;
    /** 预览弹窗 */
    private AlertDialog mDialog;
    /** 编辑弹窗 */
    private AlertDialog mEditDialog;
    /** 弹窗标题 */
    private TextView mTitle;
    /** 弹窗内容 */
    private TextView mBody;
    /** 弹窗标签部分 */
    private View mTag;
    /** 弹窗标签 */
    private TextView mTagText;
    /** 弹窗标签编辑 */
    private EditText mTagEdit;
    /** 文本着重色 */
    private int mColorAccent;
    /** 当前数据组 */
    private HistoryListData.RowData mData;
    /** 布局构造器 */
    private LayoutInflater mInflater;

    /**
     * 回调接口
     */
    public interface Callback {

        /**
         * 当要载入到计算器时
         * @param result    要载入的结果
         * @param equation  要载入的算式
         */
        void onLoadToCalculator(String result, String equation);

        /**
         * 当标签被修改时
         * @param data 被修改的数据组
         */
        void onTagChange(HistoryListData.RowData data);

    }

    /**
     * 不可从外部构造该类对象
     */
    private HistoryDialog() {}

    /**
     * 获取单例
     * @return  此类的单例
     */
    static HistoryDialog getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化历史记录弹窗
     * @param context   用于初始化弹窗
     */
    void init(@NonNull final Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        mColorAccent = value.data;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = mInflater.inflate(R.layout.history_dialog, null);
        mTitle = dialogView.findViewById(R.id.historyDialogTitle);
        mBody = dialogView.findViewById(R.id.historyDialogBody);
        mTag = dialogView.findViewById(R.id.historyDialogTag);
        mTagText = dialogView.findViewById(R.id.historyDialogTagText);


        // 详情弹窗
        mDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                // 载入到计算器
                .setNeutralButton(R.string.loadToCalculator, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onLoadToCalculator(
                                mTitle.getText().toString(),
                                mBody.getText().toString()
                        );
                    }
                })
                // 取消
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                // 编辑标签
                .setPositiveButton(R.string.editTag, null)
                .create();
        // 编辑弹窗
        mEditDialog = null;
    }

    /**
     * 显示历史记录弹窗
     * @param context   用于初始化弹窗
     * @param data      需要显示的数据
     */
    void show(@NonNull final Context context, @NonNull final HistoryListData.RowData data)  {
        mData = data;
        String result = mData.getResult();
        String tag = mData.getTag();
        CharSequence equation = TextHandler.setStyle(mData.getEquation(), mColorAccent);

        mTitle.setText(result);
        mBody.setText(equation);
        setTag(tag);
        mDialog.show();

        // 绑定编辑标签按钮事件监听器
        // 避免之前的弹窗被关闭
        Button neutralButton = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(context);
            }
        });
    }

    /**
     * 设置回调
     * @param callback  回调对象
     */
    void setCallback(Callback callback) {
        mCallback = callback;
    }

    /**
     * 设置标签（仅弹窗内）
     * @param tag   标签
     */
    private void setTag(String tag) {
        if (tag != null && tag.length() > 0) {
            mTagText.setText(tag);
            mTag.setVisibility(View.VISIBLE);
        } else {
            mTagText.setText(null);
            mTag.setVisibility(View.GONE);
        }
    }

    /**
     * 显示标签编辑弹窗
     * @param context   用于构建弹窗
     */
    private void showEditDialog(@NonNull Context context) {
        // 初始化标签编辑弹窗
        if (mEditDialog == null || mTagEdit == null) {
            View editDialogView = mInflater.inflate(R.layout.history_tag_editext, null);
            mTagEdit = editDialogView.findViewById(R.id.editText);

            // 构建标签编辑弹窗
            mEditDialog = new AlertDialog.Builder(context)
                    .setView(editDialogView)
                    .setTitle(R.string.editTag)
                    // 确认
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String newTag = mTagEdit.getText().toString();
                            setTag(newTag);
                            mCallback.onTagChange(mData.setTag(newTag));
                        }
                    })
                    // 取消
                    .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    // 删除
                    .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setTag(null);
                            mCallback.onTagChange(mData.setTag(null));
                        }
                    })
                    .create();
        }

        // 显示标签编辑弹窗
        mTagEdit.setText(mTagText.getText());
        mEditDialog.show();
    }
}
