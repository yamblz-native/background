package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

import ru.yandex.yamblz.loader.interfaces.CollageStrategy;

/**
 * Created by platon on 26.07.2016.
 */
public class CollageStrategyImpl implements CollageStrategy
{
    @Override
    public Bitmap create(List<Bitmap> bitmaps)
    {
        return combine(bitmaps);
    }

    private Bitmap combine(List<Bitmap> bitmaps)
    {
        if (bitmaps.size() != 4) return bitmaps.get(0);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        Bitmap first = bitmaps.get(0);
        Bitmap second = bitmaps.get(1);
        Bitmap third = bitmaps.get(2);
        Bitmap fourth= bitmaps.get(3);

        int centerX = first.getWidth();
        int centerY = first.getHeight();

        int width = centerX * 2;
        int height = centerY * 2;

        Bitmap collage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(collage);

        canvas.drawBitmap(first, 0f, 0f, paint);
        canvas.drawBitmap(second, centerX, 0f, paint);
        canvas.drawBitmap(third, 0f, centerY, paint);
        canvas.drawBitmap(fourth, centerX, centerY, paint);

        return collage;
    }
}
