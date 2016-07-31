package ru.yandex.yamblz.genre;

import android.support.v7.widget.RecyclerView;

import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;

/**
 * Created by platon on 30.07.2016.
 */
public class RecyclerScrollListener extends RecyclerView.OnScrollListener
{
    public static final int CS_ID = 1991;
    private final CriticalSectionsHandler criticalSectionsHandler;

    public RecyclerScrollListener()
    {
        super();
        criticalSectionsHandler = CriticalSectionsManager.getHandler();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
    {
        switch (newState)
        {
            case RecyclerView.SCROLL_STATE_IDLE:
                criticalSectionsHandler.stopSection(CS_ID);
                break;

            case RecyclerView.SCROLL_STATE_DRAGGING:
                criticalSectionsHandler.startSection(CS_ID);
                break;
        }

    }
}
