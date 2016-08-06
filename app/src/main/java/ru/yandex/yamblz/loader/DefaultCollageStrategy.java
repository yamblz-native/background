package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

public class DefaultCollageStrategy implements CollageStrategy {

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        Bitmap collage;

        if (bitmaps.size() < 4) {
            return bitmaps.get(0);
        } else {
            int width = bitmaps.get(0).getWidth()*2;
            int height = bitmaps.get(0).getHeight()*2;
            collage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(collage);
            canvas.drawBitmap(bitmaps.get(0), 0, 0, new Paint());
            canvas.drawBitmap(bitmaps.get(1), bitmaps.get(0).getWidth(), 0, new Paint());
            canvas.drawBitmap(bitmaps.get(2), 0, bitmaps.get(0).getHeight(), new Paint());
            canvas.drawBitmap(bitmaps.get(3), bitmaps.get(2).getWidth(), bitmaps.get(1).getHeight(), new Paint());
        }

        return collage;
    }

}
