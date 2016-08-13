package ru.yandex.yamblz;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import ru.yandex.yamblz.developer_settings.DevMetricsProxy;
import ru.yandex.yamblz.developer_settings.DeveloperSettingsModel;
import ru.yandex.yamblz.genre.data.source.DataSource;
import ru.yandex.yamblz.genre.data.source.RemoteDataSource;
import ru.yandex.yamblz.genre.data.source.Repository;
import ru.yandex.yamblz.genre.data.source.local.CacheImpl;
import ru.yandex.yamblz.genre.data.source.local.JsonSerializer;
import ru.yandex.yamblz.genre.util.NetworkManager;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.SimpleCriticalSectionHandler;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.CriticalSectionsCollageLoader;
import ru.yandex.yamblz.loader.SimpleCollageLoader;
import timber.log.Timber;

public class App extends Application
{
    private ApplicationComponent applicationComponent;
    private DataSource artistRepository;

    // Prevent need in a singleton (global) reference to the application object.
    @NonNull
    public static App get(@NonNull Context context)
    {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        applicationComponent = prepareApplicationComponent().build();

        if (BuildConfig.DEBUG)
        {
            Timber.plant(new Timber.DebugTree());

            DeveloperSettingsModel developerSettingModel = applicationComponent.developerSettingModel();
            developerSettingModel.apply();

            DevMetricsProxy devMetricsProxy = applicationComponent.devMetricsProxy();
            devMetricsProxy.apply();
        }

        NetworkManager.init(getApplicationContext());
        artistRepository = new Repository(
                new CacheImpl(getCacheDir(), new JsonSerializer()),
                new RemoteDataSource());

        // первое задание, раскомментировать
        //CollageLoaderManager.init(new SimpleCollageLoader(new Handler(getMainLooper())));

        //второе задание, соотвественно, закомментировать
        CriticalSectionsManager.init(new SimpleCriticalSectionHandler(new Handler(getMainLooper())));
        CollageLoaderManager.init(new CriticalSectionsCollageLoader());
    }

    @NonNull
    protected DaggerApplicationComponent.Builder prepareApplicationComponent()
    {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this));
    }

    @NonNull
    public ApplicationComponent applicationComponent()
    {
        return applicationComponent;
    }

    public DataSource getArtistRepository()
    {
        return artistRepository;
    }
}
