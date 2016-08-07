package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Litun on 07.08.2016.
 */
public class SimpleCollageStrategy implements CollageStrategy {
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        if (bitmaps == null || bitmaps.size() == 0)
            return null;

        int width = 400, height = 400;
        Bitmap collageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas collageCanvas = new Canvas(collageBitmap);

        switch (bitmaps.size()) {
            case 1:
                return bitmaps.get(0);
            case 2:
                collageCanvas.drawBitmap(bitmaps.get(0), 0f, 0f, null);
                collageCanvas.drawBitmap(bitmaps.get(1), width / 2, 0f, null);
                break;
            case 3:
                collageCanvas.drawBitmap(bitmaps.get(0), 0f, 0f, null);
                collageCanvas.drawBitmap(bitmaps.get(1), width / 2, 0f, null);
                collageCanvas.drawBitmap(bitmaps.get(2), 0, height / 2, null);
                break;
            default:
            case 4:
                collageCanvas.drawBitmap(bitmaps.get(0), 0f, 0f, null);
                collageCanvas.drawBitmap(bitmaps.get(1), 0f, height / 2, null);
                collageCanvas.drawBitmap(bitmaps.get(2), width / 2, 0f, null);
                collageCanvas.drawBitmap(bitmaps.get(3), width / 2, height / 2, null);
                break;
        }

        return collageBitmap;

    }
}
