package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import ru.yandex.yamblz.network.ImageDownloadTask;

/**
 * Created by grin3s on 06.08.16.
 */

public class ThreadedCollageLoader extends CollageLoader {

    ThreadPoolExecutor mExecutor;

    Handler mainThreadHandler;

    WeakReference<ImageTarget> imageTargetRef;
    List<String> urls;
    CollageStrategy collageStrategy;


    public ThreadedCollageLoader(ThreadPoolExecutor mExecutor, Handler mainThreadHandler) {
        this.mExecutor = mExecutor;
        this.mainThreadHandler = mainThreadHandler;
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, new DumbCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        Log.d("POST", "POST4");
        this.imageTargetRef = new WeakReference<ImageTarget>(imageTarget);
        this.urls = urls;
        this.collageStrategy = collageStrategy;
        mExecutor.execute(new MakeCollageTask());
    }

    private class MakeCollageTask implements Runnable {
        @Override
        public void run() {
            Log.d("POST", "POST3");
            List<Future<Bitmap>> futures = new ArrayList<>();
            for (String url : urls) {
                futures.add(mExecutor.submit(new ImageDownloadTask(url)));
                break;
            }
            List<Bitmap> bitmapList = new ArrayList<>();
            try {
                for (Future<Bitmap> future : futures) {
                    bitmapList.add(future.get());
                }
                Log.d("POST", "POST2");
                Bitmap collageBitmap = collageStrategy.create(bitmapList);
                mainThreadHandler.post(() -> {
                    ImageTarget imageTarget = imageTargetRef.get();
                    if (imageTarget != null) {
                        Log.d("POST", "POST1");
                        // TODO: put this into ui thread queue
                        imageTarget.onLoadBitmap(collageBitmap, ThreadedCollageLoader.this);
                    }
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
