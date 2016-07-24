package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelCollageLoader implements CollageLoader {

    private Executor mPostExecutor;
    private Executor mWorkerExecutor;
    private ImageDownloader mImageDownloader;
    private CollageStrategy mDefaultCollageStrategy;
    private Cache<String, Bitmap> mCache;

    public ParallelCollageLoader(@NonNull Executor postExecutor,
                                 @NonNull Executor workerExecutor,
                                 @NonNull ImageDownloader imageDownloader,
                                 @NonNull CollageStrategy defaultStrategy,
                                 @Nullable Cache<String, Bitmap> cache) {
        this.mPostExecutor = postExecutor;
        this.mWorkerExecutor = workerExecutor;
        this.mImageDownloader = imageDownloader;
        this.mDefaultCollageStrategy = defaultStrategy;
        this.mCache = cache;
    }

    @Override
    public Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView) {
        return process(urls, imageView, null);
    }

    @Override
    public Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget) {
        return process(urls, imageTarget, null);
    }

    @Override
    public Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView, @Nullable CollageStrategy collageStrategy) {
        return process(urls, imageView, collageStrategy);
    }

    @Override
    public Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget, @Nullable CollageStrategy collageStrategy) {
        return process(urls, imageTarget, collageStrategy);
    }


    private Subscription process(@NonNull List<String> urls, @NonNull Object listener,
                                 @Nullable CollageStrategy strategy) {
        Subscription subscription = new Subscription();
        new Job(urls, listener, subscription, strategy).doWork();
        return subscription;
    }

    private class Job {
        private final List<String> mUrls;
        private final WeakReference mListener;
        private final CollageStrategy mCollageStrategy;
        private final Subscription mSubscription;

        private List<Bitmap> mImages;
        private AtomicInteger mCntOfImages;

        private Job(@NonNull List<String> urls, @NonNull Object listener,
                    @Nullable Subscription subscription, @Nullable CollageStrategy strategy) {
            this.mUrls = new ArrayList<>(urls);
            this.mListener = new WeakReference(listener);
            this.mSubscription = subscription;
            this.mCollageStrategy = strategy;
            init();
        }

        private void init() {
            this.mCntOfImages = new AtomicInteger(mUrls.size());
            this.mImages = new ArrayList<>(mUrls.size());
        }

        private void doWork() {
            for (String url : mUrls) {
                mWorkerExecutor.execute(() -> processUrl(url));
            }
        }

        private void processUrl(@NonNull String url) {
            if(mCache != null && mCache.containsKey(url)) {
                postImage(mCache.get(url));
            } else {
                Bitmap image = mImageDownloader.downloadBitmap(url);
                if(mCache != null) {
                    mCache.put(url, image);
                }
                postImage(image);
            }
        }

        private void postImage(Bitmap image) {
            int left = mCntOfImages.decrementAndGet();
            synchronized (mImages) {
                mImages.add(image);
            }
            if (left == 0) {
                postResult(transformToCollage());
            }

        }

        private Bitmap transformToCollage() {
            if (mCollageStrategy != null) {
                return mCollageStrategy.create(mImages);
            } else {
                return mDefaultCollageStrategy.create(mImages);
            }
        }

        private void postResult(Bitmap result) {
            if(mSubscription.isSubscribed()) {
                mPostExecutor.execute(() -> {
                    Object listener = mListener.get();
                    if (listener == null) {
                        return;
                    }
                    if (listener instanceof ImageView) {
                        ((ImageView) listener).setImageBitmap(result);
                    } else if (listener instanceof ImageTarget) {
                        ((ImageTarget) listener).onLoadBitmap(result);
                    }
                });
            }
        }
    }

}
