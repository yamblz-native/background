package ru.yandex.yamblz;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import ru.yandex.yamblz.api.AssetsSingersApi;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsHandlerThread;
import ru.yandex.yamblz.images.Cache;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageStrategy;
import ru.yandex.yamblz.images.ImageDownloader;
import ru.yandex.yamblz.images.ImagesLRUCache;
import ru.yandex.yamblz.loader.ParallelCollageLoader;
import ru.yandex.yamblz.api.SingersApi;
import ru.yandex.yamblz.loader.TableCollageStrategy;
import ru.yandex.yamblz.images.UrlImageDownloader;

@Module
public class ApplicationModule {

    public static final String MAIN_THREAD_HANDLER = "main_thread_handler";

    public static final String POST_EXECUTOR = "POST_EXECUTOR";

    public static final String WORKER_EXECUTOR = "WORKER_EXECUTOR";

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

    @Provides
    @Singleton
    @Named(POST_EXECUTOR)
    Executor providePostExecutor() {
        Handler handler = new Handler(Looper.getMainLooper());
        Executor executor = handler::post;
        return executor;
    }

    @Provides
    @Singleton
    @Named(WORKER_EXECUTOR)
    Executor provideWorkerExecutor() {
        return new ThreadPoolExecutor(4, 10, 120, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

    @Provides
    @Singleton
    ImageDownloader provideImageDownloader(OkHttpClient okHttpClient) {
        return new UrlImageDownloader(okHttpClient);
    }

    @Provides
    @Singleton
    CollageStrategy provideCollageStrategy() {
        return new TableCollageStrategy();
    }

    @Provides
    @Singleton
    Cache<String, Bitmap> provideImageCache() {
        return new ImagesLRUCache(4 * 1024 * 1024);
    }

    @Provides
    @Singleton
    CollageLoader provideCollageLoader(@Named(POST_EXECUTOR) Executor postExecutor,
                                       @Named(WORKER_EXECUTOR) Executor workerExecutor,
                                       ImageDownloader imageDownloader,
                                       CollageStrategy defaultStrategy,
                                       Cache<String, Bitmap> cache) {
        return new ParallelCollageLoader(postExecutor, workerExecutor, imageDownloader,
                defaultStrategy, cache);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    SingersApi provideSingersApi(Application application) {
        return new AssetsSingersApi(application);
    }

    @Provides
    @Singleton
    CriticalSectionsHandler provideUiCriticalSectionsHandler(@Named(MAIN_THREAD_HANDLER) Handler handler) {
        return new CriticalSectionsHandlerThread(handler);
    }

}
