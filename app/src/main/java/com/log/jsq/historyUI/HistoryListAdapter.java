package com.log.jsq.historyUI;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.log.jsq.library.FuHao;
import com.log.jsq.R;
import com.log.jsq.tool.HistoryListData;
import com.log.jsq.tool.TextHandler;
import com.log.jsq.tool.Time;

import java.util.ArrayList;

public class HistoryListAdapter extends Adapter<HistoryListAdapter.ViewHolder> {
    private ArrayList<HistoryListData.RowData> mDataset;
    private Activity activity;
    private MyItemClickListener mItemClickListener;
    private MyCheckBoxClickListener mCheckBoxClickListener;

    // 为每个数据项提供引用视图
    // 复杂数据项每项可能需要多个视图,和你提供的所有视图视图中的数据项的保持者
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        private MyItemClickListener mListener;

        public ViewHolder(View rootView, MyItemClickListener listener) {
            super(rootView);
            this.rootView = rootView;
            this.mListener = listener;
            FrameLayout historyItem = (FrameLayout) rootView.findViewById(R.id.historyItem);
            historyItem.setOnClickListener(this);
            historyItem.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mListener == null || mListener.onItemLongClick(v, getAdapterPosition());
        }
    }

    public interface MyItemClickListener {
        public void onItemClick(View view,int position);
        public boolean onItemLongClick(View view, int position);
    }

    public interface MyCheckBoxClickListener {
        public void onCheckBoxClick(View view,int position);
    }

    // 构造函数(取决于数据集的类型)
    public HistoryListAdapter(ArrayList myDataset, Activity activity) {
        this.mDataset = myDataset;
        this.activity = activity;
    }

    // 创建新的视图
    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个新视图
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);

        return new ViewHolder(view, mItemClickListener);
    }

    // 设置视图的内容
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - 从数据集得到元素所在位置
        // - 用该元素替换视图中的内容
        View rootView = holder.rootView;
        final TextView itemTitle = (TextView) rootView.findViewById(R.id.itemTitleText);
        final TextView itemBody = (TextView) rootView.findViewById(R.id.itemBodyText);
        final TextView itemTime = (TextView) rootView.findViewById(R.id.itemTimeText);
        final CheckBox importance = (CheckBox) rootView.findViewById(R.id.importance);

        itemTitle.setText(FuHao.NULL);
        itemBody.setText(FuHao.NULL);
        itemTime.setText(FuHao.NULL);
        importance.setChecked(false);

        new Thread() {
            @Override
            public void run() {
                final HistoryListData.RowData rowData = mDataset.get(position);

                final String itemTitleStr = rowData.getResult();
                final String itemBodyStr = rowData.getEquation();
                TypedValue value = new TypedValue();
                activity.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
                final Spanned itemBodySpa = TextHandler.run(itemBodyStr, value.data);

                importance.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        mCheckBoxClickListener.onCheckBoxClick(v, position);
                    }
                });

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTitle.setText(itemTitleStr);
                        itemBody.setText(itemBodySpa);
                        itemTime.setText(Time.toString(rowData.getTime()));
                        importance.setChecked(rowData.getImportance());
                    }
                });
            }
        }.start();
    }

    // 返回数据集的大小
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // 监听回调
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setOnCheckBoxClickListener(MyCheckBoxClickListener listener) {
        mCheckBoxClickListener = listener;
    }

    // 删除条目（仅在Adapter中改变）
    public void deleteItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size() - position);
    }

    // 增加条目（仅在Adapter中改变）
    public void recoverItem(int position) {
        notifyItemRangeChanged(position, mDataset.size() - position);
        notifyItemInserted(position);
    }

    public void updateItem(int newPosition, int oldPosition) {
        notifyItemRangeChanged(0, mDataset.size());
        notifyItemMoved(oldPosition, newPosition);
    }

    public void setmDataset(ArrayList<HistoryListData.RowData> arrayList) {
        mDataset.clear();
        mDataset = arrayList;
    }

    public void release() {
        activity = null;
        mItemClickListener = null;
        mCheckBoxClickListener = null;
    }

}
