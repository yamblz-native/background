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


    public ParallelCollageLoader(@NonNull Executor postExecutor, @NonNull Executor workerExecutor,
                                 @NonNull ImageDownloader imageDownloader,
                                 @NonNull CollageStrategy defaultStrategy) {
        this.mPostExecutor = postExecutor;
        this.mWorkerExecutor = workerExecutor;
        this.mImageDownloader = imageDownloader;
        this.mDefaultCollageStrategy = defaultStrategy;
    }

    private class Job {
        private final List<String> mUrls;
        private final WeakReference mListener;
        private final CollageStrategy mCollageStrategy;

        private List<Bitmap> mImages;
        private AtomicInteger mCntOfImages;

        private Job(@NonNull List<String> urls, @NonNull Object listener, @Nullable CollageStrategy strategy) {
            this.mUrls = new ArrayList<>(urls);
            this.mListener = new WeakReference(listener);
            this.mCollageStrategy = strategy;
            init();
        }

        private void init() {
            this.mCntOfImages = new AtomicInteger(mUrls.size());
            this.mImages = new ArrayList<>(mUrls.size());
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


    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        process(urls, imageView, null);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        process(urls, imageTarget, null);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        process(urls, imageView, collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        process(urls, imageTarget, collageStrategy);
    }

    private void process(@NonNull List<String> urls, @NonNull Object listener,
                         @Nullable CollageStrategy strategy) {
        processJob(new Job(urls, listener, strategy));
    }

    private void processJob(Job job) {
        for (String url : job.mUrls) {
            mWorkerExecutor.execute(() -> job.postImage(mImageDownloader.downloadBitmap(url)));
        }
    }
}
