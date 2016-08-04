package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

/**
 * Created by shmakova on 30.07.16.
 */

public class StripCollageStrategy implements CollageStrategy {
    public static final int VERTICAL_STRIPES = 0;
    public static final int HORIZONTAL_STRIPES = 1;
    private int size;
    private int orientation;


    public StripCollageStrategy(int size, int orientation) {
        this.size = size;
        this.orientation = orientation;
    }

    public StripCollageStrategy(int size) {
        this.size = size;
        this.orientation = HORIZONTAL_STRIPES;
    }

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        if (bitmaps.size() < size) {
            size = bitmaps.size();
        }

        Bitmap bitmap = bitmaps.get(0);
        Bitmap.Config config = bitmap.getConfig();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap collage = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(collage);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int squareWidth = 0;
        int squareHeight = 0;

        if (orientation == VERTICAL_STRIPES) {
            squareWidth = canvasWidth / size;
            squareHeight = canvasHeight;
        } else {
            squareWidth = canvasWidth;
            squareHeight = canvasHeight / size;
        }

        Rect destinationRect = new Rect();

        int xOffset = 0;
        int yOffset = 0;

        destinationRect.set(0, 0, squareWidth, squareHeight);

        for (int i = 0; i < size; i++) {
            if (orientation == VERTICAL_STRIPES) {
                xOffset = i * squareWidth;
                yOffset = 0;
            } else {
                xOffset = 0;
                yOffset = i * squareHeight;
            }

            Rect srcRect = new Rect();
            srcRect.set(0, 0, squareWidth, squareHeight);
            srcRect.offsetTo(xOffset, yOffset);

            destinationRect.offsetTo(xOffset, yOffset);

            canvas.drawBitmap(bitmaps.get(i), srcRect, destinationRect, null);
        }

        return collage;

    }
}
