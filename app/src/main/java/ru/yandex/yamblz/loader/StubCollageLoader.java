package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class StubCollageLoader implements CollageLoader {
    public static final String DEBUG_TAG = StubCollageLoader.class.getName();

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {

    }

    @Override
    public Subscription loadCollage(List<String> urls, WeakReference<ImageView> imageView,
                                    CollageStrategy collageStrategy) {
        LinkedList<Bitmap> bitmaps = new LinkedList<>();

        return Observable.from(urls)
                .observeOn(Schedulers.io())
                .map(this::loadBitmapFromUrl)
                .collect(() -> bitmaps, LinkedList::add)
                .subscribe(new Subscriber<LinkedList<Bitmap>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(DEBUG_TAG, "Bitmap size + " + bitmaps.size());
                        Bitmap collage = collageStrategy.create(bitmaps);
                        ((AppCompatActivity) imageView.get().getContext()).runOnUiThread(() -> {
                            imageView.get().setImageBitmap(collage);
                            imageView.get().invalidate();
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(LinkedList<Bitmap> bitmaps) {

                    }
                });
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {

    }

    protected Bitmap loadBitmapFromUrl(String stringUrl) {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
