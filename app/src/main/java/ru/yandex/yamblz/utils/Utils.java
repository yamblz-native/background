package ru.yandex.yamblz.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class Utils {
    private Utils() {

    }

    @Nullable
    public static Bitmap loadBitmapFromUrl(String urlString) {
        InputStream in = null;
        try {
            in = new java.net.URL(urlString).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(in);
        }
        return null;
    }


    private static void closeConnection(InputStream in) {
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
