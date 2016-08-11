package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DefaultCollageLoader implements CollageLoader {
    private CollageStrategy defaultStrategy = new SquareCollageStrategy();
    private CompositeSubscription compositeSubscription;
    private Map<Object, Subscription> subscriptionsMap = new WeakHashMap<>();
    private LruCache<List<String>, Bitmap> bitmapCache;

    private Map<ImageTarget, WeakReference<Task>> loadingTasks = new WeakHashMap<>();


    // Начитался https://habrahabr.ru/post/265997/
    public DefaultCollageLoader(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;

        int cacheSize = (int) (Runtime.getRuntime().maxMemory());

        bitmapCache = new LruCache<List<String>, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(List<String> key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, new SimpleImageTarget(imageView), defaultStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, defaultStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, new SimpleImageTarget(imageView), collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        if (subscriptionsMap.containsKey(imageTarget)) {
            compositeSubscription.remove(subscriptionsMap.get(imageTarget));
            subscriptionsMap.remove(imageTarget);
        }


        Subscription loading = Observable
                .concat(collageFromCache(urls), collageFromNetwork(urls, collageStrategy))
                .first(bitmap -> bitmap != null)
                .doOnNext(bitmap -> bitmapCache.put(urls, bitmap))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (bitmap) -> {
                            if (loadingTasks.containsKey(imageTarget)) {
                                CriticalSectionsManager.getHandler()
                                        .removeLowPriorityTask(loadingTasks.get(imageTarget).get());
                            }
                            Task task = () -> imageTarget.onLoadBitmap(bitmap);
                            CriticalSectionsManager.getHandler().postLowPriorityTask(task);
                            loadingTasks.put(imageTarget, new WeakReference<>(task));
                        },
                        e -> Log.e("E:DefaultCollageLoader", "Collage downloading failed: " + e),
                        () -> Log.d("D:DefaultCollageLoader", "Collage downloading completed")
                );
        subscriptionsMap.put(imageTarget, loading);
        compositeSubscription.add(loading);
    }

    private Observable<Bitmap> collageFromCache(List<String> urls) {
        return Observable.create(subscriber -> {
            subscriber.onNext(bitmapCache.get(urls));
            subscriber.onCompleted();
        });
    }

    private Observable<Bitmap> collageFromNetwork(List<String> urls, CollageStrategy collageStrategy) {
        return Observable.from(urls)
                .flatMap(url -> getBitmapObservableFromURL(url).subscribeOn(Schedulers.io()))
                .toList()
                .map(collageStrategy::create);
    }

    private Observable<Bitmap> getBitmapObservableFromURL(String url) {
        return Observable.fromCallable(
                () -> getBitmapFromURL(url)
        );
    }

    private Bitmap getBitmapFromURL(String src) {
        Log.e("THREAD", " " + Thread.currentThread().toString());
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(src).openConnection();
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e("E:DefaultCollageLoader", "Image downloading failed: " + src);
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}