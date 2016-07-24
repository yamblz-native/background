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
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class StubCollageLoader implements CollageLoader {

    //загрузить колаж из списка и поставить его в ImageView
    //взять defaultCollageStrategy
    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, bitmap -> {
            imageView.setImageBitmap(bitmap);
        }, new DefaultCollageStrategy());
    }

    //загрузить
    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        List<Bitmap> bitmaps=new ArrayList<>();
        Observable.from(urls).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .flatMap(new LoadFunc()).doOnCompleted(() -> imageTarget.onLoadBitmap(collageStrategy.create(bitmaps)))
                .subscribe(bitmap -> {
                    bitmaps.add(bitmap);
                });
    }


    private static class LoadFunc implements Func1<String, Observable<Bitmap>> {
        @Override
        public Observable<Bitmap> call(String s) {
            Observable<Bitmap> observable=  Observable.create(subscriber -> {
                new AsyncTask<String,Void,Bitmap>(){
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        Bitmap b=getBitmapFromURL(params[0]);
                        return b;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        subscriber.onNext(bitmap);
                        subscriber.onCompleted();
                    }
                }.execute(s);

            });
            return observable;
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
