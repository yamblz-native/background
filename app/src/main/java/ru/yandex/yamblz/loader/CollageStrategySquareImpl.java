package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by dalexiv on 8/9/16.
 */

public class CollageStrategySquareImpl implements CollageStrategy {
    private static final int COLLAGE_SIZE = 300;
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {

        if (bitmaps.size() < 4)
            return bitmaps.get(0);
        final int imagesInAxe = (int) Math.sqrt(bitmaps.size());
        final int imageSize = COLLAGE_SIZE / imagesInAxe;
        final Bitmap outputBitmap = Bitmap.createBitmap(COLLAGE_SIZE, COLLAGE_SIZE,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);

        for (int i = 0; i < imagesInAxe; ++i) {
            for (int k = 0; k < imagesInAxe; ++k) {
                canvas.drawBitmap(Bitmap.createScaledBitmap(
                        bitmaps.get(i * imagesInAxe + k), imageSize, imageSize, false),
                        i * imageSize, k * imageSize, null);
            }
        }
        return outputBitmap;
    }
}
