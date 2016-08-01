package ru.yandex.yamblz;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.StubCriticalSectionsHandler;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.ExampleCollageLoader;
import timber.log.Timber;

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
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(2, TimeUnit.SECONDS);
        builder.readTimeout(2, TimeUnit.SECONDS);
        builder.writeTimeout(2, TimeUnit.SECONDS);
        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Timber.d("OkHttp Log ->> %s", message));
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addNetworkInterceptor(httpLoggingInterceptor);
        }
        return builder.build();
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
