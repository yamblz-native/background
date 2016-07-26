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

    private static Rect getCenteredRect(Bitmap bitmap, int widthRatio, int heightRatio) {
        int width = bitmap.getWidth();
        int height = bitmap.getWidth() * heightRatio / widthRatio;

        if (height > bitmap.getHeight()) {
            height = bitmap.getHeight();
            width = height * widthRatio / heightRatio;
            int left = (bitmap.getWidth() - width) / 2;
            return new Rect(left, 0, left + width - 1, height - 1);
        } else {
            int top = (bitmap.getHeight() - height) / 2;
            return new Rect(0, top, width - 1, top + height - 1);
        }
    }

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        Bitmap newBitmap = Bitmap.createBitmap(length, length, bitmaps.get(0).getConfig());
        Canvas canvas = new Canvas(newBitmap);
        Rect src, dst;

        switch (bitmaps.size()) {
            case 0:
                break;

            case 1:
                src = getCenteredRect(bitmaps.get(0), 1, 1);
                dst = new Rect(0, 0, newBitmap.getWidth() - 1, newBitmap.getHeight() - 1);
                canvas.drawBitmap(bitmaps.get(0), src, dst, null);
                break;

            case 2:
                for (int i = 0; i < 2; ++i) {
                    Bitmap bitmap = bitmaps.get(i);
                    src = getCenteredRect(bitmap, 1, 2);
                    dst = new Rect(i * length / 2, 0, (i + 1) * length / 2, length);
                    canvas.drawBitmap(bitmap, src, dst, null);
                }
                break;

            case 3: {
                Bitmap bitmap = bitmaps.get(0);
                src = getCenteredRect(bitmap, 1, 2);
                dst = new Rect(0, 0, length / 2, length);
                canvas.drawBitmap(bitmap, src, dst, null);

                for (int j = 0; j < 2; ++j) {
                    bitmap = bitmaps.get(j + 1);
                    src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
                    dst = new Rect(length / 2, j * length / 2, length - 1, (j + 1) * length / 2);
                    canvas.drawBitmap(bitmap, src, dst, null);
                }
                break;
            }

            default:
                for (int i = 0; i < 2; ++i) {
                    for (int j = 0; j < 2; ++j) {
                        Bitmap bitmap = bitmaps.get(i * 2 + j);
                        src = getCenteredRect(bitmap, 1, 1);
                        dst = new Rect(i * length / 2, j * length / 2,
                                (i + 1) * length / 2, (j + 1) * length / 2);
                        canvas.drawBitmap(bitmap, src, dst, null);
                    }
                }
                break;
        }

        return newBitmap;
    }
}
