package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

public class CollageStrategyImpl implements CollageStrategy {
    private static final int NUMBER_OF_COLUMNS = 3;
    private static final int IMAGE_SIZE = 300; // Размер изображения в самом коллаже. Все входящие картинки обрезаются (из левого угла) до квадрата
    private static final int COLLAGE_WIDTH = IMAGE_SIZE * NUMBER_OF_COLUMNS;

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        // Битмап ARGB_8888 размером 1500x1500 занимает 5 мегабайт, а вмещает в себя 5x5 = 25 изображений
        // Памяти должно хватить
        // Столбцов будет 5 штук, а строк - сколько получится

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
            left += IMAGE_SIZE;
            currentBitmap.recycle(); // И память не забудем почистить. Вообще нехорошо вышло, bitmap`ы лучше грузить из Picasso, которая всё кеширует
        }
        canvas.save();

        return collage;
    }
}
