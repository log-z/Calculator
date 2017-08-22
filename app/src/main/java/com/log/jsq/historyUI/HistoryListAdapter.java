package com.log.jsq.historyUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.log.jsq.library.FuHao;
import com.log.jsq.R;
import com.log.jsq.tool.HistoryListData;
import com.log.jsq.tool.TextHandler;
import com.log.jsq.tool.Time;

import java.util.ArrayList;

public class HistoryListAdapter extends Adapter<HistoryListAdapter.ViewHolder> {
    /** 数据集 */
    private ArrayList<HistoryListData.RowData> mDataset;
    /** 当前Activity */
    private Activity mActivity;
    /** 条目点击监听器 */
    private OnItemClickListener mClickListener;
    /** 条目长按监听器 */
    private OnItemLongClickListener mLongClickListener;
    /** 条目重要复选框点击监听器 */
    private OnCheckBoxClickListener mCheckBoxClickListener;

    /**
     * 条目点击监听器
     */
    public interface OnItemClickListener {
        /**
         * 当条目被点击时
         * @param view      条目视图
         * @param position  条目位置
         */
        public void onItemClick(View view,int position);
    }

    /**
     * 条目长按监听器
     */
    public interface OnItemLongClickListener {
        /**
         * 当条目被长按时
         * @param view      条目视图
         * @param position  条目位置
         */
        public boolean onItemLongClick(View view, int position);
    }

    /**
     * 条目重要复选框点击监听器
     */
    public interface OnCheckBoxClickListener {
        /**
         * 当重要复选框被点击时
         * @param checkBox  条目视图
         * @param position  条目位置
         */
        public void onCheckBoxClick(CheckBox checkBox,int position);
    }

    /**
     * 条目视图持有者类
     */
    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private OnItemClickListener mClickListener;
        private OnItemLongClickListener mLongClickListener;

        /**
         * 构造视图持有者
         * @param view                  条目的视图
         * @param clickListener         点击监听器
         * @param longClickListener     长按监听器
         */
        public ViewHolder(View view, OnItemClickListener clickListener,
                          OnItemLongClickListener longClickListener) {
            super(view);
            mClickListener = clickListener;
            mLongClickListener = longClickListener;
            View historyItem =  view.findViewById(R.id.historyItem);
            historyItem.setOnClickListener(this);
            historyItem.setOnLongClickListener(this);
        }

        /**
         * 条目点击事件
         * @param v     被点击的视图
         */
        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        /**
         * 条目长按事件
         * @param v     被长按的视图
         * @return      是否消耗掉触控事件
         */
        @Override
        public boolean onLongClick(View v) {
            return mLongClickListener == null
                    || mLongClickListener.onItemLongClick(v, getAdapterPosition());
        }

    }

    /**
     * 构造函数
     * @param dataset     指定数据集
     * @param activity      指定activity
     */
    public HistoryListAdapter(ArrayList<HistoryListData.RowData> dataset, Activity activity) {
        mDataset = dataset;
        mActivity = activity;
    }

    /**
     * 创建新视图持有者
     * @param parent        父视图组
     * @param viewType      视图类型
     * @return              返回一个视图持有者
     */
    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个新视图
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_card, parent, false);
        return new ViewHolder(view, mClickListener, mLongClickListener);
    }

    /**
     * 绑定视图持有者
     * 替换视图内容
     * @param holder        当前视图持有者
     * @param position      当前条目位置
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        final View view = holder.itemView;
        final TextView itemTitle = view.findViewById(R.id.itemTitleText);
        final TextView itemBody = view.findViewById(R.id.itemBodyText);
        final TextView itemTime = view.findViewById(R.id.itemTimeText);
        final TextView itemTag = view.findViewById(R.id.itemTagText);
        final CheckBox importance = view.findViewById(R.id.importance);
        final View itemDot = view.findViewById(R.id.itemDot);

        itemTitle.setText(FuHao.NULL);
        itemBody.setText(FuHao.NULL);
        itemTime.setText(FuHao.NULL);
        importance.setChecked(false);

        // 条目异步显示
        new Thread() {
            @Override
            public void run() {
                final HistoryListData.RowData rowData = mDataset.get(position);
                final String itemTitleStr = rowData.getResult();
                final String itemBodyStr = rowData.getEquation();
                final String itemTagStr = rowData.getTag();
                TypedValue value = new TypedValue();
                mActivity.getTheme()
                        .resolveAttribute(R.attr.colorAccent, value, true);
                final Spanned itemBodySpa = TextHandler.setStyle(itemBodyStr, value.data);

                // 绑定重要复选框点击监听器
                importance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCheckBoxClickListener.onCheckBoxClick(importance, position);
                    }
                });

                // 修改视图内容
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTitle.setText(itemTitleStr);
                        itemBody.setText(itemBodySpa);
                        itemTime.setText(Time.toString(rowData.getTime()));
                        importance.setChecked(rowData.getImportance());
                        // 设置标签内容和是否可见
                        if (itemTagStr != null && itemTagStr.length() > 0) {
                            itemTag.setText(itemTagStr);
                            itemTag.setVisibility(View.VISIBLE);
                            itemDot.setVisibility(View.VISIBLE);
                        } else {
                            itemDot.setVisibility(View.GONE);
                            itemTag.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }.start();
    }

    /**
     * 返回数据集的大小
     * @return  返回数据集的大小
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * 设置条目点击事件监听器
     * @param listener  点击事件监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        mClickListener = listener;
    }

    /**
     * 设置条目长按事件监听器
     * @param listener  长按事件监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mLongClickListener = listener;
    }

    /**
     * 设置重要复选框点击事件监听器
     * @param listener  点击事件监听器
     */
    public void setOnCheckBoxClickListener(OnCheckBoxClickListener listener) {
        mCheckBoxClickListener = listener;
    }

    /**
     * 删除条目（仅在Adapter中改变）
     * @param position  要删除条目的位置
     */
    public void deleteItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size() - position);
    }

    /**
     * 增加条目（仅在Adapter中改变）
     * @param position  添加新条目到再此位置
     */
    public void addItem(int position) {
        notifyItemRangeChanged(position, mDataset.size() - position);
        notifyItemInserted(position);
    }

    /**
     * 移动条目（仅在Adapter中改变）
     * @param newPosition   条目新位置
     * @param oldPosition   条目旧位置
     */
    public void moveItem(int newPosition, int oldPosition) {
        notifyItemRangeChanged(0, mDataset.size());
        notifyItemMoved(oldPosition, newPosition);
    }

    /**
     * 设置数据集
     * @param dataset   数据集
     */
    public void setDataset(ArrayList<HistoryListData.RowData> dataset) {
        mDataset.clear();
        mDataset = dataset;
    }

    /**
     * 释放构造器资源
     */
    public void release() {
        mActivity = null;
        mClickListener = null;
        mLongClickListener = null;
        mCheckBoxClickListener = null;
    }

}
