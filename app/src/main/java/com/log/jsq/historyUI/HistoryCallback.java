package com.log.jsq.historyUI;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.CheckBox;

import com.log.jsq.R;

/**
 * Created by log on 2017/1/24.
 */

public class HistoryCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter mAdapter;

    protected interface ItemTouchHelperAdapter {
        void onItemDismiss(int position);
    }

    public HistoryCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View rootView = ((HistoryListAdapter.ViewHolder) viewHolder).rootView;
        CheckBox checkBox = ((CheckBox) rootView.findViewById(R.id.importance));

        if (checkBox.isClickable() && checkBox.isChecked()) {
            return 0;
        } else {
            int swipedFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;

            return makeMovementFlags(0, swipedFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public void release() {
        mAdapter = null;
    }
}
