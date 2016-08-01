package ru.yandex.yamblz;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.StubCriticalSectionsHandler;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.ExampleCollageLoader;

@Module
public class ApplicationModule {

    public static final String MAIN_THREAD_HANDLER = "main_thread_handler";

    @NonNull
    private final Application application;

    public ApplicationModule(@NonNull Application application) {
        this.application = application;
    }

    @Provides @NonNull @Singleton
    public Application provideYamblzApp() {
        return application;
    }

    @Provides @NonNull @Named(MAIN_THREAD_HANDLER) @Singleton
    public Handler provideMainThreadHandler() {
        return new Handler(Looper.getMainLooper());
    }


    @Provides @Singleton
    public OkHttpClient provideOkHttpClient(){
        return new OkHttpClient.Builder().build();
    }

    @Provides @Singleton
    public CollageLoader provideCollageLoader(OkHttpClient okHttpClient, CriticalSectionsHandler criticalSectionsHandler){
        return new ExampleCollageLoader(okHttpClient, criticalSectionsHandler);
    }

    @Provides @Singleton
    public CriticalSectionsHandler provideCriticalSectionsHandler(){
        return new StubCriticalSectionsHandler();
    }

}
