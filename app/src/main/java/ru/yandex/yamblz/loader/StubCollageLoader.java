package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StubCollageLoader implements CollageLoader {
    private HashMap<String, LoadBitmapsCallback> callbacksHashMap;

    public StubCollageLoader() {
        callbacksHashMap = new HashMap<>();
    }

    //загрузить колаж из списка и поставить его в ImageView
    //взять defaultCollageStrategy
    @Override
    public void loadCollage(List<String> urls, ImageView imageView, String key) {
        loadCollage(urls, new DefaultImageTarget(imageView), key);
    }

    //загрузить
    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, String key) {
        loadCollage(urls, imageTarget,new DefaultCollageStrategy(), key);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy, String key) {
        loadCollage(urls, new DefaultImageTarget(imageView),collageStrategy, key);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy, String key) {
        cancel(key);
        LoadBitmapsCallback loadBitmapsCallback = new LoadBitmapsCallback(value -> {
            imageTarget.onLoadBitmap(collageStrategy.create(value));
        });
        callbacksHashMap.put(key, loadBitmapsCallback);
        loadBitmapsCallback.load(urls);

    }

    @Override
    public void cancel(String key) {
        if (callbacksHashMap.containsKey(key)) {
            LoadBitmapsCallback loadBitmapsCallback = callbacksHashMap.get(key);
            loadBitmapsCallback.cancel();
        }
    }

    @Override
    public void cancelAll() {
        for(LoadBitmapsCallback loadBitmapsCallback:callbacksHashMap.values()){
            loadBitmapsCallback.cancel();
        }
        callbacksHashMap.clear();
    }

    private class LoadBitmapsCallback {
        List<LoadBitmapTask> asyncTasks;
        List<Bitmap> bitmaps;
        private int tasksDone;
        private Consumer<List<Bitmap>> consumer;

        LoadBitmapsCallback(Consumer<List<Bitmap>> consumer) {
            this.consumer = consumer;
            asyncTasks = new ArrayList<>();
            bitmaps = new ArrayList<>();
        }

        void load(List<String> urls) {
            tasksDone = 0;
            bitmaps.clear();
            asyncTasks.clear();
            for (String url : urls) {
                LoadBitmapTask loadBitmapTask = new LoadBitmapTask();
                asyncTasks.add(loadBitmapTask);
                loadBitmapTask.execute(url);
            }
        }

        void loadComplete(List<Bitmap> bitmaps) {
            consumer.call(bitmaps);
        }

        void taskDone(Bitmap bitmap) {
            bitmaps.add(bitmap);
            tasksDone++;
            if (tasksDone == asyncTasks.size()) {
                asyncTasks.clear();
                loadComplete(bitmaps);
                bitmaps.clear();
                tasksDone = 0;
            }
        }

        public void cancel() {
            for (LoadBitmapTask loadBitmapTask : asyncTasks) {
                loadBitmapTask.cancel(true);
            }
        }

        private class LoadBitmapTask extends AsyncTask<String, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap b = getBitmapFromURL(params[0]);
                return b;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                taskDone(bitmap);
            }

            private Bitmap getBitmapFromURL(String src) {
                try {
                    System.out.print(Thread.currentThread());
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    // Log exception
                    return null;
                }
            }
        }
    }

    private interface Consumer<T> {
        void call(T value);
    }


}
