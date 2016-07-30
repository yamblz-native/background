package ru.yandex.yamblz.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.setThreadPriority;

public class DefaultCollageLoader implements CollageLoader {
    private static final String TAG = DefaultCollageLoader.class.getSimpleName();
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private Resources resources;
    private LruCache<Integer, Bitmap> bitmapCache;

    public DefaultCollageLoader(Resources resources) {
        this.resources = resources;

        int maxCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024 / 2);
        bitmapCache = new LruCache<Integer, Bitmap>(maxCacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    @Override
    public void loadCollage(int[] ids, ImageView imageView) {
        loadCollage(ids, imageView, null, null);
    }


    @Override
    public void loadCollage(int[] ids, ImageTarget imageTarget) {
        loadCollage(ids, null, imageTarget, null);

    }


    @Override
    public void loadCollage(int[] ids, ImageView imageView, CollageStrategy collageStrategy) {
        loadCollage(ids, imageView, null, collageStrategy);

    }


    @Override
    public void loadCollage(int[] ids, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        loadCollage(ids, null, imageTarget, collageStrategy);
    }


    private void loadCollage(int[] ids, ImageView iv, ImageTarget it, CollageStrategy strategy) {
        if (strategy == null) {
            strategy = new SquareCollageStrategy();
        }

        WeakReference<ImageView> refImageView = new WeakReference<>(iv);
        WeakReference<ImageTarget> refImageTarget = new WeakReference<>(it);

        new AsyncCollageLoader(ids, refImageView, refImageTarget, strategy).execute();
    }


    private class AsyncCollageLoader extends AsyncTask<Void, Void, Bitmap> {
        private int[] ids;
        private WeakReference<ImageView> refImageView;
        private WeakReference<ImageTarget> refImageTarget;
        private CollageStrategy collageStrategy;

        AsyncCollageLoader(int[] ids, WeakReference<ImageView> iv, WeakReference<ImageTarget> it, CollageStrategy strategy) {
            this.ids = ids;
            this.refImageView = iv;
            this.refImageTarget = it;
            this.collageStrategy = strategy;
        }


        @Override
        protected void onPreExecute() {
            ImageView imageView = refImageView.get();
            if (null == imageView) {
                cancel(false);
            } else {
                imageView.setImageBitmap(null);
            }
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            List<Future<Bitmap>> futures = new ArrayList<>(ids.length);
            List<Bitmap> bitmaps = new ArrayList<>(ids.length);

            for (int id : ids) {
                if (isCancelled()) {
                    return null;
                }

                Bitmap bitmap = bitmapCache.get(id);
                if (bitmap == null) {
                    futures.add(executorService.submit(() -> {
                        setThreadPriority(THREAD_PRIORITY_BACKGROUND);
                        Bitmap bmp = BitmapFactory.decodeResource(resources, id);
                        bitmapCache.put(id, bmp);
                        return bmp;
                    }));
                } else {
                    bitmaps.add(bitmap);
                }
            }

            for (Future<Bitmap> future : futures) {
                try {
                    bitmaps.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }

            return collageStrategy.create(bitmaps);
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = refImageView.get();
            if (imageView != null) {
                imageView.setAlpha(0f);
                imageView.setImageBitmap(bitmap);
                imageView.animate().alpha(1).setDuration(500).start();
            }

            ImageTarget imageTarget = refImageTarget.get();
            if (imageTarget != null) {
                imageTarget.onLoadBitmap(bitmap);
            }
        }
    }
}
