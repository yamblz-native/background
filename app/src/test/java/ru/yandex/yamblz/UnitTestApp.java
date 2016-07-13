package ru.yandex.yamblz;

import android.app.Application;
import android.support.annotation.NonNull;

import ru.yandex.yamblz.developer_settings.DevMetricsProxy;
import ru.yandex.yamblz.developer_settings.DeveloperSettingsModule;

public class UnitTestApp extends App {

    @NonNull
    @Override
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return super.prepareApplicationComponent()
                .developerSettingsModule(new DeveloperSettingsModule() {
                    @NonNull
                    public DevMetricsProxy provideDevMetricsProxy(@NonNull Application application) {
                        return () -> {
                            //No Op
                        };
                    }
                });
    }
}
