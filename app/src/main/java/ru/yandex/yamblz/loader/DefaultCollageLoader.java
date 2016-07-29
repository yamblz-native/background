package ru.yandex.yamblz.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class DefaultCollageLoader implements CollageLoader {
    private static int imageDensity;

    static {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        imageDensity = (int) (metrics.densityDpi / metrics.density);
    }

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
            strategy = new DefaultCollageStrategy();
        }

        WeakReference<ImageView> refImageView = new WeakReference<>(iv);
        WeakReference<ImageTarget> refImageTarget = new WeakReference<>(it);

        new AsyncCollageLoader(ids, resources, refImageView, refImageTarget, strategy).execute();
    }


    private static class AsyncCollageLoader extends AsyncTask<Void, Void, Bitmap> {
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
            imageView.setImageResource(android.R.color.white);
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inTargetDensity = imageDensity;
            return BitmapFactory.decodeResource(resources, ids[0], options);
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
