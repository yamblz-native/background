package ru.yandex.yamblz;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.yandex.yamblz.loader.Cache;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageStrategy;
import ru.yandex.yamblz.loader.ImageDownloader;
import ru.yandex.yamblz.loader.ImagesLRUCache;
import ru.yandex.yamblz.loader.ParallelCollageLoader;
import ru.yandex.yamblz.loader.TableCollageStrategy;
import ru.yandex.yamblz.loader.UrlImageDownloader;

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
        return new ThreadPoolExecutor(4, 10, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
    }

    @Provides
    @Singleton
    ImageDownloader provideImageDownloader() {
        return new UrlImageDownloader();
    }

    @Provides
    @Singleton
    CollageStrategy provideCollageStrategy() {
        return new TableCollageStrategy(2, 2);
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

}
