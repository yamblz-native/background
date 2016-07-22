package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlImageDownloader implements ImageDownloader {
    @Override
    public Bitmap downloadBitmap(String url) {
        try {
            return BitmapFactory.decodeStream((new URL(url)).openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
