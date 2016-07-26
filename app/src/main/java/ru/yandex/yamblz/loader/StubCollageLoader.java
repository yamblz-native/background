package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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
    public void loadCollage(List<String> urls, WeakReference<ImageView> imageView,
                            CollageStrategy collageStrategy) {
        Bitmap collage = getCollage(urls, collageStrategy);

        CriticalSectionsManager.getHandler().postLowPriorityTask(new ImageSetter(imageView, collage));
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

    public class ImageSetter implements Task {

        private WeakReference<ImageView> imageView;
        private Bitmap collage;

        public ImageSetter(WeakReference<ImageView> imageView, Bitmap collage) {
            this.imageView = imageView;
            this.collage = collage;
        }

        @Override
        public void run() {
            imageView.get().setImageBitmap(collage);
            imageView.get().invalidate();
            imageView.get().requestLayout();
        }
    }
}
