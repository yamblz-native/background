package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Александр on 25.07.2016.
 */

public class ExampleCollageLoader implements CollageLoader {
    private final OkHttpClient okHttpClient;
    private final Bitmap placeHolder;
    private final CriticalSectionsHandler criticalSectionsHandler;



    {
        placeHolder = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        placeHolder.eraseColor(Color.rgb(255, 255, 255));
    }

    public ExampleCollageLoader(OkHttpClient okHttpClient, CriticalSectionsHandler criticalSectionsHandler) {
        this.okHttpClient = okHttpClient;
        this.criticalSectionsHandler = criticalSectionsHandler;
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, new ImageViewImageTarget(imageView));
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, null);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        loadCollage(urls, new ImageViewImageTarget(imageView), collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, final ImageTarget imageTarget, CollageStrategy collageStrategy) {
        if (collageStrategy == null) collageStrategy = new RandomCollageStrategy(600, 400);

        Observable.just(urls)
                .flatMap(loadBitmaps())
                .map(createCollage(collageStrategy))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(processResult(imageTarget));
    }

    @NonNull
    private Subscriber<Bitmap> processResult(final ImageTarget imageTarget) {
        return new Subscriber<Bitmap>() {
            @Override
            public void onStart() {
                super.onStart();
                imageTarget.onLoadBitmap(placeHolder);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Bitmap bitmap) {
                criticalSectionsHandler.postLowPriorityTask(() ->imageTarget.onLoadBitmap(bitmap));
            }
        };
    }

    @NonNull
    private Func1<List<Bitmap>, Bitmap> createCollage(final CollageStrategy finalCollageStrategy) {
        return finalCollageStrategy::create;
    }

    @NonNull
    private Func1<List<String>, Observable<List<Bitmap>>> loadBitmaps() {
        final Executor executor = createExecutor();
        return strings -> Observable.from(strings)
                .flatMap(new Func1<String, Observable<Bitmap>>() {
                    @Override
                    public Observable<Bitmap> call(String s) {
                        return loadOneBitmap(executor, s);
                    }
                })
                .reduce(new ArrayList<Bitmap>(strings.size()), new Func2<List<Bitmap>, Bitmap, List<Bitmap>>() {
                    @Override
                    public List<Bitmap> call(List<Bitmap> bitmaps, Bitmap bitmap) {
                        bitmaps.add(bitmap);
                        return bitmaps;
                    }
                });
    }

    @NonNull
    private ExecutorService createExecutor() {
        return Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MIN_PRIORITY + 3);
            return thread;
        });
    }

    //разкоментировать чтобы увидеть интересные логи
    //@RxLogObservable
    private Observable<Bitmap> loadOneBitmap(Executor executor, String bitmapUrl) {
        return Observable.just(bitmapUrl)
                .map(s -> {
                    try {
                        return BitmapFactory.decodeStream(okHttpClient
                                .newCall(new Request.Builder().url(s).build()).execute().body().byteStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribeOn(Schedulers.from(executor));
    }

    public static class ImageViewImageTarget implements ImageTarget{
        private final WeakReference<ImageView> wrImageView;

        public ImageViewImageTarget(ImageView wrImageView) {
            this.wrImageView = new WeakReference<>(wrImageView);
        }

        @Override
        public void onLoadBitmap(Bitmap bitmap) {
            ImageView imageView = wrImageView.get();
            if (imageView != null){
                imageView.setImageBitmap(bitmap);
            }
        }

        public void cancel(){
            wrImageView.clear();
        }
    }
}
