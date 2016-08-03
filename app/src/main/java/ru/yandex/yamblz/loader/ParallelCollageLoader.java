package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import ru.yandex.yamblz.handler.CriticalSectionsManager;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static ru.yandex.yamblz.utils.Utils.loadBitmapFromUrl;


public class ParallelCollageLoader implements CollageLoader {
    private WeakHashMap<ImageView, Subscription> subscriptionImages = new WeakHashMap<>();
    private  WeakHashMap<ImageTarget, Subscription> subscriptionTargets = new WeakHashMap<>();
    private CompositeSubscription subs = new CompositeSubscription();
    private CollageStrategy collageStrategy = new SimpleCollageStrategy();
    private LruCache<List<String>, Bitmap> memoryCache;

    public ParallelCollageLoader() {
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

    // Для параллельной загрузки еще можно использовать операторы .groupBy или связку .flatMap().toList().toBlocking()
    // Очень длинный метод. Ну прямо очень. Фу.
    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        if (subs == null) subs = new CompositeSubscription();
        if (subscriptionImages.get(imageView) != null) {
            subs.remove(subscriptionImages.get(imageView));
        }
        Bitmap cachedCollage = memoryCache.get(urls);
        if (cachedCollage != null) {
            Timber.d("Cache hit! Adding task to handler");
            loadBitmap(imageView, cachedCollage);
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
                                loadBitmap(imageView, collage);

                            },
                            Throwable::printStackTrace
                    );
            subs.add(subscription);
            subscriptionImages.put(imageView, subscription);
        }
    }

    private void loadBitmap(ImageView imageView, Bitmap cachedCollage) {
        CriticalSectionsManager.getHandler().postLowPriorityTask(() -> {
            imageView.setAlpha(0f);
            imageView.setImageBitmap(cachedCollage);
            imageView.animate()
                    .alpha(1);

        });
        Looper.myQueue().addIdleHandler(CriticalSectionsManager.getHandler());
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        if (subs == null) subs = new CompositeSubscription();
        Subscription s = subscriptionTargets.get(imageTarget);
        if (s != null) {
            subs.remove(s);
        }
        Bitmap cachedCollage = memoryCache.get(urls);
        if (cachedCollage != null) {
            Timber.d("Cache hit! Adding task to handler");
            imageTarget.onLoadBitmap(cachedCollage);
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
                                imageTarget.onLoadBitmap(collage);

                            },
                            Throwable::printStackTrace
                    );
            subs.add(subscription);
            subscriptionTargets.put(imageTarget, subscription);

        }
    }

    private List<Observable<Bitmap>> loadBitmaps(List<String> urls) {
        List<Observable<Bitmap>> observables = new ArrayList<>();
        for (String urlString : urls) {
            observables.add(Observable.fromCallable(() -> loadBitmapFromUrl(urlString)));
        }

        return observables;
    }


    // TODO: Можно ли как-то автоматизировать очистку подписок?
    // Нехорошо, что фрагмент управляет очисткой подписок. Мб вынести метод в менеджер?
    // Нужно это как-то автоматизировать
    @Override
    public void destroyAll() {
        if (subs != null) {
            subs.clear();
            subs = null;
        }
        subscriptionImages.clear();
    }

}
