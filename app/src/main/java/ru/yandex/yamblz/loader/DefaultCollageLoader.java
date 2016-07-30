package ru.yandex.yamblz.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
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
    private Resources resources;

    public DefaultCollageLoader(Resources resources) {
        this.resources = resources;
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

        new AsyncCollageLoader(ids, resources, refImageView, refImageTarget, strategy).execute();
    }


    private static class AsyncCollageLoader extends AsyncTask<Void, Void, Bitmap> {
        private static ExecutorService executorService = Executors.newCachedThreadPool();

        private int[] ids;
        private Resources resources;
        private WeakReference<ImageView> refImageView;
        private WeakReference<ImageTarget> refImageTarget;
        private CollageStrategy collageStrategy;

        AsyncCollageLoader(int[] ids, Resources r, WeakReference<ImageView> iv, WeakReference<ImageTarget> it, CollageStrategy s) {
            this.ids = ids;
            this.resources = r;
            this.refImageView = iv;
            this.refImageTarget = it;
            this.collageStrategy = s;
        }


        @Override
        protected void onPreExecute() {
            ImageView imageView = refImageView.get();
            if (null == imageView) return;
            imageView.setImageBitmap(null);
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            List<Future<Bitmap>> futures = new ArrayList<>(ids.length);
            for (int id : ids) {
                futures.add(executorService.submit(() -> {
                    setThreadPriority(THREAD_PRIORITY_BACKGROUND);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inTargetDensity = DisplayMetrics.DENSITY_MEDIUM;
                    return BitmapFactory.decodeResource(resources, id, options);
                }));
            }

            List<Bitmap> bitmaps = new ArrayList<>(futures.size());

            for (Future<Bitmap> future : futures) {
                try {
                    bitmaps.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            return collageStrategy.create(bitmaps);
        }


        @Override
        protected void onPostExecute(Bitmap image) {
            ImageView imageView = refImageView.get();
            if (imageView == null) {
                return;
            }

            imageView.setAlpha(0f);
            imageView.setImageBitmap(image);
            imageView.animate().alpha(1).setDuration(500).start();
        }
    }
}
