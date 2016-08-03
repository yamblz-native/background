package ru.yandex.yamblz.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


public class ParallelCollageLoader implements CollageLoader {
    private WeakHashMap<ImageView, Subscription> subscriptionTargets = new WeakHashMap<>();
    private CompositeSubscription subs = new CompositeSubscription();
    private CollageStrategy collageStrategy = new SimpleCollageStrategy();
    private LruCache<List<String>, Bitmap> memoryCache;
    private Context context;

    public ParallelCollageLoader(Context context) {
        this.context = context.getApplicationContext();
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<>(cacheSize);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, this.collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, this.collageStrategy);
    }

    // Для параллельной загрузки еще можно использовать операторы .groupBy и
    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        if (subs == null) subs = new CompositeSubscription();
        if (subscriptionTargets.get(imageView) != null) {
            subs.remove(subscriptionTargets.get(imageView));
        }
        Bitmap cachedCollage = memoryCache.get(urls);
        if (cachedCollage != null) {
            Timber.d("Cache hit! Adding task to handler");
            CriticalSectionsManager.getHandler().postLowPriorityTask(() -> {
                Timber.d("Loading cached image");
                imageView.setAlpha(0f);
                imageView.setImageBitmap(cachedCollage);
                imageView.animate()
                        .alpha(1);

            });
            Timber.d("Adding handler");
            Looper.myQueue().addIdleHandler(CriticalSectionsManager.getHandler());

        } else {
            Subscription subscription = Observable.zip(loadBitmaps(urls),
                    args -> {
                        List<Bitmap> loadedBitmaps = new ArrayList<>();
                        // Вот тут хз, наверняка можно как-то лаконичнее написать
                        for (Object o : args) {
                            if (o instanceof Bitmap) {
                                loadedBitmaps.add((Bitmap) o);
                            }
                        }
                        return collageStrategy.create(loadedBitmaps);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(collage -> {
                                memoryCache.put(urls, collage);
                                Timber.d("Image loaded! Adding task to handler");
                                CriticalSectionsManager.getHandler().postLowPriorityTask(() -> {
                                    Timber.d("Loading image from web into view");
                                    imageView.setAlpha(0f);
                                    imageView.setImageBitmap(collage);
                                    imageView.animate()
                                            .alpha(1);

                                });
                                Timber.d("Adding handler");
                                Looper.myQueue().addIdleHandler(CriticalSectionsManager.getHandler());

                            },
                            Throwable::printStackTrace
                    );
            subs.add(subscription);
            subscriptionTargets.put(imageView, subscription);
        }
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {

    }

    private List<Observable<Bitmap>> loadBitmaps(List<String> urls) {
        List<Observable<Bitmap>> observables = new ArrayList<>();
        for (String urlString : urls) {
            observables.add(Observable.fromCallable(() -> {
                InputStream in = null;
                try {
                    in = new java.net.URL(urlString).openStream();
                    return BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) in.close();
                }
                return null;
            }));
        }

        return observables;
    }

    // TODO: Можно ли как-то автоматизировать очистку подписок?
    // Нехорошо, что фрагмент управляет очисткой подписок. Мб вынести метод в менеджер?
    @Override
    public void destroyAll() {
        if (subs != null) {
            subs.clear();
            subs = null;
        }
        subscriptionTargets.clear();
    }

}
