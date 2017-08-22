package com.log.jsq.historyUI;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.CheckBox;

import com.log.jsq.R;

/**
 * Created by log on 2017/1/24.
 */

public class HistoryCallback extends ItemTouchHelper.Callback {
    /** 条目操作构造器 */
    private ItemTouchHelperAdapter mAdapter;

    /**
     * 条目操作构造器
     */
    protected interface ItemTouchHelperAdapter {
        /**
         * 当条目将被划掉后
         * @param position  条目位置
         */
        void onItemDismiss(int position);
    }

    /**
     * 构造一个操作回调
     * @param adapter   需要一个操作构造器
     */
    public HistoryCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * 配置是否允许条目移动和滑动
     * @param recyclerView  条目视图
     * @param viewHolder    条目视图持有者
     * @return              关于条目移动和滑动的配置
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View rootView = ((HistoryListAdapter.ViewHolder) viewHolder).itemView;
        CheckBox checkBox = rootView.findViewById(R.id.importance);
        if (checkBox.isClickable() && checkBox.isChecked()) {
            // 重要条目不可移动和滑动
            return 0;
        } else {
            // 非重要条目可以两端滑动，不可以移动
            return makeMovementFlags(
                    0,
                    ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT
            );
        }
    }

    /**
     * 当条目被移动时
     * @param recyclerView      条目视图
     * @param viewHolder        当前条目视图持有者
     * @param target            目标条目视图持有者
     * @return                  是否允许移动条目
     */
    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * 当条目被滑掉时
     * @param viewHolder    条目视图持有者
     * @param direction     条目位置
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * 是否允许视图滑动
     * @return  返回是否允许视图滑动
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * 当条目被移动或滑动时
     * @param c                     RecyclerView画布
     * @param recyclerView          条目视图
     * @param viewHolder            条目视图持有者
     * @param dX                    水平位移量
     * @param dY                    垂直位移量
     * @param actionState           运动状态（移动ACTION_STATE_DRAG，或滑动ACTION_STATE_SWIPE）
     * @param isCurrentlyActive     （未知）
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // 取水平位移绝对值
        float x = Math.abs(dX) + 0.5f;
        // 获得条目视图宽度
        float width = viewHolder.itemView.getWidth();
        // 实时绘制条目视图透明度（根据水平位移量）
        viewHolder.itemView.setAlpha(1f - x / (width * 0.7f));
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * 当用户交互结束并动画完成后
     * @param recyclerView  条目视图
     * @param viewHolder    条目视图持有者
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 操作完毕后恢复条目视图透明度
        viewHolder.itemView.setAlpha(1.0f);
        super.clearView(recyclerView, viewHolder);
    }

    public void release() {
        mAdapter = null;
    }
}
