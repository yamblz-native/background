package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class DefaultCollageStrategy implements CollageStrategy {
    private final static int PIECE_SIDE = 300;
    private final static int PIECES_IN_A_ROW = 2;

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {

        int bitmapsCount = bitmaps.size();
        Bitmap resultBitmap = Bitmap.createBitmap(PIECE_SIDE * PIECES_IN_A_ROW,
                PIECE_SIDE * (bitmapsCount / PIECES_IN_A_ROW + 1),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resultBitmap);

        int offsetX = 0;
        int offsetY = 0;
        int rowCounter = 0;
        for (int i = 0; i < bitmapsCount; i++) {
            canvas.drawBitmap(bitmaps.get(i), offsetX, offsetY, null);
            rowCounter++;
            if (rowCounter == PIECES_IN_A_ROW) {
                offsetY += PIECE_SIDE;
                offsetX = 0;
                rowCounter = 0;
            } else {
                offsetX += PIECE_SIDE;
            }
        }
        return resultBitmap;
    }
}
