package ru.yandex.yamblz.utils;

import android.support.v7.widget.RecyclerView;

import ru.yandex.yamblz.handler.CriticalSectionsManager;

public class GenresScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            CriticalSectionsManager.getHandler().startSection(0);
        } else if (newState == RecyclerView.SCROLL_STATE_IDLE){
            CriticalSectionsManager.getHandler().stopSection(0);
        }
    }

}
