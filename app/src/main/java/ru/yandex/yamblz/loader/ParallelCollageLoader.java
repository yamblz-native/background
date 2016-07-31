package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

import ru.yandex.yamblz.images.Cache;
import ru.yandex.yamblz.images.ImageDownloader;

/**
 * A {@link CollageLoader} which loads images using executors (so it's possible to use as many
 * threads as you want) and posts them back to UIThread
 * Supports subscribe/unsubscribe mechanism.
 * Supports caching mechanism.
 */
public class ParallelCollageLoader implements CollageLoader {

    /**
     * The executor to post result back on UI thread
     */
    @NonNull private Executor mPostExecutor;

    /**
     * The executor which does hard work - loading images, transforming them etc.
     */
    @NonNull private Executor mWorkerExecutor;

    /**
     * Downloads images
     */
    @NonNull private ImageDownloader mImageDownloader;

    /**
     * Collages images into one
     */
    @NonNull private CollageStrategy mDefaultCollageStrategy;

    /**
     * Caches the images by URL
     */
    @Nullable private Cache<String, Bitmap> mCache;

    @NonNull private final Map<Object, Subscription> mListeners2subscriptions = new WeakHashMap<>();

    public ParallelCollageLoader(@NonNull Executor postExecutor,
                                 @NonNull Executor workerExecutor,
                                 @NonNull ImageDownloader imageDownloader,
                                 @NonNull CollageStrategy defaultStrategy,
                                 @Nullable Cache<String, Bitmap> cache) {
        mPostExecutor = postExecutor;
        mWorkerExecutor = workerExecutor;
        mImageDownloader = imageDownloader;
        mDefaultCollageStrategy = defaultStrategy;
        mCache = cache;
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

    /**
     * Starts processing job
     * @param urls the urls to download images from
     * @param listener the listener
     * @param strategy the strategy to combine downloaded images
     * @return subscription to make it possible to unsubscribe
     */
    private Subscription process(@NonNull List<String> urls, @NonNull Object listener,
                                 @Nullable CollageStrategy strategy) {
        Subscription subscription = new Subscription();
        addListenerToMapAndCancelPrev(listener, subscription);
        new Job(urls, listener, subscription, strategy).doWork();
        return subscription;
    }

    private void addListenerToMapAndCancelPrev(Object listener, Subscription subscription) {
        cancelPrevSubscription(listener);
        mListeners2subscriptions.put(listener, subscription);
    }

    private void cancelPrevSubscription(Object listener) {
        if(mListeners2subscriptions.containsKey(listener)) {
            Subscription oldSubscription = mListeners2subscriptions.get(listener);
            if(oldSubscription.isSubscribed()) {
                oldSubscription.unsubscribe();
            }
        }
    }

    /**
     * Unit of work. Does downloading, transforming, collaging, posting back
     */
    private class Job {
        private final List<String> mUrls;
        private final WeakReference mListener;
        private final CollageStrategy mCollageStrategy;
        private final Subscription mSubscription;

        /**
         * Downloaded images
         */
        private List<Bitmap> mImages;

        /**
         * How many to download left
         */
        private int mCntOfImages;

        private Job(@NonNull List<String> urls, @NonNull Object listener,
                    @Nullable Subscription subscription, @Nullable CollageStrategy strategy) {
            mUrls = new ArrayList<>(urls);
            mListener = new WeakReference(listener);
            mSubscription = subscription;
            mCollageStrategy = strategy;
            init();
        }

        private void init() {
            mCntOfImages = mUrls.size();
            mImages = new ArrayList<>(mUrls.size());
        }

        /**
         * Start downloading by posting to {@link #mWorkerExecutor}
         */
        private void doWork() {
            for (String url : mUrls) {
                mWorkerExecutor.execute(() -> processUrl(url));
            }
        }

        /**
         * Gets image from the cache or downloads it
         * @param url the url of image
         */
        private void processUrl(@NonNull String url) {
            if(!mSubscription.isSubscribed()) {
                return;
            }
            if(mCache != null && mCache.containsKey(url)) {
                postImage(mCache.get(url));
            } else {
                Bitmap image = mImageDownloader.downloadBitmap(url);
                if(image != null) { //TODO handle case when image wasn't downloaded
                    if(mCache != null) {
                        mCache.put(url, image);
                    }
                    postImage(image);
                }
            }
        }

        /**
         * Called when image was retrieved. If all images were downloaded posts result back
         * @param image
         */
        private void postImage(Bitmap image) {
            if(!mSubscription.isSubscribed()) {
                return;
            }
            synchronized (mImages) {
                mImages.add(image);
                mCntOfImages--;
            }
            if (mCntOfImages == 0) {
                postResult(transformToCollage());
            }

        }

        /**
         * Transforms downloaded image
         * @return the result
         */
        private Bitmap transformToCollage() {
            if(!mSubscription.isSubscribed()) {
                return null;
            }
            if (mCollageStrategy != null) {
                return mCollageStrategy.create(mImages);
            } else {
                return mDefaultCollageStrategy.create(mImages);
            }
        }

        /**
         * Posts result back to either {@link ImageView} or {@link ImageTarget}
         * @param result the result collage
         */
        private void postResult(Bitmap result) {
            if(!mSubscription.isSubscribed()) {
                return;
            }
            mPostExecutor.execute(() -> {
                if(!mSubscription.isSubscribed()) {
                    return;
                }
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
