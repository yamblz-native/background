package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import ru.yandex.yamblz.images.ImageCache;
import ru.yandex.yamblz.images.ImageDownloadTask;

/**
 * Created by grin3s on 06.08.16.
 */

public class ThreadedCollageLoader extends CollageLoader {

    ThreadPoolExecutor mExecutor;

    //we need this executor to avoid deadlocks in case we aggregate results in mExecutor
    Executor resultAggregationExecutor = Executors.newSingleThreadExecutor();

    Handler mainThreadHandler;

    WeakReference<ImageTarget> imageTargetRef;
    List<String> urls;
    CollageStrategy collageStrategy;

    ImageCache imageCache;


    public ThreadedCollageLoader(ThreadPoolExecutor mExecutor, Handler mainThreadHandler, ImageCache imageCache) {
        this.mExecutor = mExecutor;
        this.mainThreadHandler = mainThreadHandler;
        this.imageCache = imageCache;
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, new PowerOfTwoCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        this.imageTargetRef = new WeakReference<ImageTarget>(imageTarget);
        this.urls = urls;
        this.collageStrategy = collageStrategy;
        resultAggregationExecutor.execute(new MakeCollageTask());
    }

    private class MakeCollageTask implements Runnable {
        @Override
        public void run() {
            String cacheKey = TextUtils.join("", urls);
            Bitmap cachedCollage = imageCache.get(cacheKey);
            if (cachedCollage == null) {
                List<Future<Bitmap>> futures = new ArrayList<>();
                for (String url : urls) {
                    futures.add(mExecutor.submit(new ImageDownloadTask(url)));
                }
                List<Bitmap> bitmapList = new ArrayList<>();
                try {
                    for (Future<Bitmap> future : futures) {
                        bitmapList.add(future.get());
                    }
                    Collections.shuffle(bitmapList);
                    Bitmap collageBitmap = collageStrategy.create(bitmapList);
                    imageCache.put(cacheKey, collageBitmap);
                    postResult(collageBitmap);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            else {
                postResult(cachedCollage);
            }
        }

        private void postResult(Bitmap bitmap) {
            mainThreadHandler.post(() -> {
                ImageTarget imageTarget = imageTargetRef.get();
                if (imageTarget != null) {
                    imageTarget.onLoadBitmap(bitmap, ThreadedCollageLoader.this);
                }
            });
        }
    }

}
