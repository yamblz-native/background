package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Simple {@link CollageStrategy}.
 * Assumes that all given images have the same size and are square.
 */
public class SquareCollageStrategy implements CollageStrategy {

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        if (bitmaps == null || bitmaps.isEmpty()) {
            return null;
        }

        Collections.shuffle(bitmaps);

        if (bitmaps.size() < 4) {
            return bitmaps.get(0);
        }

        int imgPerSide = (int) Math.sqrt(bitmaps.size());

        return createSquareCollage(bitmaps, imgPerSide);
    }


    private Bitmap createSquareCollage(List<Bitmap> bitmaps, int imgPerSide) {
        int totalSize = bitmaps.get(0).getWidth();
        int imgSize = totalSize / imgPerSide;

        Bitmap collage = Bitmap.createBitmap(totalSize, totalSize, ARGB_8888);
        Canvas canvas = new Canvas(collage);

        Iterator<Bitmap> iterator = bitmaps.iterator();
        for (int row = 0; row < imgPerSide; row++) {
            for (int col = 0; col < imgPerSide; col++) {
                int left = col * imgSize;
                int top = row * imgSize;
                int right = totalSize - (imgPerSide - 1 - col) * imgSize;
                int bottom = totalSize - (imgPerSide - 1 - row) * imgSize;
                canvas.drawBitmap(iterator.next(), null, new Rect(left, top, right, bottom), null);
            }
        }

        return collage;
    }
}
