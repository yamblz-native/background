package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StubCollageLoader implements CollageLoader {
    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        Bitmap collage = getCollage(urls, collageStrategy);

        ((AppCompatActivity) imageView.getContext()).runOnUiThread(() -> {
            imageView.setImageBitmap(collage);
            imageView.invalidate();
            imageView.requestLayout();
        });
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {

    }

    protected Bitmap getCollage(List<String> stringUrls, CollageStrategy strategy) {
        List<Bitmap> bitmaps = new LinkedList<>();
        for (String url : stringUrls) {
            try {
                bitmaps.add(loadBitmapFromUrl(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strategy.create(bitmaps);
    }

    protected Bitmap loadBitmapFromUrl(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        return BitmapFactory.decodeStream(inputStream);
    }
}
