package ru.yandex.yamblz;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import ru.yandex.yamblz.developer_settings.DevMetricsProxy;
import ru.yandex.yamblz.developer_settings.DeveloperSettingsModel;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsHandlerImpl;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import timber.log.Timber;

public class App extends Application {
    private ApplicationComponent applicationComponent;

    // Prevent need in a singleton (global) reference to the application object.
    @NonNull
    public static App get(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = prepareApplicationComponent().build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            DeveloperSettingsModel developerSettingModel = applicationComponent.developerSettingModel();
            developerSettingModel.apply();

            DevMetricsProxy devMetricsProxy = applicationComponent.devMetricsProxy();
            devMetricsProxy.apply();
        }

        CollageLoaderManager.init(null);  // add implementation
        CriticalSectionsManager.init(new CriticalSectionsHandlerImpl());

        // Testing
        CriticalSectionsHandler handler = CriticalSectionsManager.getHandler();
        Log.d("test", "adding testing tasks");
        Log.d("test", "Expected output: first task, second task, fourth task");
        handler.postLowPriorityTask(() -> {
            Log.d("test", "first task");

            Task thirdTask = () -> Log.e("test", "third task"); // Shouldn't happen
            Task secondTask = () -> {
                Log.d("test", "second task");
                handler.removeLowPriorityTask(thirdTask);

                Task fifthTask = () -> Log.e("test", "fifth task"); // Shouldn't happen
                handler.postLowPriorityTaskDelayed(() -> {
                    Log.d("test", "fourth task");
                    handler.removeLowPriorityTask(fifthTask);
                }, 1000);
                handler.postLowPriorityTaskDelayed(fifthTask, 1500);
            };
            handler.postLowPriorityTask(secondTask);
            handler.postLowPriorityTask(thirdTask);
        });
    }

    @NonNull
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this));
    }

    @NonNull
    public ApplicationComponent applicationComponent() {
        return applicationComponent;
    }
}
