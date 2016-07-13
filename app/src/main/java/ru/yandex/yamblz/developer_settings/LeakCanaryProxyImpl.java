package ru.yandex.yamblz.developer_settings;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class LeakCanaryProxyImpl implements LeakCanaryProxy {

    @NonNull
    private final Application yamblzApp;

    @Nullable
    private RefWatcher refWatcher;

    public LeakCanaryProxyImpl(@NonNull Application yamblzApp) {
        this.yamblzApp = yamblzApp;
    }

    @Override
    public void init() {
        refWatcher = LeakCanary.install(yamblzApp);
    }

    @Override
    public void watch(@NonNull Object object) {
        if (refWatcher != null) {
            refWatcher.watch(object);
        }
    }
}
