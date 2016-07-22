package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class TableCollageStrategy implements CollageStrategy {

    private int mRows, mCols;

    public TableCollageStrategy(int rows, int cols) {
        this.mRows = rows;
        this.mCols = cols;
    }

    private void checkConditions(List<Bitmap> bitmaps) {
        if (bitmaps.size() == 0) {
            throw new IllegalArgumentException("Empty bitmap list");
        }
        if(bitmaps.size() != mRows * mCols) {
            throw new IllegalArgumentException("Number of bitmaps not equals to number of cells");
        }
        int width, height;
        {
            Bitmap bitmap = bitmaps.get(0);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        for(Bitmap bitmap : bitmaps) {
            if(bitmap.getWidth() != width || bitmap.getHeight() != height) {
                throw new IllegalArgumentException("Images should have the same size");
            }
        }
    }

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        int width = bitmaps.get(0).getWidth();
        int height = bitmaps.get(0).getHeight();
        int totalWidth = width * mCols;
        int totalHeight = height * mRows;

        Bitmap result = Bitmap.createBitmap(totalWidth, totalHeight, bitmaps.get(0).getConfig());
        Canvas canvas = new Canvas(result);
        int left, top;
        left = top = 0;
        for(int i = 0; i < mRows; i++) {
            for(int j = 0; j < mCols; j++) {
                Bitmap bitmap = bitmaps.get(i * mCols + j);
                canvas.drawBitmap(bitmap, left, top, null);
                if(j == mCols - 1) {
                    left = 0;
                    top += height;
                } else {
                    left += width;
                }
            }
        }
        return result;
    }
}
