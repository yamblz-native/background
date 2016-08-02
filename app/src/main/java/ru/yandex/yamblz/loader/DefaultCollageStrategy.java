package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class DefaultCollageStrategy implements CollageStrategy {
    private final static int RESULT_MAP_SIDE = 400;

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        int bitmapsToDraw;
        int mapsInRow = getBitmapsInRow(bitmaps.size());

        bitmapsToDraw = mapsInRow * mapsInRow;

        Bitmap resultBitmap = Bitmap.createBitmap(RESULT_MAP_SIDE, RESULT_MAP_SIDE,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resultBitmap);

        int offsetX = 0;
        int offsetY = 0;
        int rowCounter = 0;
        int pieceSide = RESULT_MAP_SIDE / mapsInRow;
        for (int i = 0; i < bitmapsToDraw; i++) {
            Bitmap toDraw = Bitmap.createScaledBitmap(bitmaps.get(i), pieceSide, pieceSide, false);
            canvas.drawBitmap(toDraw, offsetX, offsetY, null);
            rowCounter++;
            if (rowCounter == mapsInRow) {
                offsetY += pieceSide;
                offsetX = 0;
                rowCounter = 0;
            } else {
                offsetX += pieceSide;
            }
        }

        return resultBitmap;
    }

    private int getBitmapsInRow(int size) {
        int mapsInRow;
        if (size >= 9) {
            mapsInRow = 3;
        } else if (size >= 4) {
            mapsInRow = 2;
        } else {
            mapsInRow = 1;
        }
        return mapsInRow;
    }
}
