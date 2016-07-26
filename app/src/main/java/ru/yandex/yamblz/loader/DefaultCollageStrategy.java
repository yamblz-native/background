package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class DefaultCollageStrategy implements CollageStrategy {
    private int length;

    public DefaultCollageStrategy(int length) {
        this.length = length;
    }

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        if (bitmaps.size() == 1) {
            return Bitmap.createBitmap(bitmaps.get(0));
        }

        Bitmap newBitmap = Bitmap.createBitmap(length, length, bitmaps.get(0).getConfig());
        Canvas canvas = new Canvas(newBitmap);

        if (bitmaps.size() == 2) {
            for (int i = 0; i < 2; ++i) {
                Bitmap bitmap = bitmaps.get(0);
                Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
                Rect dst = new Rect(i * length / 2, 0, (i + 1) * length / 2, length);
                canvas.drawBitmap(bitmap, src, dst, null);
            }
        } else if (bitmaps.size() == 3) {
            Bitmap bitmap = bitmaps.get(0);
            Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
            Rect dst = new Rect(0, 0, length / 2, length);
            canvas.drawBitmap(bitmap, src, dst, null);

            for (int j = 0; j < 2; ++j) {
                bitmap = bitmaps.get(j + 1);
                src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
                dst = new Rect(length / 2, j * length / 2, length - 1, (j + 1) * length / 2);
                canvas.drawBitmap(bitmap, src, dst, null);
            }
        } else {
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    Bitmap bitmap = bitmaps.get(i * 2 + j);
                    Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
                    Rect dst = new Rect(i * length / 2, j * length / 2,
                            (i + 1) * length / 2, (j + 1) * length / 2);
                    canvas.drawBitmap(bitmap, src, dst, null);
                }
            }
        }

        return newBitmap;
    }
}
