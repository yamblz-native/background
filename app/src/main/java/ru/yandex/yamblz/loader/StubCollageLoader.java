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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;

public class StubCollageLoader implements CollageLoader {

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        imageView.setImageDrawable(null);

        Object old_setter = imageView.getTag();
        if (old_setter != null) {
            Log.d("onBindViewHlder", "removeLowPriorityTask");
            CriticalSectionsManager.getHandler().removeLowPriorityTask((Task) old_setter);
        }

        UUID uuid = UUID.randomUUID();

        Task setter = () -> {
            pool.execute(() -> loadCollageImpl(
                    urls,
                    imageView,
                    collageStrategy,
                    uuid
            ));
        };

        imageView.setTag(setter);
        imageView.setTag(R.id.img_in_list, uuid);
        CriticalSectionsManager.getHandler().postLowPriorityTask(setter);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {

    }

    private void loadCollageImpl(List<String> urls, ImageView imageView,
                                 CollageStrategy collageStrategy, UUID uuid) {
        Log.d("loadCollageImpl", "download");
        List<Bitmap> bitmapList = new ArrayList<>();
        int size = Math.min(collageStrategy.numOfImage(), urls.size());
        for (int i = 0; i < size; i++) {
            bitmapList.add(getBitmapFromURL(urls.get(i)));
        }
        Bitmap bitmap = collageStrategy.create(bitmapList);
        if (imageView != null && imageView.getTag(R.id.img_in_list).equals(uuid)) {
            imageView.post(() -> {
                imageView.setImageDrawable(null);
                imageView.setImageBitmap(bitmap);
            });
        }
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
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
