package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelCollageLoader implements CollageLoader {

    private Executor mPostExecutor;
    private Executor mWorkerExecutor;
    private Executor mCommunicationExecutor;
    private ImageDownloader mImageDownloader;
    private CollageStrategy mDefaultCollageStrategy;
    private ConcurrentMap<String, Object> mTags = new ConcurrentHashMap<>();

    public ParallelCollageLoader(@NonNull Executor postExecutor, @NonNull Executor workerExecutor,
                                 @NonNull Executor communicationExecutor,
                                 @NonNull ImageDownloader imageDownloader,
                                 @NonNull CollageStrategy defaultStrategy) {
        this.mPostExecutor = postExecutor;
        this.mWorkerExecutor = workerExecutor;
        this.mCommunicationExecutor = communicationExecutor;
        this.mImageDownloader = imageDownloader;
        this.mDefaultCollageStrategy = defaultStrategy;
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView) {
        process(urls, imageView, null, null);
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView, @Nullable String tag) {
        process(urls, imageView, tag, null);
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget) {
        process(urls, imageTarget, null, null);
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget, @Nullable String tag) {
        process(urls, imageTarget, tag, null);
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView, @Nullable CollageStrategy collageStrategy) {
        process(urls, imageView, null, collageStrategy);
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView, @Nullable String tag, @Nullable CollageStrategy collageStrategy) {
        process(urls, imageView, tag, collageStrategy);
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget, @Nullable CollageStrategy collageStrategy) {
        process(urls, imageTarget, null, collageStrategy);
    }

    @Override
    public void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget, @Nullable String tag, @Nullable CollageStrategy collageStrategy) {
        process(urls, imageTarget, tag, collageStrategy);
    }


    private void process(@NonNull List<String> urls, @NonNull Object listener,
                         @Nullable String tag, @Nullable CollageStrategy strategy) {
        mCommunicationExecutor.execute(() -> (new Job(urls, listener, tag, strategy)).doWork());
    }

    private void removeJob(@NonNull String tag) {
        mCommunicationExecutor.execute(() -> mTags.remove(tag));
    }

    private class Job {
        private final List<String> mUrls;
        private final WeakReference mListener;
        private final CollageStrategy mCollageStrategy;
        private final String mTag;

        private List<Bitmap> mImages;
        private AtomicInteger mCntOfImages;

        private Job(@NonNull List<String> urls, @NonNull Object listener, @Nullable String tag,
                    @Nullable CollageStrategy strategy) {
            this.mUrls = new ArrayList<>(urls);
            this.mListener = new WeakReference(listener);
            this.mTag = tag;
            this.mCollageStrategy = strategy;
            init();
        }

        private void init() {
            this.mCntOfImages = new AtomicInteger(mUrls.size());
            this.mImages = new ArrayList<>(mUrls.size());
        }

        private void doWork() {
            for (String url : mUrls) {
                mWorkerExecutor.execute(() -> postImage(mImageDownloader.downloadBitmap(url)));
            }
        }

        private void postImage(Bitmap bitmap) {
            int left = mCntOfImages.decrementAndGet();
            synchronized (mImages) {
                mImages.add(bitmap);
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
