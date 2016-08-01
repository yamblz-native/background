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

        int sectionsPerSide = (int) Math.sqrt(bitmaps.size());

        return createSquareCollage(bitmaps, sectionsPerSide);
    }


    private Bitmap createSquareCollage(List<Bitmap> bitmaps, int sectionsPerSide) {
        int collageSize = bitmaps.get(0).getWidth();
        int sectionSize = collageSize / sectionsPerSide;

        Bitmap collage = Bitmap.createBitmap(collageSize, collageSize, ARGB_8888);
        Canvas canvas = new Canvas(collage);

        Iterator<Bitmap> iterator = bitmaps.iterator();
        for (int row = 0; row < sectionsPerSide; row++) {
            for (int col = 0; col < sectionsPerSide; col++) {
                int left = col * sectionSize;
                int top = row * sectionSize;
                int right = collageSize - (sectionsPerSide - 1 - col) * sectionSize;
                int bottom = collageSize - (sectionsPerSide - 1 - row) * sectionSize;
                canvas.drawBitmap(iterator.next(), null, new Rect(left, top, right, bottom), null);
            }
        }

        return collage;
    }
}
