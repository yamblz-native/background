package ru.yandex.yamblz.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ru.yandex.yamblz.loader.CollageStrategy;
import ru.yandex.yamblz.loader.ImageTarget;

public class CollageCreateTask implements Task {

    private WeakReference<ImageView> wrImageView;
    private List<String> urls;
    private CollageStrategy collageStrategy;
    private ImageTarget imageTarget;
    private ImagesLoadTask imagesLoadTask;
    private CollageCreateTaskFinishListener listener;

    public CollageCreateTask(List<String> urls, ImageView imageView, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        this.wrImageView = new WeakReference<>(imageView);
        this.imageTarget = imageTarget;
        this.urls = urls;
        this.collageStrategy = collageStrategy;
    }

    @Override
    public void run() {
        start();
    }

    public void start() {
        imagesLoadTask = new ImagesLoadTask(urls, wrImageView, imageTarget, collageStrategy, listener, this);
        imagesLoadTask.execute();
    }

    public void stop() {
        if (imagesLoadTask != null){
            imagesLoadTask.cancel(false);
        }
    }

    public void setListener(CollageCreateTaskFinishListener listener) {
        this.listener = listener;
    }

    public ImageView getImageView() {
        return wrImageView.get();
    }

    public ImageTarget getImageTarget() {
        return imageTarget;
    }

    private static class ImagesLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private List<String> urls;
        private WeakReference<ImageView> wrImageView;
        private ImageTarget imageTarget;
        private CollageStrategy collageStrategy;
        private CollageCreateTaskFinishListener listener;
        private CollageCreateTask task;

        public ImagesLoadTask(List<String> urls,
                              WeakReference<ImageView> wrImageView,
                              ImageTarget imageTarget,
                              CollageStrategy collageStrategy,
                              CollageCreateTaskFinishListener listener,
                              CollageCreateTask task) {

            this.urls = urls;
            this.wrImageView = wrImageView;
            this.imageTarget = imageTarget;
            this.collageStrategy = collageStrategy;
            this.listener = listener;
            this.task = task;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            CountDownLatch countDownLatch = new CountDownLatch(urls.size());

            for (int i = 0; i < urls.size(); i++) {
                new ImageLoadThread(urls.get(i), bitmaps, countDownLatch).start();
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return collageStrategy.create(bitmaps);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (wrImageView != null) {
                wrImageView.get().setImageBitmap(bitmap);
            }
            if (imageTarget != null) {
                imageTarget.onLoadBitmap(bitmap);
            }
            if (listener != null) {
                listener.onTaskFinished(task);
            }
        }

    }

    private static class ImageLoadThread extends Thread {

        private String url;
        private final ArrayList<Bitmap> bitmaps;
        private CountDownLatch countDownLatch;

        public ImageLoadThread(String url, ArrayList<Bitmap> bitmaps, CountDownLatch countDownLatch) {
            this.url = url;
            this.bitmaps = bitmaps;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            Bitmap bitmap = getBitmap(url);
            synchronized (bitmaps) {
                bitmaps.add(bitmap);
            }
            countDownLatch.countDown();
        }

        private static Bitmap getBitmap(String url) {
            InputStream inputStream = null;
            Bitmap bitmap = null;
            try {
                inputStream = new URL(url).openConnection().getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при загрузке изображения.");
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bitmap;
        }

    }

}
