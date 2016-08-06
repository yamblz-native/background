package ru.yandex.yamblz;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    public static final String MAIN_THREAD_HANDLER = "main_thread_handler";
    public static final String MAIN_THREAD_POOL_EXECUTOR = "main_thread_pool_executor";

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

    @Provides @Named(ApplicationModule.MAIN_THREAD_POOL_EXECUTOR) @Singleton
    ThreadPoolExecutor provideMainExecutor() {
        int nCores = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(nCores, nCores, 120, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

}
