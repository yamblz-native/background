package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.List;
import java.util.Random;

/**
 * Created by kostya on 26.07.16.
 */

public class CollageStrategyImpl implements CollageStrategy {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        Log.d("Bitmap create, size = ", Integer.toString(bitmaps.size()));
        if (bitmaps.isEmpty()) {
            return null;
        }
        else if (bitmaps.size() < 4) {
            Bitmap bitmap = Bitmap.createBitmap(WIDTH/2, HEIGHT/2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmaps.get(0), 0f, 0f, null);
            canvas.save();
            return bitmap;
        }
        else {
            Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmaps.get(0), 0f, 0f, null);
            canvas.drawBitmap(bitmaps.get(1), WIDTH/2, 0, null);
            canvas.drawBitmap(bitmaps.get(2), 0f, HEIGHT/2, null);
            canvas.drawBitmap(bitmaps.get(3), WIDTH/2, HEIGHT/2, null);
            canvas.save();
            return bitmap;
        }
    }
}
