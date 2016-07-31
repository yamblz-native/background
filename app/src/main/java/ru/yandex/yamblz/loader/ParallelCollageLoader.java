package ru.yandex.yamblz.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class ParallelCollageLoader implements CollageLoader {
    private WeakHashMap<ImageView, Subscription> subscriptionTargets = new WeakHashMap<>();
    private CompositeSubscription subs = new CompositeSubscription();
    private CollageStrategy collageStrategy = new SimpleCollageStrategy();

    public ParallelCollageLoader() {

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
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        if (subscriptionTargets.get(imageView) != null) {
            subs.remove(subscriptionTargets.get(imageView));
        }
        Subscription subscription = Observable.zip(loadBitmaps(urls),
                args -> {
                    List<Bitmap> loadedBitmaps = new ArrayList<>();
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
                            imageView.setAlpha(0f);
                            imageView.setImageBitmap(collage);
                            imageView.animate()
                                    .alpha(1);

                        },
                        Throwable::printStackTrace
                );
        subs.add(subscription);
        subscriptionTargets.put(imageView, subscription);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {

    }

    private List<Observable<Bitmap>> loadBitmaps(List<String> urls) {
        List<Observable<Bitmap>> observables = new ArrayList<>();
        for (String urlString : urls) {
            observables.add(Observable.create(subscriber -> {
                try {
                    URL url = new URL(urlString);
                    InputStream is = url.openConnection().getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }));
        }

        return observables;
    }
}
