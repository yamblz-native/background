package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;
import java.util.Random;

import ru.yandex.yamblz.utils.Utils;

/**
 * Created by grin3s on 06.08.16.
 */

public class DumbCollageStrategy implements CollageStrategy {
    private final static Random rnd = new Random();
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        Bitmap firstImage = bitmaps.get(0);
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        for (Bitmap bitmap : bitmaps) {
            canvas.drawBitmap(bitmap, Utils.getRandomCoordinate(0, width), Utils.getRandomCoordinate(0, height), null);
        }
        return result;
    }
}
