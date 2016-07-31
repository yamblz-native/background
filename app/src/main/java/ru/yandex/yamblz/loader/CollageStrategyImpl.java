package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.util.List;

public class CollageStrategyImpl implements CollageStrategy {
    private static final int NUMBER_OF_COLUMNS = 2;
    private static final int IMAGE_SIZE = 300; // Размер изображения в самом коллаже. Все входящие картинки обрезаются (из левого угла) до квадрата
    private static final int COLLAGE_WIDTH = IMAGE_SIZE * NUMBER_OF_COLUMNS;

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        int numberOfRows;
        if (bitmaps.size() >= 3) {
            numberOfRows = bitmaps.size() / NUMBER_OF_COLUMNS; // 10 / 3 = 3, одной картинки не будет, задо красивенько
        } else {
            numberOfRows = 1;
        }

        Bitmap collage = Bitmap.createBitmap(COLLAGE_WIDTH, IMAGE_SIZE * numberOfRows, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(collage);
        int top = 0, left = 0;
        for (Bitmap currentBitmap : bitmaps) {
            if (left >= COLLAGE_WIDTH) {
                top += IMAGE_SIZE;
                left = 0;
            }
            int size;

            if (currentBitmap.getWidth() < currentBitmap.getHeight()) { // Обрезаем картинку
                size = currentBitmap.getWidth();
            } else {
                size = currentBitmap.getHeight();
            }
            canvas.drawBitmap(currentBitmap, new Rect(0, 0, size, size), new Rect(left, top, left + IMAGE_SIZE, top + IMAGE_SIZE), null);
            currentBitmap.recycle();
            left += IMAGE_SIZE;
            if (IMAGE_SIZE * numberOfRows < top) {
                break;
            }
        }
        Log.d("makeCollage", "BitmapsListHash=" + bitmaps.hashCode());
        canvas.save();

        return collage;
    }
}
