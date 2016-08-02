package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

/**
 * Created by aleien on 31.07.16.
 *
 */

class SimpleCollageStrategy implements CollageStrategy {
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        return combineBitmaps(bitmaps);
    }

    private Bitmap combineBitmaps(List<Bitmap> bitmaps) {
        int width = 300, height = 300;
        Bitmap combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(combinedBitmap);
        Bitmap scaledBitmap;

        switch (bitmaps.size()) {
            case 1:
                return bitmaps.get(0);
            case 2:
                scaledBitmap = Bitmap.createScaledBitmap(bitmaps.get(0), width, height, true);
                comboImage.drawBitmap(scaledBitmap, 0f, 0f, null);
                scaledBitmap = Bitmap.createScaledBitmap(bitmaps.get(1), width, height, true);
                comboImage.drawBitmap(scaledBitmap, width / 2, 0f, null);
                break;
            case 3:
                scaledBitmap = Bitmap.createScaledBitmap(bitmaps.get(0), width, height, true);
                comboImage.drawBitmap(scaledBitmap, 0f, 0f, null);
                comboImage.drawBitmap(bitmaps.get(1), width / 2, 0f, null);
                comboImage.drawBitmap(bitmaps.get(2), width / 2, height / 2, null);
                break;
            default:
            case 4:
                comboImage.drawBitmap(bitmaps.get(0), 0f, 0f, null);
                comboImage.drawBitmap(bitmaps.get(1), 0f, height / 2, null);
                comboImage.drawBitmap(bitmaps.get(2), width / 2, 0f, null);
                comboImage.drawBitmap(bitmaps.get(3), width / 2, height / 2, null);
                break;
        }

        return combinedBitmap;

    }
}
