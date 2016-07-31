package ru.yandex.yamblz.loader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.DrawableContainer;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.yandex.yamblz.App;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.ui.activities.BaseActivity;
import ru.yandex.yamblz.ui.activities.MainActivity;
import ru.yandex.yamblz.ui.fragments.ArtistAdapter;
import ru.yandex.yamblz.ui.fragments.ArtistListFragment;
import ru.yandex.yamblz.ui.fragments.BaseFragment;

public class StubCollageLoader implements CollageLoader {

    private static final int THREADS = 4;

    public ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {


        UUID uuid = UUID.randomUUID();
        Object oldCreateImages = imageView.getTag();


        if (oldCreateImages != null) {
            CriticalSectionsManager.getHandler().removeLowPriorityTask((Task) oldCreateImages);
        }

        Task setter = () -> {
            threadPool.execute(() -> {
                loadCollage(urls, imageView, collageStrategy, uuid);
            });
        };
        imageView.setTag(setter);
        imageView.setTag(R.id.recycler_item_image, uuid);
        CriticalSectionsManager.getHandler().postLowPriorityTask(setter);
    }

    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy, UUID uuid) {
        Runnable rr = () -> {
            List<Bitmap> bitmapList = new ArrayList<>();
            int len = 1;
            if (urls.size() >= 4) {
                len = 4;
            }
            for (int i = 0; i < len; i++) {
                bitmapList.add(getBitmapFromUrl(urls.get(i)));
            }
            Bitmap bitmap = collageStrategy.create(bitmapList);
            if (imageView != null && imageView.getTag(R.id.recycler_item_image).equals(uuid)) {
                imageView.post(() -> imageView.setImageBitmap(bitmap));
            }
        };

        threadPool.submit(rr);

    }



    public Bitmap getBitmapFromUrl(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {

    }



}
