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
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class StubCollageLoader implements CollageLoader {
    private Map<String, LoadBitmapsCallback> callbacksMap;
    private Map<String, CreateCollageTask> collageTaskMap;
    private Executor collageExecutor;
    StubCollageLoader() {
        callbacksMap = new HashMap<>();
        collageTaskMap = new HashMap<>();
        collageExecutor=new ScheduledThreadPoolExecutor(5);
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
            callbacksMap.remove(key);
            CreateCollageTask execute = new CreateCollageTask(value, imageTarget, collageStrategy){
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    collageTaskMap.remove(key);
                }
            };
            execute.executeOnExecutor(collageExecutor);
            collageTaskMap.put(key,execute);
        });
        callbacksMap.put(key, loadBitmapsCallback);
        loadBitmapsCallback.load(urls);

    }

    @Override
    public void cancel(String key) {
        if (callbacksMap.containsKey(key)) {
            LoadBitmapsCallback loadBitmapsCallback = callbacksMap.get(key);
            loadBitmapsCallback.cancel();
        }
        if(collageTaskMap.containsKey(key)){
            CreateCollageTask createCollageTask = collageTaskMap.get(key);
            createCollageTask.cancel(true);
        }
    }

    @Override
    public void cancelAll() {
        for(LoadBitmapsCallback loadBitmapsCallback: callbacksMap.values()){
            loadBitmapsCallback.cancel();
        }
        for(CreateCollageTask createCollageTask:collageTaskMap.values()){
            createCollageTask.cancel(true);
        }
        callbacksMap.clear();
        collageTaskMap.clear();
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
                tasksDone = 0;
            }
        }

        void cancel() {
            for (LoadBitmapTask loadBitmapTask : asyncTasks) {
                loadBitmapTask.cancel(true);
            }
        }

        private class LoadBitmapTask extends AsyncTask<String, Void, Bitmap> {
            private InputStream input;
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
                    input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    // Log exception
                    return null;
                }
                finally {
                    if(input!=null) try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onCancelled() {
                if(input!=null) try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.onCancelled();
            }
        }
    }

    private interface Consumer<T> {
        void call(T value);
    }

    private static class CreateCollageTask extends AsyncTask<Void,Void,Bitmap>{
        private List<Bitmap> bitmaps;
        private ImageTarget imageTarget;
        private CollageStrategy collageStrategy;

        public CreateCollageTask(List<Bitmap> bitmaps,ImageTarget imageTarget,CollageStrategy collageStrategy) {
            this.bitmaps = bitmaps;
            this.imageTarget = imageTarget;
            this.collageStrategy = collageStrategy;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return collageStrategy.create(bitmaps);
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageTarget.onLoadBitmap(bitmap);
        }
    }


}
