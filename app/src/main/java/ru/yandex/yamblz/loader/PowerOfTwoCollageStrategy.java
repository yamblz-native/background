package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Created by grin3s on 07.08.16.
 */

public class PowerOfTwoCollageStrategy implements CollageStrategy {
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        int nRows = (int) Math.floor(Math.sqrt(bitmaps.size()));
        Bitmap firstImage = bitmaps.get(0);
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, firstImage.getConfig());
        Canvas canvas = new Canvas(result);

        int delta = (int) Math.floor(width / nRows);
        int curBitmap = 0;
        int curLeft = 0;
        int curTop = 0;
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nRows; j++) {
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmaps.get(curBitmap), delta, delta, false), curLeft, curTop, null);
                curLeft += delta;
                curBitmap += 1;
            }
            curTop += delta;
            curLeft = 0;
        }
        return result;
    }
}
