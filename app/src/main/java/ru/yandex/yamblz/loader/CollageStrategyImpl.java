package ru.yandex.yamblz.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.util.List;

/**
 * Created by dan on 29.07.16.
 */
public class CollageStrategyImpl implements CollageStrategy {
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        if (bitmaps.size() >= 4) {
            Log.i("String", "Im here beach with 4 pics");
            Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
            canvas.drawBitmap(bitmaps.get(1), 300, 0, null);
            canvas.drawBitmap(bitmaps.get(2), 0, 300, null);
            canvas.drawBitmap(bitmaps.get(3), 300, 300, null);
            canvas.save();
            return bitmap;
        }
        else {
            Log.i("String", "Im here beach with 1 pic");
            Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
            canvas.save();
            return bitmaps.get(0);
        }

    }


}
