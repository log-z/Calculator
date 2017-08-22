package com.log.jsq.historyUI;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by log on 2017/8/19.
 */

public class HistoryDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        // 设置外边距
        outRect.set(0, 0, 0, 3);
    }

}
