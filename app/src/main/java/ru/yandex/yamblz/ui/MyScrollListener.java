package ru.yandex.yamblz.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import ru.yandex.yamblz.handler.CriticalSectionsManager;

/**
 * Created by Aleksandra on 26/07/16.
 */
public class MyScrollListener extends RecyclerView.OnScrollListener {
    public static final String DEBUG_TAG = MyScrollListener.class.getName();

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                CriticalSectionsManager.getHandler().startSection(0);
                Log.d(DEBUG_TAG, "In scroll state dragging");
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                Log.d(DEBUG_TAG, "In scroll state idle");
                CriticalSectionsManager.getHandler().stopSection(0);
                break;
            default:
                break;
        }
    }
}
