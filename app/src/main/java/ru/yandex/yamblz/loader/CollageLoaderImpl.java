package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CollageLoaderImpl implements CollageLoader {
    private static final int NUMBER_OF_THREADS = 2;
    private SparseArray<Subscription> mSubscriptionMap = new SparseArray<>(); // Хранит подписки

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, new CollageStrategyImpl());
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, new CollageStrategyImpl());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        WeakReference<ImageView> imageViewRef = new WeakReference<>(imageView);
        int imageViewId = imageView.getId();
        Subscription subscription = mSubscriptionMap.get(imageViewId);
        if (subscription != null) {
            subscription.unsubscribe(); // Негоже грузить 42 коллажа в одну картинковьюху
            Log.d("UNSBSRB", "ImageViewHash=" + imageView.hashCode());
        }

        List<Bitmap> bitmapList = new ArrayList<>();

        Action1<? super Bitmap> action1 = new Action1<Bitmap>() {
            private List<Bitmap> mBitmaps = new ArrayList<>();

            @Override
            public void call(Bitmap bitmap) {
                mBitmaps.add(bitmap);
                //Math.round(Math.sqrt(mBitmaps.size())) - Math.sqrt(mBitmaps.size()) == 0
                if (Math.round(Math.sqrt(mBitmaps.size())) - Math.sqrt(mBitmaps.size()) == 0) {
                    Bitmap collage = collageStrategy.create(mBitmaps);
                    ImageView collageView = imageViewRef.get();
                    if (collageView != null) {
                        collageView.setImageBitmap(collage);
                    }
                }
            }
        };

        Action0 action0 = new Action0() {
            @Override
            public void call() {

            }
        };

        Subscription newSubscription;
        newSubscription = makeCollage(urls, bitmapList, action0, action1);


        mSubscriptionMap.put(imageViewId, newSubscription);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        List<Bitmap> bitmapList = new ArrayList<>();
        //makeCollage(urls, bitmapList, () -> imageTarget.onLoadBitmap(collageStrategy.create(bitmapList)));
    }

    // Как-то костыльно без rx everywhere, но не переписывать же интерфейсы?
    private Subscription makeCollage(List<String> urls, List<Bitmap> bitmapList, @NonNull Action0 onCompleted, Action1 onNext) {
        return collageObservable(urls)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, (e) -> {
                }, onCompleted);
    }

    private Observable<Bitmap> collageObservable(List<String> urls) {
        AtomicInteger counter = new AtomicInteger();
        return Observable.from(urls)
                .groupBy(s -> counter.getAndIncrement() % NUMBER_OF_THREADS)
                .flatMap(g -> g.observeOn(Schedulers.newThread()))
                .map(this::downloadImage);
    }

    // Кеширование бы, но только на диск, иначе память быстро закончится
    private Bitmap downloadImage(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            return BitmapFactory.decodeStream(response.body().byteStream());
        } else {
            return null;
        }
    }
}