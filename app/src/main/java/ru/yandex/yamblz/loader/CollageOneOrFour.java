package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.List;

/**
 * Created by vorona on 01.08.16.
 */

public class CollageOneOrFour implements CollageStrategy {
    private int imgCount;

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        if (bitmaps == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        if (bitmaps.size() > 0 && bitmaps.size() < 4) {
            imgCount = 1;
            Canvas canvas = new Canvas(bitmap);
            Bitmap b = bitmaps.get(0);
            canvas.drawBitmap(b, 0, 0, null);
            return b;

        }
        else {
            imgCount = 4;
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
            canvas.drawBitmap(bitmaps.get(1), 0, 200, null);
            canvas.drawBitmap(bitmaps.get(2), 200, 0, null);
            canvas.drawBitmap(bitmaps.get(3), 200, 200, null);
        }
        return bitmap;
    }

    public int getImgCount() {
        return imgCount;
    }
}
