package ru.yandex.yamblz.ui.adapters;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Volha on 04.08.2016.
 */
public class FrameItemDecoration extends RecyclerView.ItemDecoration {

    private final int offset = 15;


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = offset;
        outRect.right = offset;
        outRect.bottom = offset;
        outRect.top = offset;
    }

}