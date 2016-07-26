package ru.yandex.yamblz.ui.fragments.Genre;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import ru.yandex.yamblz.handler.CriticalSectionsManager;

/**
 * Created by kostya on 26.07.16.
 */

public class GenreOnScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            Log.d("nScrollStateChanged", "DRAGGING");
            CriticalSectionsManager.getHandler().startSection(42);
        }
        else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            Log.d("nScrollStateChanged", "IDLE");
            CriticalSectionsManager.getHandler().stopSection(42);
        }
    }
}
