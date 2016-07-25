package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Composes images in square table NxN
 */
public class TableCollageStrategy implements CollageStrategy {

    private void checkConditions(List<Bitmap> bitmaps) {
        if (bitmaps.size() == 0) {
            throw new IllegalArgumentException("Empty bitmap list");
        }
    }

    private int meanWidth(List<Bitmap> bitmaps) {
        int result = 0;
        for(Bitmap bitmap : bitmaps) {
            result += bitmap.getWidth();
        }
        return result / bitmaps.size();
    }

    private int meanHeight(List<Bitmap> bitmaps) {
        int result = 0;
        for(Bitmap bitmap : bitmaps) {
            result += bitmap.getHeight();
        }
        return result / bitmaps.size();
    }

    private void transformToEqualSizes(List<Bitmap> bitmaps) {
        int width = meanWidth(bitmaps);
        int height = meanHeight(bitmaps);
        for(int i = 0; i < bitmaps.size(); i++) {
            bitmaps.set(i, Bitmap.createScaledBitmap(bitmaps.get(i), width, height, false));
        }
    }

    private Bitmap collage(List<Bitmap> bitmaps) {
        int sqrt = (int)Math.sqrt(bitmaps.size());

        int width = bitmaps.get(0).getWidth();
        int height = bitmaps.get(0).getHeight();

        int totalWidth = sqrt * width;
        int totalHeight = sqrt * height;

        Bitmap result = Bitmap.createBitmap(totalWidth, totalHeight, bitmaps.get(0).getConfig());
        Canvas canvas = new Canvas(result);
        int left, top;
        left = top = 0;
        for(int i = 0; i < sqrt; i++) {
            for(int j = 0; j < sqrt; j++) {
                Bitmap bitmap = bitmaps.get(i * sqrt + j);
                canvas.drawBitmap(bitmap, left, top, null);
                if(j == sqrt - 1) {
                    left = 0;
                    top += height;
                } else {
                    left += width;
                }
            }
        }
        return result;
    }

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        checkConditions(bitmaps);

        bitmaps = new ArrayList<>(bitmaps);
        transformToEqualSizes(bitmaps);

        return collage(bitmaps);
    }
}
