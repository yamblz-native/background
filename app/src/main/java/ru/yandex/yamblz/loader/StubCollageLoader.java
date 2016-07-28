package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.schedulers.Schedulers;

public class StubCollageLoader implements CollageLoader {
    public static final String DEBUG_TAG = StubCollageLoader.class.getName();

    private static final Scheduler SCHEDULER = Schedulers.from(Executors.newFixedThreadPool(10));

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, new FourImagesCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, new FourImagesCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        loadCollage(urls, new ImageViewImageTarget(new WeakReference<ImageView>(imageView)), collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        int n = Math.min(urls.size(), collageStrategy.amountOfImagesNeeded());
        urls = urls.subList(0, n);

        Subscription subscription = Observable.from(urls)
                .flatMap(s -> Observable.just(s)
                        .observeOn(SCHEDULER)
                        .map(this::loadBitmapFromUrl))
                .collect(LinkedList::new, new Action2<LinkedList<Bitmap>, Bitmap>() {
                    @Override
                    public void call(LinkedList<Bitmap> bitmaps, Bitmap bitmap) {
                        bitmaps.add(bitmap);
                    }
                })
                .map(collageStrategy::create)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap collage) {
                        Task task = new ImageViewSetter(imageTarget, collage);
                        imageTarget.setTask(task);
                        CriticalSectionsManager.getHandler()
                                .postLowPriorityTask(task);
                    }
                });
        imageTarget.setSubscription(subscription);
    }

    protected Bitmap loadBitmapFromUrl(String stringUrl) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(stringUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public class ImageViewSetter implements Task {
        private ImageTarget target;
        private Bitmap collage;

        public ImageViewSetter(ImageTarget target, Bitmap collage) {
            this.target = target;
            this.collage = collage;
        }

        @Override
        public void run() {
            target.onLoadBitmap(collage);
        }
    }
}
