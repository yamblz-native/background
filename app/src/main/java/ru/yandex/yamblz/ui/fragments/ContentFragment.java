package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;

public class ContentFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        CriticalSectionsManager.getHandler().postLowPriorityTask(() -> {
            Log.d(this.getClass().getSimpleName(), "another low priority task");
        });
        CriticalSectionsManager.getHandler().startSection(0);
        Log.d(this.getClass().getSimpleName(), "high priority section started");
        Log.d(this.getClass().getSimpleName(), "press home button to stop it");
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        CriticalSectionsManager.getHandler().stopSection(0);
        Log.d(this.getClass().getSimpleName(), "high priority section stopped");
    }
}
