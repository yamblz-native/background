package ru.yandex.yamblz.loader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.yandex.yamblz.App;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.activities.BaseActivity;
import ru.yandex.yamblz.ui.activities.MainActivity;
import ru.yandex.yamblz.ui.fragments.ArtistAdapter;
import ru.yandex.yamblz.ui.fragments.ArtistListFragment;
import ru.yandex.yamblz.ui.fragments.BaseFragment;

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
            ///here
        new Thread() {
            @Override
            public void run() {
                List<Bitmap> bitmapList = new ArrayList<>();
                for (String url: urls) {
                    bitmapList.add(getBitmapFromUrl(url));
                }
                Bitmap bitmap = collageStrategy.create(bitmapList);




                if (imageView != null) {
                    imageView.setImageBitmap(bitmap); ///run on ui thread!
                }
            }
        }.start();

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
