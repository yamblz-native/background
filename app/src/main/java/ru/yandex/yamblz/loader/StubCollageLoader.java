package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;

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
        Log.d("loadCollage", "download");
        List<Bitmap> bitmapList = new ArrayList<>();
        for (String url :
                urls) {
            bitmapList.add(getBitmapFromURL(url));
        }
        Bitmap bitmap = collageStrategy.create(bitmapList);
        if (imageView != null) {
            imageView.post(() -> {
                imageView.setImageBitmap(null);
                imageView.setImageBitmap(bitmap);
                imageView.invalidate();
            });

        }

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {

    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            //throw new IOException();
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            int[] colors = {Color.BLACK, Color.BLUE, Color.GREEN, Color.RED};
            Random random = new Random();
            int ind = Math.abs(random.nextInt()) % colors.length;
            Paint paint = new Paint();
            paint.setColor(colors[ind]);
            canvas.drawCircle(150, 150, 150, paint);
            canvas.save();
            return bitmap;
        }
    }
}
