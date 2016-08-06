package ru.yandex.yamblz;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import ru.yandex.yamblz.developer_settings.DevMetricsProxy;
import ru.yandex.yamblz.developer_settings.DeveloperSettingsComponent;
import ru.yandex.yamblz.developer_settings.DeveloperSettingsModel;
import ru.yandex.yamblz.developer_settings.DeveloperSettingsModule;
import ru.yandex.yamblz.developer_settings.LeakCanaryProxy;
import ru.yandex.yamblz.ui.activities.MainActivity;
import ru.yandex.yamblz.ui.adapters.GenresAdapter;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DeveloperSettingsModule.class,
})
public interface ApplicationComponent {

    // Provide LeakCanary without injection to leave.
    @NonNull
    LeakCanaryProxy leakCanaryProxy();

    @NonNull
    DeveloperSettingsComponent plusDeveloperSettingsComponent();

    DeveloperSettingsModel developerSettingModel();

    DevMetricsProxy devMetricsProxy();

    @NonNull @Named(ApplicationModule.MAIN_THREAD_HANDLER)
    Handler mainThreadHandler();

    @Named(ApplicationModule.MAIN_THREAD_POOL_EXECUTOR) @Singleton
    ThreadPoolExecutor mainExecutor();

    void inject(@NonNull MainActivity mainActivity);
    void inject(@NonNull GenresAdapter.GenresHolder genresHolder);
}
