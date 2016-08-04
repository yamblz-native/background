package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
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
    @NonNull private final WeakHashMap<ImageTarget, Subscription> subscriptionTargets;
    @NonNull private final CompositeSubscription subs;
    @NonNull private final CollageStrategy collageStrategy;
    @NonNull private final LruCache<List<String>, Bitmap> memoryCache;

    public ParallelCollageLoader() {
        subscriptionTargets = new WeakHashMap<>();
        subs = new CompositeSubscription();
        collageStrategy = new SimpleCollageStrategy();
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

    @Override
    public void loadCollage(List<String> urls, final ImageView imageView, CollageStrategy collageStrategy) {
        ImageTarget target = bitmap -> {
            imageView.setAlpha(0f);
            imageView.setImageBitmap(bitmap);
            imageView.animate().alpha(1);
        };
        loadCollage(urls, target, collageStrategy);
    }

    private void loadBitmap(ImageTarget imageTarget, Bitmap cachedCollage) {
        CriticalSectionsManager.getHandler().postLowPriorityTask(() -> imageTarget.onLoadBitmap(cachedCollage));
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        Subscription s = subscriptionTargets.get(imageTarget);
        if (s != null) {
            subs.remove(s);
        }
        Bitmap cachedCollage = memoryCache.get(urls);
        if (cachedCollage != null) {
            Timber.d("Cache hit! Adding task to handler");
            loadBitmap(imageTarget, cachedCollage);
        } else {
            Subscription subscription = Observable.zip(loadBitmaps(urls),
                    args -> args)
                    .flatMap(Observable::from)
                    .map(o -> (Bitmap) o)
                    .toList()
                    .map(collageStrategy::create)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(collage -> {
                                memoryCache.put(urls, collage);
                                Timber.d("Image loaded! Adding task to handler");
                                loadBitmap(imageTarget, collage);
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

    @Override
    public void destroyAll() {
        subs.clear();
        subscriptionTargets.clear();
    }

}
