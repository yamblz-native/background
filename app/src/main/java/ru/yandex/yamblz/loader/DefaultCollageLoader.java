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
    private static final long MEMORY_MAX = Runtime.getRuntime().maxMemory();
    private static final float MEMORY_USE_THRESHOLD = 0.9f;

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private Resources resources;
    private LruCache<Integer, Bitmap> bitmapCache;

    public DefaultCollageLoader(Resources resources) {
        this.resources = resources;

        int maxCacheSize = (int) (MEMORY_MAX / 1024 / 2);
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


    /**
     * Asynchronously loads (or gets from cache) all the images whose ids are specified.
     * <p>
     * There is a drawback in current architecture: since only a {@link CollageStrategy}
     * knows all the details about collage management, we can't load only a useful
     * part of images, nor we can load a scaled down images (the latter is also impossible
     * since an {@link ImageTarget} is unable to tell the desired width and height).
     */
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
            if (imageView == null) {
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

                        Bitmap bmp = loadBitmap(id);
                        if (bmp == null) {
                            return null;
                        }

                        bitmapCache.put(id, bmp);
                        return bmp;
                    }));
                } else {
                    bitmaps.add(bitmap);
                }
            }

            for (Future<Bitmap> future : futures) {
                try {
                    Bitmap bitmap = future.get();
                    if (bitmap != null) {
                        bitmaps.add(bitmap);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }

            return collageStrategy.create(bitmaps);
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                return;
            }

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


        private Bitmap loadBitmap(int resId) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(resources, resId, options);
            long bitmapSize = options.outWidth * options.outHeight * 4;

            long memoryRequired = Runtime.getRuntime().totalMemory() + bitmapSize;
            if (memoryRequired > MEMORY_MAX * MEMORY_USE_THRESHOLD) {
                Log.d(TAG, "Could not load a new image because of exceeding memory consumption limit");
                return null;
            }

            return BitmapFactory.decodeResource(resources, resId);
        }
    }
}
