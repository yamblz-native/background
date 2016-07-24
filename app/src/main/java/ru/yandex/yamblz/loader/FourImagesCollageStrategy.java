package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.List;

/**
 * Created by Aleksandra on 24/07/16.
 */
public class FourImagesCollageStrategy implements CollageStrategy {
    public static final String DEBUG_TAG = FourImagesCollageStrategy.class.getName();

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        Log.d(DEBUG_TAG, "in create collage");

        Bitmap result;

        if (bitmaps.size() >= 4) {
            result = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
            canvas.drawBitmap(bitmaps.get(1), 0, 300, null);
            canvas.drawBitmap(bitmaps.get(2), 300, 0, null);
            canvas.drawBitmap(bitmaps.get(3), 300, 300, null);
            canvas.save();
        } else {
            result = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
            canvas.save();
        }

        return result;
    }

}
